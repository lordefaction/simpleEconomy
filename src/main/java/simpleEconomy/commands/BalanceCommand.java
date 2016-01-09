package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import simpleEconomy.main.SimpleEconomy;

public class BalanceCommand implements CommandExecutor {

	private SimpleEconomy plugin;
	
	public BalanceCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Player target = args.<Player>getOne("player").get();
			if((target != null) && (plugin.getEconomyManager().checkAccount(target.getUniqueId().toString()))) {
				double amount = plugin.getEconomyManager().getAccountAmount(target.getUniqueId().toString());
				
				if((amount < 2) && (plugin.isEnableSingularSymbol() == true)) {
					src.sendMessage(Text.builder("[").color(TextColors.DARK_GREEN)
							.append(Text.builder("Money]").color(TextColors.WHITE)
							.append(Text.builder("] " + target.getName() + "'s balance : ").color(TextColors.DARK_GREEN)
							.append(Text.builder(amount + " " + plugin.getSingularSymbol())
							.build()).build()).build()).build());
				} else {

					src.sendMessage(Text.builder("[").color(TextColors.DARK_GREEN)
							.append(Text.builder("SimpleEconomy]").color(TextColors.WHITE)
							.append(Text.builder("] " + target.getName() + "'s balance : ").color(TextColors.DARK_GREEN)
							.append(Text.builder(amount + " " + plugin.getPluralSymbol())
							.build()).build()).build()).build());
				}
				
				return CommandResult.success();
			} else return CommandResult.empty();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error("Le joueur " + src.getName() + " a demande d'afficher la balance du joueur " + args.<Player>getOne("player").get().getName());
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}

}
