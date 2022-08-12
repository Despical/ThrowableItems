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
public class DirectPunchSpell extends Skill {

	public DirectPunchSpell(Main plugin) {
		super (plugin);
	}

	@Override
	public void onUse(Player player) {
		Entity target = player.getTargetEntity(40);

		if (target == null) {
			player.sendActionBar("No target found!");
			return;
		}

		Location startLoc = player.getEyeLocation();
		Location standLoc = startLoc.clone().subtract(0, -.5, 0);
		World world = startLoc.getWorld();

		new BukkitRunnable() {

			int beamLength = 0;
			final int maxBeamLength = 30;

			@Override
			public void run() {
				Vector vecOffset;
				Location targetLoc = target.getLocation().clone().add(0, target.getHeight() / 2, 0);

				Vector standDirection = standLoc.getDirection();
				Vector inBetween = targetLoc.clone().subtract(standLoc).toVector().normalize();

				for (Entity entity : world.getNearbyEntities(standLoc, 5, 5, 5)) {
					if (entity instanceof LivingEntity) {
						if (entity instanceof Player || entity instanceof ArmorStand || !target.equals(entity)) {
							continue;
						}

						Vector particleMinVector = new Vector(standLoc.getX() - 0.25, standLoc.getY() - 0.25, standLoc.getZ() - 0.25);
						Vector particleMaxVector = new Vector(standLoc.getX() + 0.25, standLoc.getY() + 0.25, standLoc.getZ() + 0.25);

						if (entity.getBoundingBox().overlaps(particleMinVector, particleMaxVector)) {
							world.spawnParticle(Particle.FLASH, standLoc, 0);
							world.playSound(standLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

							entity.setVelocity(entity.getVelocity().add(standLoc.getDirection().normalize().multiply(1.5)));

							((Damageable) entity).damage(5, player);
							cancel();
							return;
						}
					}
				}

				standDirection.add(inBetween).normalize();
				vecOffset = standDirection.clone();
				standLoc.setDirection(standDirection);

				beamLength++;

				if (beamLength >= maxBeamLength) {
					world.spawnParticle(Particle.FLASH, standLoc, 0);
					this.cancel();
					return;
				}

				standLoc.add(vecOffset);

				ArmorStand stand = createArmorStand(standLoc.clone().add(0, -.65, 0));

				plugin.getServer().getScheduler().runTaskLater(plugin, stand::remove, 10);
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
		return 1;
	}
}