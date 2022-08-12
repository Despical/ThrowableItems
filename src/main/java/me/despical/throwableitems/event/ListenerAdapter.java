package me.despical.throwableitems.event;

import me.despical.throwableitems.Main;
import org.bukkit.event.Listener;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public abstract class ListenerAdapter implements Listener {

	protected final Main plugin;

	public ListenerAdapter(Main plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}