package net.ayld.facade.ui.console.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.ayld.facade.ui.console.command.Command;

public class CommandBundle {
	
	private final Command command;
	private final Set<Argument> arguments;
	
	public CommandBundle(Command command, Set<Argument> arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public Command getCommand() {
		return command;
	}

	public Set<Argument> getArguments() {
		return ImmutableSet.copyOf(arguments);
	}
}
