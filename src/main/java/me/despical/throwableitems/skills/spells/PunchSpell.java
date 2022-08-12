package me.despical.throwableitems.skills.spells;

import me.despical.commons.compat.XMaterial;
import me.despical.throwableitems.Main;
import me.despical.throwableitems.skills.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public class PunchSpell extends Skill {

	public PunchSpell(Main plugin) {
		super (plugin);
	}

	@Override
	public void onUse(Player player) {
		Location startLoc = player.getEyeLocation();
		Location particleLoc = startLoc.clone();
		World world = startLoc.getWorld();

		new BukkitRunnable() {

			Entity target = null;
			int ticks = 0, ticksPerParticle = 3, beamLength = 0;
			final int maxBeamLength = 30;

			public void run() {
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

									player.sendActionBar("Target locked: " + target.getType().toString());
									break;
								}
							}
						}
					}

					if (target != null) {
						Location targetLoc = target.getLocation().clone().add(0, target.getHeight() / 2, 0);

						Vector particleDirection = particleLoc.getDirection();
						Vector inBetween = targetLoc.clone().subtract(particleLoc).toVector().normalize();

						double accuracy = 0.5, distance = particleLoc.distance(targetLoc);

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
					}

					beamLength++;

					if (beamLength >= maxBeamLength) {
						world.spawnParticle(Particle.FLASH, particleLoc, 0);
						this.cancel();
						return;
					}

					particleLoc.add(vecOffset);

					ArmorStand stand = createArmorStand(particleLoc.clone().add(0, -.65, 0));

					plugin.getServer().getScheduler().runTaskLater(plugin, stand::remove, 25);

					world.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc.clone().subtract(vecOffset), 0);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	private ArmorStand createArmorStand(Location loc) {
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setSmall(true);
		stand.setHelmet(XMaterial.BLUE_STAINED_GLASS.parseItem());
		return stand;
	}

	@Override
	public int slot() {
		return 0;
	}
}