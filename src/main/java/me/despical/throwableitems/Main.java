package me.despical.throwableitems;

import me.despical.commandframework.CommandFramework;
import me.despical.commons.compat.VersionResolver;
import me.despical.commons.exception.ExceptionLogHandler;
import me.despical.commons.util.Collections;
import me.despical.commons.util.JavaVersion;
import me.despical.commons.util.LogUtils;
import me.despical.throwableitems.event.ChatEvent;
import me.despical.throwableitems.event.SkillEvents;
import me.despical.throwableitems.event.ThrowEvent;
import me.despical.throwableitems.recipe.RecipeManager;
import me.despical.throwableitems.skills.SkillHandler;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class Main extends JavaPlugin {

	private boolean forceDisable;
	private ExceptionLogHandler exceptionLogHandler;
	private CommandFramework commandFramework;
	private ConfigPreferences configPreferences;
	private RecipeManager recipeManager;
	private SkillHandler skillHandler;

	@Override
	public void onEnable() {
		this.configPreferences = new ConfigPreferences(this);

//		if ((forceDisable = !validateIfPluginShouldStart())) {
//			getServer().getPluginManager().disablePlugin(this);
//			return;
//		}

		if (configPreferences.getOption(ConfigPreferences.Option.DEBUG_MODE)) {
			LogUtils.setLoggerName("ThrowableItems");
			LogUtils.enableLogging();
			LogUtils.log("Initialization started!");
		}

		exceptionLogHandler = new ExceptionLogHandler(this);
		exceptionLogHandler.setMainPackage("me.despical");
		exceptionLogHandler.addBlacklistedClass("me.despical.throwableitems.user.data.MysqlManager", "me.despical.commons.database.MysqlDatabase");
		exceptionLogHandler.setRecordMessage("[ThrowableItems] We have found a bug in the code. Contact us at our official Discord server (link: https://discord.gg/rVkaGmyszE) with the following error given above!");

		long start = System.currentTimeMillis();

		setupFiles();
		initializeClasses();

		LogUtils.log("Initialization finished took {0} ms.", System.currentTimeMillis() - start);
	}

	private boolean validateIfPluginShouldStart() {
		if (!VersionResolver.isCurrentBetween(VersionResolver.ServerVersion.v1_8_R1, VersionResolver.ServerVersion.v1_19_R1)) {
			LogUtils.sendConsoleMessage("[ThrowableItems] &cYour server version is not supported by King of the Ladder!");
			LogUtils.sendConsoleMessage("[ThrowableItems] &cSadly, we must shut off. Maybe you consider changing your server version?");
			return false;
		}

		if (JavaVersion.getCurrentVersion().isAt(JavaVersion.JAVA_8)) {
			LogUtils.sendConsoleMessage("[ThrowableItems] &cThis plugin won't support Java 8 in future updates.");
			LogUtils.sendConsoleMessage("[ThrowableItems] &cSo, maybe consider to update your version, right?");
		}

		try {
			Class.forName("org.spigotmc.SpigotConfig");
		} catch (Exception e) {
			LogUtils.sendConsoleMessage("[ThrowableItems] &cYour server software is not supported by King of the Ladder!");
			LogUtils.sendConsoleMessage("[ThrowableItems] &cWe support only Spigot and Spigot forks only! Shutting off...");
			return false;
		}

		return true;
	}

	@Override
	public void onDisable() {
		if (forceDisable) return;

		LogUtils.log("System disable initialized.");
		long start = System.currentTimeMillis();

		getServer().getLogger().removeHandler(exceptionLogHandler);

		LogUtils.log("System disable finished took {0} ms.", System.currentTimeMillis() - start);
		LogUtils.disableLogging();
	}

	private void initializeClasses() {
//		this.commandFramework = new CommandFramework(this);
		this.recipeManager = new RecipeManager(this);
		this.skillHandler = new SkillHandler(this);

		new ChatEvent(this);
		new ThrowEvent(this);
		new SkillEvents(this);

		startPluginMetrics();
	}

	private void startPluginMetrics() {
		Metrics metrics = new Metrics(this, 7938);

		metrics.addCustomChart(new SimplePie("update_notifier", () -> configPreferences.getOption(ConfigPreferences.Option.UPDATE_NOTIFIER_ENABLED) ? "Enabled" : "Disabled"));
	}

	private void setupFiles() {
		Collections.streamOf("messages").filter(name -> !new File(getDataFolder(),name + ".yml").exists()).forEach(name -> saveResource(name + ".yml", false));
	}

	public ConfigPreferences getConfigPreferences() {
		return configPreferences;
	}

	public CommandFramework getCommandFramework() {
		return commandFramework;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public SkillHandler getSkillHandler() {
		return skillHandler;
	}
}