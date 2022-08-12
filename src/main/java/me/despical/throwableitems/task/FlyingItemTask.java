package me.despical.throwableitems.task;

import me.despical.commons.compat.VersionResolver;
import me.despical.throwableitems.Main;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public abstract class FlyingItemTask {

	protected final Main plugin;

	protected ArmorStand stand;
	protected int blocksBroken = 0;

	public FlyingItemTask(Main plugin) {
		this.plugin = plugin;
	}

	public abstract void createArmorStand(ItemStack itemStack, Player player);

	protected void setItemInHand(ItemStack stack) {
		if (stand.getEquipment() == null) {
			return;
		}

		if (VersionResolver.isCurrentEqualOrLower(VersionResolver.ServerVersion.v1_8_R3)) {
			stand.getEquipment().setItemInHand(stack);
			return;
		}

		stand.getEquipment().setItemInMainHand(stack);
	}

	protected void setCollidable() {
		if (VersionResolver.isCurrentHigher(VersionResolver.ServerVersion.v1_8_R3)) {
			stand.setCollidable(false);
		}
	}

	protected void setMarker() {
		if (VersionResolver.isCurrentEqualOrHigher(VersionResolver.ServerVersion.v1_8_R3)) {
			stand.setMarker(true);
		}
	}

	protected void setSilent() {
		if (VersionResolver.isCurrentEqualOrHigher(VersionResolver.ServerVersion.v1_8_R3)) {
			stand.setSilent(true);
		}
	}

	protected void setInvulnerable() {
		if (VersionResolver.isCurrentEqualOrHigher(VersionResolver.ServerVersion.v1_8_R3)) {
			stand.setInvulnerable(true);
		}
	}

	protected void runLater(Runnable runnable, long time) {
		plugin.getServer().getScheduler().runTaskLater(plugin, runnable, time);
	}
}