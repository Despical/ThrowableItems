package me.despical.throwableitems.util;

import me.despical.commons.ReflectionUtils;
import me.despical.commons.util.Collections;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class Utils {

	private static final Set<String> FLOWERS = Collections.setOf("DEAD_BUSH", "DANDELION", "POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET",
			"TULIP", "DAISY", "FLOWER", "LILY", "ROSE", "BLOSSOM", "MUSHROOM", "FUNGUS", "ROOTS", "SPROUTS", "VINE");

	public static Vector rotateAroundAxisX(Vector v, double angle) {
		angle = Math.toRadians(angle);
		double
				cos = Math.cos(angle),
				sin = Math.sin(angle),
				y = v.getY() * cos - v.getZ() * sin,
				z = v.getY() * sin + v.getZ() * cos;

		return v.setY(y).setZ(z);
	}

	public static Vector rotateAroundAxisY(Vector v, double angle) {
		angle = -angle;
		angle = Math.toRadians(angle);
		double
				cos = Math.cos(angle),
				sin = Math.sin(angle),
				x = v.getX() * cos + v.getZ() * sin,
				z = v.getX() * -sin + v.getZ() * cos;

		return v.setX(x).setZ(z);
	}

	public static void ringTheBell(Player player, Block block) {
		World world = block.getWorld();
		world.playSound(block.getLocation(), Sound.BLOCK_BELL_USE, 3.0f, 1.0f);
		BlockFace facing = ((Directional) block.getBlockData()).getFacing();

		byte face = 2;

		if (facing == BlockFace.SOUTH) {
			face = 3;
		} else if (facing == BlockFace.WEST) {
			face = 4;
		} else if (facing == BlockFace.EAST) {
			face = 5;
		}

		try {
			Object blockPosition = ReflectionUtils.getNMSClass("core", "BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ());
			Object bellBlock = ReflectionUtils.getNMSClass("world.level.block", "Blocks").getDeclaredField("mO").get(null);
			Object packet = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutBlockAction").getConstructor(ReflectionUtils.getNMSClass("core", "BlockPosition"), ReflectionUtils.getNMSClass("world.level.block", "Block"), int.class, int.class).newInstance(blockPosition, bellBlock, 1, face);

			ReflectionUtils.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Location getLocationBehind(Location location) {
		Vector inverse = location.clone().getDirection().normalize().multiply(-1);

		return location.add(inverse);
	}

	public static boolean isFlower(Block block) {
		return FLOWERS.stream().anyMatch(name -> block.getType().name().contains(name));
	}
}