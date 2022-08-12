package me.despical.throwableitems.event;

import me.despical.commons.compat.XMaterial;
import me.despical.throwableitems.Main;
import me.despical.throwableitems.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public class AnimationEventsClass extends ListenerAdapter {

	public AnimationEventsClass(Main plugin) {
		super (plugin);
	}

	@EventHandler
	public void onThrow(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

		spawnTardis(event.getPlayer());
	}

	public void spawnTardis(Player player){
		Location startLoc = player.getEyeLocation();
		Location particleLoc = startLoc.clone();
		World world = startLoc.getWorld();

		ArmorStand stand = (ArmorStand) world.spawnEntity(startLoc, EntityType.ARMOR_STAND);
		stand.setHelmet(XMaterial.BLUE_STAINED_GLASS.parseItem());
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setSmall(true);

		new BukkitRunnable() {
			final double t = Math.PI / 8;
			Vector vec = new Vector(Math.cos(t), 0, Math.sin(t));

			int beamLength = 0;
			Entity target = null;
			double targetHeight = 0.0;

			int ticks = 0;
			int ticksPerParticle = 5;

			public void run() {
				vec = Utils.rotateAroundAxisY(vec, 10);
				stand.setHeadPose(new EulerAngle(Math.toRadians(vec.getX()), vec.getZ(), Math.toRadians(vec.getZ())));

				ticks++;

				if (ticks == ticksPerParticle) {
					ticks = 0;

					for (Entity entity : world.getNearbyEntities(particleLoc, 5, 5, 5)) {
						if (entity instanceof LivingEntity) {
							if (entity instanceof Player || entity instanceof ArmorStand) {
								continue;
							}

							Vector particleMinVector = new Vector(particleLoc.getX() - 0.25, particleLoc.getY() - 0.25, particleLoc.getZ() - 0.25);
							Vector particleMaxVector = new Vector(particleLoc.getX() + 0.25, particleLoc.getY() + 0.25, particleLoc.getZ() + 0.25);

							if (entity.getBoundingBox().overlaps(particleMinVector, particleMaxVector)) {
								world.spawnParticle(Particle.FLASH, particleLoc, 0);
								world.playSound(particleLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

								entity.setVelocity(entity.getVelocity().add(particleLoc.getDirection().normalize().multiply(1.5)));

								((Damageable) entity).damage(5, player);
								cancel();
								return;
							}
						}
					}

					Vector vecOffset;

					if (beamLength >= 6) {
						if (target == null || target.isDead()) {
							for (Entity entity : world.getNearbyEntities(particleLoc, 5, 5, 5)) {
								if (entity instanceof LivingEntity) {
									if (entity instanceof Player || entity instanceof ArmorStand) {
										continue;
									}

									target = entity;
									targetHeight = target.getHeight();
									String targetName = target.getType().toString();

									if (!target.isDead()) {
										targetEntity(entity, particleLoc.clone(), world, player);
										target = null;
										ticksPerParticle = 25;
									} else {
										ticksPerParticle = 5;
									}


									player.sendMessage("Target Locked! (" + targetName + ")");
									// break varsa tekli saldırı
								}
							}
						}
					}

						vecOffset = particleLoc.getDirection().clone().multiply(0);
//						vecOffset = dir.clone().multiply(Math.PI / 3);

					beamLength++;

//					if (beamLength >= maxBeamLength) {
//						world.spawnParticle(Particle.FLASH, particleLoc, 0);
//						this.cancel();
//						return;
//					}

					particleLoc.add(vecOffset);

					world.spawnParticle(Particle.PORTAL, particleLoc, 0);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	public void targetEntity(Entity entity, Location particleLoc, World world, Player player) {
		new BukkitRunnable() {

			final int maxBeamLength = 30;
			int beamLength = 0;
			final Entity target = entity;
			final double targetHeight = entity.getHeight();

			int ticks = 0;
			int ticksPerParticle = 3;

			public void run() {
				ticks++;
				if (ticks == ticksPerParticle) {
					ticks = 0;

					if (entity instanceof LivingEntity) {
						if (entity instanceof Player || entity instanceof ArmorStand) {
							return;
						}

						Vector particleMinVector = new Vector(particleLoc.getX() - 0.25, particleLoc.getY() - 0.25, particleLoc.getZ() - 0.25);
						Vector particleMaxVector = new Vector(particleLoc.getX() + 0.25, particleLoc.getY() + 0.25, particleLoc.getZ() + 0.25);

						if (entity.getBoundingBox().overlaps(particleMinVector, particleMaxVector)) {
							world.spawnParticle(Particle.FLASH, particleLoc, 0);
							world.playSound(particleLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

							entity.setVelocity(entity.getVelocity().add(particleLoc.getDirection().normalize().multiply(1.5)));

							((Damageable) entity).damage(5, player);
							cancel();
							return;
						}
					}

					Vector vecOffset;

					if (target != null) {
						Location targetLoc = target.getLocation().clone().add(0, targetHeight / 2, 0);

						Vector particleDirection = particleLoc.getDirection();
						Vector inBetween = targetLoc.clone().subtract(particleLoc).toVector().normalize();

						double accuracy = 0.5;
						double distance = particleLoc.distance(targetLoc);

						if (distance < 5) {
							ticksPerParticle = 2;
							ticks = 0;

							accuracy = accuracy * Math.pow(0.6, distance) + 0.5;
						}

						if (distance < 3) {
							ticksPerParticle = 1;
							ticks = 0;
						}

						inBetween.multiply(accuracy);
						particleDirection.add(inBetween).normalize();
						vecOffset = particleDirection.clone();
						particleLoc.setDirection(particleDirection);
					} else {
						vecOffset = particleLoc.getDirection().clone();
//						vecOffset = dir.clone().multiply(Math.PI / 3);
					}

					beamLength++;

					if (beamLength >= maxBeamLength) {
						world.spawnParticle(Particle.FLASH, particleLoc, 0);
						this.cancel();
						return;
					}

					particleLoc.add(vecOffset);

					world.spawnParticle(Particle.CLOUD, particleLoc, 0);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	private void createAnimation(Location origin) {
		List<ArmorStand> stands = new ArrayList<>();
		for (int i = 0; i <= 16; i++) {
			ArmorStand stand = (ArmorStand) origin.getWorld().spawnEntity(origin, EntityType.ARMOR_STAND);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setSmall(true);
			stand.setHelmet(XMaterial.WHITE_STAINED_GLASS.parseItem());

			stands.add(stand);
		}

		new BukkitRunnable() {

			double t = 0;
			final double r = 2;

			public void run() {
				for (ArmorStand stand : stands) {
					t = t + Math.PI / 8;
					double x = r * Math.cos(t);
					double z = r * Math.sin(t);
					Vector v = new Vector(x, 0, z);
					v = Utils.rotateAroundAxisY(v, 10);
					origin.add(v.getX(), v.getY(), v.getZ());

					origin.getWorld().spawnParticle(Particle.HEART, origin, 1);
					stand.teleport(origin);
					stand.setHeadPose(new EulerAngle(Math.toRadians(v.getX()), v.getZ(), Math.toRadians(v.getZ())));

					origin.subtract(v.getX(), v.getY(), v.getZ());

//					if (t > Math.PI * 32) {
//						this.cancel();
//					}
				}
			}

		}.runTaskTimer(plugin, 0, 2);
	}
}