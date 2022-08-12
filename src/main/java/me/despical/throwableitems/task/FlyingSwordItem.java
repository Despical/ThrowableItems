package me.despical.throwableitems.task;

import me.despical.commons.compat.XSound;
import me.despical.throwableitems.Main;
import me.despical.throwableitems.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class FlyingSwordItem extends FlyingItemTask {

	private final int maxRange;
	private final double maxHitRange;

	public FlyingSwordItem(Main plugin) {
		super (plugin);
		this.maxRange = plugin.getConfig().getInt("Flying-Sword-Range", 30);
		this.maxHitRange = plugin.getConfig().getDouble("Flying-Sword-Hit-Range", 0.5);
	}

	@Override
	public void createArmorStand(ItemStack itemStack, Player player) {
		Location origin = player.getLocation().clone();
		World world = origin.getWorld();

		Location standStart = Utils.rotateAroundAxisY(new Vector(1.0D, 0.0D, 0.0D), origin.getYaw()).toLocation(world).add(origin);
		standStart.setYaw(origin.getYaw());

		stand = (ArmorStand) origin.getWorld().spawnEntity(standStart, EntityType.ARMOR_STAND);
		stand.setRightArmPose(new EulerAngle(Math.toRadians(350.0), Math.toRadians(origin.getPitch() * -1.0), Math.toRadians(90.0)));
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setRemoveWhenFarAway(true);

		setMarker();
		setSilent();
		setCollidable();
		setInvulnerable();
		setItemInHand(itemStack);

		Location initialise = Utils.rotateAroundAxisY(new Vector(-1.0D, 1.45D, 0.0D), origin.getYaw()).toLocation(world).add(standStart).add(Utils.rotateAroundAxisY(Utils.rotateAroundAxisX(new Vector(0.0D, 0.0D, 1.0D), origin.getPitch()), origin.getYaw()));
		Vector vec = origin.getDirection();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (stand.isInWaterOrRainOrBubbleColumn()) vec.setY(vec.getY() - .01);

				vec.normalize().multiply(stand.isInWaterOrRainOrBubbleColumn() ? .25 : 0.65);

				stand.teleport(standStart.add(vec));

				initialise.add(vec);
				initialise.getWorld().getNearbyEntities(initialise, maxHitRange, maxHitRange, maxHitRange).forEach(entity -> {
					if (entity instanceof LivingEntity) {
						cancel();
						stand.remove();

						((LivingEntity) entity).damage(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
					}
				});

				Block block = initialise.getBlock();
				Material material = block.getType();
				String name = material.name();

				if (name.contains("TORCH")) {
					block.breakNaturally();

					XSound.BLOCK_WOOD_BREAK.play(initialise, 3, 3);
				}

				if (block.getBlockData() instanceof Ageable) {
					block.breakNaturally();

					XSound.BLOCK_CROP_BREAK.play(initialise, 3, 3);
				}

				if (name.equals("GRASS") || name.equals("TALL_GRASS")) {
					block.breakNaturally();

					XSound.BLOCK_GRASS_BREAK.play(initialise, 3, 3);
				}

				player.sendMessage(name);

				if (Utils.isFlower(block)) {
					block.breakNaturally();

					XSound sound = XSound.BLOCK_CHORUS_FLOWER_DEATH;

					if (name.contains("vine")) {
						sound = XSound.BLOCK_WEEPING_VINES_BREAK;
					} else if (name.contains("fungus")) {
						sound = XSound.BLOCK_FUNGUS_BREAK;
					}

					sound.play(initialise, 3, 3);
				}

				if (name.equalsIgnoreCase("lava")) {
					cancel();
					stand.remove();

					XSound.BLOCK_LAVA_EXTINGUISH.play(initialise, 3, 3);
					return;
				}

				if (name.equals("NETHER_PORTAL")) {
					cancel();
					stand.remove();

					block.setType(Material.AIR);

					XSound.BLOCK_GLASS_BREAK.play(initialise, 3, 3);
					return;
				}

				if (block.isSolid()) {
					cancel();
					stand.remove();

					block.getWorld().dropItemNaturally(Utils.getLocationBehind(initialise).add(0, 1, 0), itemStack);

					XSound.ENTITY_ITEM_BREAK.play(initialise, 3, 3);
					return;
				}

				Block nextBlock = initialise.clone().add(vec).getBlock();
				Material nextMaterial = nextBlock.getType();

				if (nextMaterial.isSolid()) {
					if (nextMaterial.name().contains("GLASS")) {
						nextBlock.setType(Material.AIR);

						XSound.BLOCK_GLASS_BREAK.play(initialise, 3, 3);
					}

					if (nextMaterial == Material.BELL) {
						Utils.ringTheBell(player, nextBlock);
					}
				}

				if (origin.distance(initialise) > maxRange) {
					cancel();
					stand.remove();

					block.getWorld().dropItemNaturally(initialise, itemStack);
					player.sendMessage("Max range'e ulaşıldı.");
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}