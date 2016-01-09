package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import simpleEconomy.main.SimpleEconomy;

public class CreateAdminAccountCommand implements CommandExecutor {

	private SimpleEconomy plugin;
	
	public CreateAdminAccountCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Player owner = (Player) src;
			String name = args.<String>getOne("name").get();
			Player target = args.<Player>getOne("player").get();
			if(target != null) {
				if(!plugin.getEconomyManager().checkAccount(target.getUniqueId().toString())) {
					plugin.getEconomyManager().addAdminAccount(name, owner);
					src.sendMessage(Text.of("Creating a new admin account named " + target.getName() + ". You own it."));
				}
				else src.sendMessage(Text.of("Command aborted, an account already exists with that name"));	
				return CommandResult.success();
			} else return CommandResult.empty();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}
}