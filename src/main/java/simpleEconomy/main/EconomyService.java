package simpleEconomy.main;

import org.spongepowered.api.entity.living.player.Player;

public interface EconomyService {
	public boolean checkAccount(String accountName);
	public void addMoney(String accountName, double amount);
	public void subtractedMoney(String accountName, double amount);
	public void addPlayerAccount(String accountName);
	public void addAdminAccount(String accountName, Player owner);
	public void removePlayerAccount(String accountName);
	public void removeAdminAccount(String accountName);
}
