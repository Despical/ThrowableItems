package me.despical.throwableitems.event;

import me.despical.throwableitems.Main;
import me.despical.throwableitems.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public class SkillEvents extends ListenerAdapter {

	public SkillEvents(Main plugin) {
		super( plugin);
	}

	@EventHandler
	public void onSkillUsage(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		Player player = event.getPlayer();

		for (Skill skill : plugin.getSkillHandler().getSkills()) {
			if (skill.slot() == player.getInventory().getHeldItemSlot()) {
				skill.onUse(player);
				break;
			}
		}
	}
}
