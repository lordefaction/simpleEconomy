package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import simpleEconomy.main.SimpleEconomy;

public class PayCommand implements CommandExecutor {

	private SimpleEconomy plugin;
	
	public PayCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			if(src instanceof Player) {
				Player executor = (Player) src;
				Player target = args.<Player>getOne("player").get();
				Double amount = args.<Double>getOne("amount").get();
				
				if((executor != null) && (target != null) && (amount != null) && (amount > 0)) {
					if((plugin.getEconomyManager().checkAccount(executor.getUniqueId().toString())) && (plugin.getEconomyManager().checkAccount(target.getUniqueId().toString()))) {
						if(plugin.getEconomyManager().getAccountAmount(executor.getUniqueId().toString()) > amount) {
							plugin.getEconomyManager().addMoney(target.getUniqueId().toString(), amount);
							plugin.getEconomyManager().subtractedMoney(executor.getUniqueId().toString(), amount);
							
							if((amount < 2) && (plugin.isEnableSingularSymbol() == true)) src.sendMessage(Text.of("Payement of " + amount + plugin.getSingularSymbol() + " to " + target.getName()));
							else src.sendMessage(Text.of("Payement of " + amount + plugin.getPluralSymbol() + " to " + target.getName()));
							
							return CommandResult.success();
						}
					}
				}
				return CommandResult.empty();
			} else {
				src.sendMessage(Text.of("This command can only be launch by a player !"));
				return CommandResult.empty();
			}
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}
}