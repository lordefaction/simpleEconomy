package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import simpleEconomy.main.SimpleEconomy;

public class CreatePlayerAccountCommand implements CommandExecutor {

	private SimpleEconomy plugin;
	
	public CreatePlayerAccountCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Player target = args.<Player>getOne("player").get();
			if(target != null) {
				if(!plugin.getEconomyManager().checkAccount(target.getUniqueId().toString())) {
					plugin.getEconomyManager().addPlayerAccount(target.getUniqueId().toString());
					src.sendMessage(Text.of("Creating new account for player " + target.getName()));
				}
				else src.sendMessage(Text.of("Command aborted, " + target.getName() + "already has an account"));	
				return CommandResult.success();
			} else return CommandResult.empty();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}
}