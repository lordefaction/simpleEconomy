package simpleEconomy.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import simpleEconomy.commands.BalanceCommand;
import simpleEconomy.commands.CreateAdminAccountCommand;
import simpleEconomy.commands.CreateCommand;
import simpleEconomy.commands.CreatePlayerAccountCommand;
import simpleEconomy.commands.GiveCommand;
import simpleEconomy.commands.MoneyCommand;
import simpleEconomy.commands.MoneyHelpCommand;
import simpleEconomy.commands.PayCommand;
import simpleEconomy.commands.SetCommand;
import simpleEconomy.commands.MoneyVersionCommand;
import simpleEconomy.commands.TakeCommand;

import com.google.inject.Inject;

@Plugin(id="simpleEconomy", name="simpleEconomy", version="0.1-SNAPSHOT")

public class SimpleEconomy {
	
	private static Game game;
	private static CommandManager cmdManager;
	private EconomyManager economyManager;
	public static HashMap<String, CommandSpec> commandsList;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private File configFile;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private CommentedConfigurationNode config = null;
	
	@Inject
	private Logger logger;
	
	private double defaultAmount;
	private String singularSymbol;
	private String pluralSymbol;
	private boolean enableSingularSymbol;
	private boolean enableCents;
	private boolean enableLogs;
	private boolean enableAdminAccounts;
	
	/**
	 * Load the config file content
	 */
	public void loadConfig() {
		try {
			if (!configFile.exists()) {
				loadDefaultConfig();
			}
		     
	        config = configManager.load();
	        setDefaultAmount(config.getNode("account", "default-amount").getDouble());
	        setSingularSymbol(config.getNode("currency", "singular-symbol").getString());
	        setPluralSymbol(config.getNode("currency", "plural-symbol").getString());
	        setEnableSingularSymbol(config.getNode("general", "enable-singular-symbol").getBoolean());
	        setEnableCents(config.getNode("general", "enable-cents").getBoolean());
	        setEnableLogs(config.getNode("general", "logging").getBoolean());
	        setEnableAdminAccounts(config.getNode("general", "enable-admin-accounts").getBoolean());
		     
		 } catch (IOException exception) {
		     getLogger().error("The default configuration could not be loaded or created !");
		     getLogger().error(exception.getMessage());
		 }
	}
	
	/**
	 * Load the default configuration
	 */
	public void loadDefaultConfig() {
		try {
			getLogger().info("Config file don't exist. Loading default config.");
	    	configFile.createNewFile();
	        config = configManager.load();
	        
	        config.getNode("account").getNode("default-amount").setValue(100);
	        config.getNode("currency").getNode("singular-symbol").setValue("$");
	        config.getNode("currency").getNode("plural-symbol").setValue("$");
	        config.getNode("general").getNode("enable-singular-symbol").setValue(true);
	        config.getNode("general").getNode("enable-cents").setValue(true);
	        config.getNode("general").getNode("logging").setValue(false);
	        config.getNode("general").getNode("enable-admin-accounts").setValue(false);
	        
	        saveConfig();
		} catch (IOException exception) {
			getLogger().error("The default configuration could not be loaded or created !");
			getLogger().error(exception.getMessage());
		}
	}
	
	/**
	 * Save the configuration
	 */
	public boolean saveConfig() {
		try {
			configManager.save(config);
			return true;
		} catch(IOException e) {
			getLogger().error("Unable to save configuration ! Error : " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Registering commands on commandManager
	 */
	private void commandsRegistering() {
		CommandSpec moneyHelpCommand = CommandSpec.builder()
				.description(Text.of("/simpleEconomy help"))
				.extendedDescription(Text.of("Display simpleEconomy commands list."))
				.permission("simpleEconomy.help")
				.executor(new MoneyHelpCommand())
				.build();
		commandsList.put("help", moneyHelpCommand);
		
		class MoneyReloadCommand implements CommandExecutor {
			public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
				loadConfig();
				src.sendMessage(Text.of("SimpleEconomy config reloaded !"));
				return CommandResult.success();
			}
		}
		
		CommandSpec moneyReloadCommand = CommandSpec.builder()
				.description(Text.of("/simpleEconomy reload"))
				.extendedDescription(Text.of("Reload simpleEconomy config."))
				.permission("simpleEconomy.admin")
				.executor(new MoneyReloadCommand())
				.build();
		commandsList.put("reload", moneyReloadCommand);
		
		CommandSpec moneyVersionCommand = CommandSpec.builder()
				.description(Text.of("/simpleEconomy"))
				.extendedDescription(Text.of("Display simpleEconomy version."))
				.permission("simpleEconomy.version")
				.executor(new MoneyVersionCommand())
				.build();
		commandsList.put("version", moneyVersionCommand);
		
		CommandSpec balanceCommand = CommandSpec.builder()
				.description(Text.of("/money balance <player>"))
				.extendedDescription(Text.of("Display the specified player account."))
				.permission("simpleEconomy.money.balance")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
				.executor(new BalanceCommand(this))
				.build();
		commandsList.put("balance", balanceCommand);
		
		CommandSpec payCommand = CommandSpec.builder()
				.description(Text.of("/money pay <player> <amount>"))
				.extendedDescription(Text.of("Pay the specified sum to the indicate player."))
				.permission("simpleEconomy.money.pay")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new PayCommand(this))
				.build();
		commandsList.put("pay", payCommand);
		
		CommandSpec giveCommand = CommandSpec.builder()
				.description(Text.of("/money give <player> <amount>"))
				.extendedDescription(Text.of("Give the specified sum to a player."))
				.permission("simpleEconomy.admin.give")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new GiveCommand(this))
				.build();
		commandsList.put("give", giveCommand);
		
		CommandSpec takeCommand = CommandSpec.builder()
				.description(Text.of("/money take <player> <amount>"))
				.extendedDescription(Text.of("Remove the specified sum to a player."))
				.permission("simpleEconomy.admin.take")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new TakeCommand(this))
				.build();
		commandsList.put("take", takeCommand);
		
		CommandSpec setCommand = CommandSpec.builder()
				.description(Text.of("/money set <player> <amount>"))
				.extendedDescription(Text.of("Set the specified sum on the indicate player account."))
				.permission("simpleEconomy.admin.set")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new SetCommand(this))
				.build();
		commandsList.put("set", setCommand);
		
		CommandSpec createPlayerAccountCommand = CommandSpec.builder()
				.description(Text.of("/money create playerAccount <player>"))
				.extendedDescription(Text.of("Create an account for the specified player."))
				.permission("simpleEconomy.admin.create.player")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
				.executor(new CreatePlayerAccountCommand(this))
				.build();
		commandsList.put("create playerAccount", createPlayerAccountCommand);
		
		if(this.isEnableAdminAccounts()) {
			CommandSpec createAdminAccountCommand = CommandSpec.builder()
					.description(Text.of("/money create adminAccount <name>"))
					.extendedDescription(Text.of("Create a new admin account with the specified name."))
					.permission("simpleEconomy.admin.create.admin")
					.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
					.executor(new CreateAdminAccountCommand(this))
					.build();
			commandsList.put("create adminAccount", createAdminAccountCommand);
			
			CommandSpec createCommand = CommandSpec.builder()
					.description(Text.of("/money create"))
					.extendedDescription(Text.of("Create a new player or admin account."))
					.permission("simpleEconomy.admin.create")
					.executor(new CreateCommand())
					.child(createPlayerAccountCommand, "playerAccount")
					.child(createAdminAccountCommand, "adminAccount")
					.build();
			
			CommandSpec moneyCommand = CommandSpec.builder()
					.description(Text.of("/money"))
					.extendedDescription(Text.of("Display your account."))
					.permission("simpleEconomy.money.account")
					.executor(new MoneyCommand(this))
					.child(moneyHelpCommand, "help")
					.child(moneyReloadCommand, "reload")
					.child(balanceCommand, "balance")
					.child(payCommand, "pay")
					.child(giveCommand, "give")
					.child(takeCommand, "take")
					.child(setCommand, "set")
					.child(createCommand, "create")
					.build();
			
			commandsList.put("money", moneyCommand);
			
			cmdManager.register(this, moneyCommand, "money");
			
		} else {
			CommandSpec createCommand = CommandSpec.builder()
					.description(Text.of("/money create"))
					.extendedDescription(Text.of("Create a new player or admin account."))
					.permission("simpleEconomy.admin.create")
					.executor(new CreateCommand())
					.child(createPlayerAccountCommand, "playerAccount")
					.build();
			
			CommandSpec moneyCommand = CommandSpec.builder()
					.description(Text.of("/money"))
					.extendedDescription(Text.of("Display your account."))
					.permission("simpleEconomy.money.account")
					.executor(new MoneyCommand(this))
					.child(moneyHelpCommand, "help")
					.child(moneyReloadCommand, "reload")
					.child(balanceCommand, "balance")
					.child(payCommand, "pay")
					.child(giveCommand, "give")
					.child(takeCommand, "take")
					.child(setCommand, "set")
					.child(createCommand, "create")
					.build();
			
			commandsList.put("money", moneyCommand);
			
			//cmdManager.register(this, simpleEconomyCommand, "simpleEconomy");
			cmdManager.register(this, moneyCommand, "money");
		}
	}
	
	/**
	 * Plugin initialization
	 * @param event server initialization
	 */
	@Listener
	public void onGameInitialization(GameInitializationEvent event) {
		getLogger().info("--------------------------------------");
		getLogger().info("Loading SimpleEconomy ...");
		
		game = Sponge.getGame();
		cmdManager = game.getCommandManager();
		commandsList = new HashMap<String, CommandSpec>();
		
		loadConfig();
		
		commandsRegistering();
		
		economyManager = new EconomyManager(this);
		
		getLogger().info("SimpleEconomy is made by lorde_faction.");
		getLogger().info("This plugin is still in development.");
		getLogger().info("It may contain bugs and could be unstable. Please save your server before use it !");
		getLogger().info("Loading of SimpleEconomy finished !");
		getLogger().info("--------------------------------------");
	}
	
	@Listener
	public void onGameStopped(GameStoppedServerEvent event) {
		economyManager.saveAcountsFile();
		saveConfig();
		getLogger().info("SimpleEconomy stopped.");
	}
	
	@Listener
	public void onPlayerConnection(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		
		if(!economyManager.checkAccount(player.getUniqueId().toString())) {
			economyManager.addPlayerAccount(player.getUniqueId().toString());
		}
	}

	/**
	 * Getter on game instance
	 * @return game
	 */
	public static Game getGame() {
		return game;
	}
	
	/**
	 * Getter on cmdManager
	 * @return
	 */
	public static CommandManager getCommandManager() {
		return cmdManager;
	}
	
	/**
	 * Getter on economyManager
	 * @return economyManager
	 */
	public EconomyManager getEconomyManager() {
		return economyManager;
	}
	
	/**
	 * Getter on Commands HashMap 
	 * @return commandsList
	 */
	public HashMap<String, CommandSpec> getCommandsList() {
		return commandsList;
	}
	
	/**
	 * Getter on logger
	 * @return logger
	 */
	public Logger getLogger() {
	    return logger;
	}
	
	/**
	 * Getter on configFile
	 * @return configFile
	 */
	public File getConfigFile() {
	    return configFile;
	}

	/**
	 * Getter on configDir
	 * @return configDir
	 */
	public File getConfigDir() {
		return configDir;
	}

	/**
	 * Getter on getConfigManager
	 * @return getConfigManager
	 */
	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
	    return configManager;
	}

	
	public double getDefaultAmount() {
		return defaultAmount;
	}

	public void setDefaultAmount(double defaultAmount) {
		this.defaultAmount = defaultAmount;
	}

	public String getSingularSymbol() {
		return singularSymbol;
	}

	public void setSingularSymbol(String singularSymbol) {
		this.singularSymbol = singularSymbol;
	}

	public String getPluralSymbol() {
		return pluralSymbol;
	}

	public void setPluralSymbol(String pluralSymbol) {
		this.pluralSymbol = pluralSymbol;
	}

	public boolean isEnableSingularSymbol() {
		return enableSingularSymbol;
	}

	public void setEnableSingularSymbol(boolean enableSingularSymbol) {
		this.enableSingularSymbol = enableSingularSymbol;
	}

	public boolean isEnableCents() {
		return enableCents;
	}

	public void setEnableCents(boolean enableCents) {
		this.enableCents = enableCents;
	}

	public boolean isEnableLogs() {
		return enableLogs;
	}

	public void setEnableLogs(boolean enableLogs) {
		this.enableLogs = enableLogs;
	}

	public boolean isEnableAdminAccounts() {
		return enableAdminAccounts;
	}

	public void setEnableAdminAccounts(boolean enableAdminAccounts) {
		this.enableAdminAccounts = enableAdminAccounts;
	}
}