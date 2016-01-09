package simpleEconomy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import simpleEconomy.main.SimpleEconomy;

public class MoneyVersionCommand implements CommandExecutor {
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			src.sendMessage(Text.builder("simpleEconomy version " + SimpleEconomy.getGame().getPluginManager().getPlugin("simpleEconomy").get().getVersion()).color(TextColors.BLUE).build());
			src.sendMessage(Text.of("/simpleEconomy help to display simpleEconomy command list ."));
			return CommandResult.success();
		} catch(NullPointerException exception) {
			src.sendMessage(Text.of("Error while processing your request ! Please contact your administrator !"));
			return CommandResult.empty();
		}
	}
}