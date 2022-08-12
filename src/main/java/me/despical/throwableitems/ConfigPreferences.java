package me.despical.throwableitems;

import me.despical.commons.string.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class ConfigPreferences {

	private final Map<Option, Boolean> options;

	ConfigPreferences(Main plugin) {
		this.options = new HashMap<>();

		plugin.saveDefaultConfig();

		for (Option option : Option.values()) {
			options.put(option, plugin.getConfig().getBoolean(option.path, option.def));
		}
	}

	public boolean getOption(Option option) {
		return options.get(option);
	}

	public enum Option {

		DEBUG_MODE(false), UPDATE_NOTIFIER_ENABLED(false);

		String path;
		boolean def;

		Option() {
			this(true);
		}

		Option(boolean def) {
			this.def = def;
			this.path = StringUtils.capitalize(name().replace('_', '-').toLowerCase(), '-', '.');
		}
	}
}