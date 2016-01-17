package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import simpleEconomy.main.SimpleEconomy;

public class CreateCommand implements CommandExecutor {
	
	private SimpleEconomy plugin;
	
	public CreateCommand(SimpleEconomy plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			if(src instanceof Player) {
				String type = args.<String>getOne("type").get();
				if(type.equals("player")) {
					Player target = args.<Player>getOne("name").get();
					if(target != null) {
						if(!plugin.getEconomyManager().checkAccount(target.getUniqueId().toString())) {
							plugin.getEconomyManager().addPlayerAccount(target.getUniqueId().toString());
							src.sendMessage(Text.of("Creating new account for player " + target.getName()));
						}
						else src.sendMessage(Text.of("Command aborted, " + target.getName() + "already has an account"));	
						return CommandResult.success();
					} else return CommandResult.empty();
				} else if (type.equals("admin")) {
					Player owner = (Player) src;
					String name = args.<String>getOne("name").get();
					
					if(!plugin.getEconomyManager().checkAccount(name)) {
						plugin.getEconomyManager().addAdminAccount(name, owner);
						src.sendMessage(Text.of("Creating a new admin account named " + name + ". You own it."));
					}
					else src.sendMessage(Text.of("Command aborted, an account already exists with that name"));	
					return CommandResult.success();
				} else {
					src.sendMessage(Text.of("You must specify the account type : 'player' or 'admin'"));
					return CommandResult.empty();
				}
			} else {
				src.sendMessage(Text.of("This command can only be launch by a player !"));
				return CommandResult.empty();
			}
			
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			plugin.getLogger().error("Le joueur " + src.getName() + " a demande d'afficher la balance du joueur " + args.<Player>getOne("player").get().getName());
			plugin.getLogger().error(exception.getMessage());
			return CommandResult.empty();
		}
	}

}
