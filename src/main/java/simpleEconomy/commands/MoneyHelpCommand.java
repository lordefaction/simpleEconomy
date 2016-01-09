package simpleEconomy.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import simpleEconomy.main.SimpleEconomy;

public class MoneyHelpCommand implements CommandExecutor {

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			HashMap<String, CommandSpec> commandsList = SimpleEconomy.commandsList;
			
			src.sendMessage(Text.of("-----------------------------------------------------"));
			src.sendMessage(Text.builder("Simpleeconomy commands list :").build());
			
			// load info from each commands
			for(String name : commandsList.keySet()) {
				CommandSpec commandSpec = commandsList.get(name);
				src.sendMessage(Text.builder(commandSpec.getShortDescription(src).get().toPlain()).color(TextColors.WHITE).build());
			}
			
			return CommandResult.success();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			return CommandResult.empty();
		}
	}
}