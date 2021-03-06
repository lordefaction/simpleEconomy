package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import simpleEconomy.main.SimpleEconomy;

public class SetCommand implements CommandExecutor {

	private SimpleEconomy plugin;
	
	public SetCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Player target = args.<Player>getOne("player").get();
			Double amount = args.<Double>getOne("amount").get();
			
			if((target != null) && (amount != null) && (amount >= 0)) {
				if(plugin.getEconomyManager().checkAccount(target.getUniqueId().toString())) {
					plugin.getEconomyManager().setMoney(target.getUniqueId().toString(), amount);
					
					if((amount < 2) && (plugin.isEnableSingularSymbol() == true)) src.sendMessage(Text.of(target.getName() + "'s account's amount is now " + amount + plugin.getSingularSymbol()));
					else src.sendMessage(Text.of(target.getName() + "'s account's amount is now " + amount + plugin.getPluralSymbol()));
					
					return CommandResult.success();
				} else src.sendMessage(Text.of("Removing the sum impossible. The target account doesn't exist !"));
			} else src.sendMessage(Text.of("Removing the sum impossible. Wrong parameters !"));
			return CommandResult.empty();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}
}