package me.despical.throwableitems.skills;

import me.despical.throwableitems.Main;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public abstract class Skill {

	protected final Main plugin;

	public Skill(Main plugin) {
		this.plugin = plugin;
	}

	public abstract void onUse(Player player);

	public abstract int slot();
}