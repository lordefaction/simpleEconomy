package simpleEconomy.main;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;

import com.google.inject.Inject;

public class EconomyManager implements EconomyService {
	
	private SimpleEconomy plugin;
	private Logger logger;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File accountsFile;
	
	private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
	private static ConfigurationNode accountsNode;

	public EconomyManager(SimpleEconomy plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		
		setupConfig();
	}
	
	public void setupConfig() {
		accountsFile = new File(plugin.getConfigDir(), "accounts.conf");
		configurationLoader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
        	accountsNode = configurationLoader.load();
            if (!accountsFile.exists()) {
            	configurationLoader.save(accountsNode);
            }
        } catch (IOException e) {
            logger.error("Could not create accounts file!");
        }
	}
	
	/**
	 * Save accounts file
	 */
	public void saveAcountsFile() {
		try {
			logger.info("saving accounts file.");
			configurationLoader.save(accountsNode);
		} catch(IOException exception) {
			logger.error("Could not save the accounts file ! " + exception.getMessage());
		}
	}
	
	public boolean checkAccount(String accountName) {
		if(accountsNode.getNode(accountName,"amount").getValue() == null) return false;
		else return true;
	}
	
	/**
	 * Add the indicated sum on the specified account
	 * @param accountName
	 * @param amount
	 */
	public void addMoney(String accountName, double amount) {
		double value = getAccountAmount(accountName);
		accountsNode.getNode(accountName, "amount").setValue(value + amount);
		saveAcountsFile();
	}
	
	/**
	 * subtracted the indicated sum on the specified account
	 * @param accountName
	 * @param amount
	 */
	public void subtractedMoney(String accountName, double amount) {
		double value = getAccountAmount(accountName);
		accountsNode.getNode(accountName, "amount").setValue(value - amount);
		saveAcountsFile();
	}
	
	/**
	 * Define the the specified account amount with the indicate sum
	 * @param accountName
	 * @param amount
	 */
	public void setMoney(String accountName, double amount) {
		accountsNode.getNode(accountName, "amount").setValue(amount);
		saveAcountsFile();
	}
	
	/**
	 * Reset the specified account to his default amount
	 */
	public void resetAccount(String accountName) {
		if(accountsNode.getNode(accountName, "type").getString().equals("admin")) {
			setMoney(accountName, Double.MAX_VALUE);
		} else if(accountsNode.getNode(accountName, "type").getString().equals("player")) {
			setMoney(accountName, plugin.getDefaultAmount());
		}
		saveAcountsFile();
	}
	
	public void testEconomyManager() {
		System.out.println("Test du manager d'économie !");
	}
	
	/**
	 * Return the specified account amount
	 * @param accountName
	 * @return double account amount
	 */
	public double getAccountAmount(String accountName) {
		return accountsNode.getNode(accountName, "amount").getDouble();
	}
	
	/**
	 * Create a new player account
	 * @param accountName
	 */
	public void addPlayerAccount(String accountName) {
			logger.info("adding " + plugin.getDefaultAmount() + " on account " + accountName);
			accountsNode.getNode(accountName, "amount").setValue(plugin.getDefaultAmount());
			accountsNode.getNode(accountName, "type").setValue("player");
			saveAcountsFile();
	}
	
	/**
	 * Create a new admin shop account
	 * @param accountName
	 */
	public void addAdminAccount(String accountName, Player owner) {
		accountsNode.getNode(accountName, "owner").setValue(owner.getUniqueId());
		accountsNode.getNode(accountName, "amount").setValue(Double.MAX_VALUE);
		accountsNode.getNode(accountName, "type").setValue("admin");
		saveAcountsFile();
	}
	
	/**
	 * Remove the specified player account
	 * @param accountName
	 */
	public void removePlayerAccount(String accountName) {
		accountsNode.removeChild(accountName);
		saveAcountsFile();
	}
	
	/**
	 * Remove the specified admin account*
	 * @param accountName
	 */
	public void removeAdminAccount(String accountName) {
		accountsNode.removeChild(accountName);
		saveAcountsFile();
	}
}