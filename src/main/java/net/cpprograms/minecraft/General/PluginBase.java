package net.cpprograms.minecraft.General;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin base for all of my plugins. Automates some stuff, and makes it easier to manage for me.
 *
 */
public class PluginBase extends JavaPlugin {

	/**
	 * The name of this plugin.
	 */
	String pluginName = "";

	/**
	 * The version of this plugin.
	 */
	String pluginVersion = "";

	/**
	 * Debugging mode - determines whether to send debugging messages.
	 */
	boolean debugMode = false;

	/**
	 * Whether to automatically load and use the built in configuration stuff. 
	 * Set this to be false in your onLoad before calling super() if you do not want it.
	 */
	boolean useConfig = true;

	/**
	 * Permissions handler.
	 */
	public PermissionsHandler permissions;

	/**
	 * Handle our commands!
	 */
	public CommandHandler commandHandler;

	/**
	 * Constructor. Do some setup stuff.
	 */
	@Override
	public void onLoad()
	{
		// Grab a name and version from the plugin's description file.
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		pluginVersion = pdfFile.getVersion();
		permissions = new PermissionsHandler();
		if (useConfig && !loadConfig())
			this.logSevere("Could not load configuration for " + getPluginName() + "! This may break the plugin!");
	}

	/**
	 * What to do when the plugin is enabled. 
	 * If nothing else, this will show that the plugin was loaded.
	 */
	@Override
	public void onEnable() 
	{
		commandHandler = new CommandHandler();
		showLoadedMessage();
	}

	/**
	 * What to do when the plugin is disabled.
	 */
	@Override
	public void onDisable()
	{
		showUnloadedMessage();
	}

	/**
	 * Show the message that the plugin is loaded.
	 */
	public void showLoadedMessage() 
	{
		logInfo( pluginName + " version " + pluginVersion + " is enabled!" );
		logDebug("Debugging mode is active.");
	}

	/**
	 * Show the message when the plugin is unloaded.
	 */
	public void showUnloadedMessage()
	{
		logInfo( pluginName + " version " + pluginVersion + " has been disabled.");
	}


	/**
	 * Log a warning message
	 * @param message The message to log.
	 */
	public void logWarning(String message)
	{
		log(message, Level.WARNING);
	}

	/**
	 * Log an info message
	 * @param message The message to log.
	 */
	public void logInfo(String message)
	{
		log(message, Level.INFO);
	}

	/**
	 * Log a severe message
	 * @param message The message to log.
	 */
	public void logSevere(String message)
	{
		log(message, Level.SEVERE);
	}

	/**
	 * Log a debug message (if debugging is on.)
	 * @param message The message to log.
	 */
	public void logDebug(String message)
	{
		if (this.debugMode)
			log(message, Level.INFO);
	}

	/**
	 * Check if we're debugging with this plugin.
	 * @return true if we're debugging; false otherwise.
	 */
	public boolean isDebugging()
	{
		return debugMode;
	}

	/**
	 * Log a message
	 * @param message message The message to log.
	 * @param level The level of the message.
	 */
	public void log(String message, Level level)
	{
		getLogger().log(level, message);
	}

	/**
	 * Run a command with our CommandSender.
	 * @param sender Our sender; entity or otherwise.
	 * @param command The command being sent.
	 * @param label The label for the command.
	 * @param args The arguments passed in.
	 * @return true if the command is handled; false otherwise.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return commandHandler.HandleCommand(sender, command, label, args);
	}

	/**
	 * Gets the name of this plugin.
	 * @return The name of the plugin.
	 */
	public String getPluginName()
	{
		return pluginName;
	}

	/**
	 * Gets the version of the plugin.
	 * @return The version of the plugin.
	 */
	public String getVersion()
	{
		return pluginVersion;
	}

	/**
	 * Load our default config.yml, or alternatively, create and load the default one.
	 * @return
	 */
	protected boolean loadConfig()
	{
		saveDefaultConfig();
		reloadConfig();
		if (getConfig().contains("debug"))
			debugMode = getConfig().getBoolean("debug");
		return true;
	}

	/**
	 * Just allows us to generate worlds ourselves. Does nothing otherwise.
	 * @param name The world to gen for.
	 * @param id The id of the world to gen for.
	 * @return ChunkGenerator a ChunkGenerator.
	 */
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String name, String id) {
		return super.getDefaultWorldGenerator(name, id);
	}

}

