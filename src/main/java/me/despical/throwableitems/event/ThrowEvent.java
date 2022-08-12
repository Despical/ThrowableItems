package me.despical.throwableitems.event;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.despical.throwableitems.Main;
import me.despical.throwableitems.task.FlyingItemTask;
import me.despical.throwableitems.task.FlyingSwordItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class ThrowEvent extends ListenerAdapter {

	public ThrowEvent(Main plugin) {
		super (plugin);
	}

	@EventHandler
	public void onThrow(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

		ItemStack item = event.getItem();
		NBTItem nbtItem = new NBTItem(item, true);
		boolean throwableItem = nbtItem.getBoolean("ThrowableItem");

		if (!throwableItem) return;

		FlyingItemTask task = getTask(item);

		if (task != null) task.createArmorStand(item, event.getPlayer());
	}

	private FlyingItemTask getTask(ItemStack itemStack) {
		String name = itemStack.getType().name();

		if (name.contains("SWORD")) {
			return new FlyingSwordItem(plugin);
		}

		return null;
	}
}