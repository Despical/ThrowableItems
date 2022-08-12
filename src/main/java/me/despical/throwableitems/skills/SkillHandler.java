package me.despical.throwableitems.skills;

import me.despical.throwableitems.Main;
import me.despical.throwableitems.skills.spells.DirectPunchSpell;
import me.despical.throwableitems.skills.spells.PunchSpell;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public class SkillHandler {

	private final Set<Skill> skills;

	public SkillHandler(Main plugin) {
		this.skills = new HashSet<>();

		registerSkill(new PunchSpell(plugin));
		registerSkill(new DirectPunchSpell(plugin));
	}

	public void registerSkill(Skill skill) {
		this.skills.add(skill);
	}

	public Set<Skill> getSkills() {
		return new HashSet<>(skills);
	}
}