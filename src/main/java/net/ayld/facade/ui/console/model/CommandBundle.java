package net.ayld.facade.ui.console.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.ayld.facade.ui.console.command.Command;

public class CommandBundle {
	
	private final Command command;
	private final Set<Argument> arguments;
	
	public CommandBundle(Command command, Set<Argument> arguments) {
		if (arguments == null) {
			arguments = Collections.emptySet();
		}
		this.command = command;
		this.arguments = arguments;
	}

	public Command getCommand() {
		return command;
	}

	public Set<Argument> getArguments() {
		return ImmutableSet.copyOf(arguments);
	}

	@Override
	public String toString() {
		return "command: " + command + ", arguments: " + arguments;
	}

	@Override
	public int hashCode() {
		return Objects.hash(command, arguments);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CommandBundle)) {
			return false;
		}
		
		final CommandBundle cb = (CommandBundle) obj;
		
		return 
				Objects.equals(cb.getCommand(), command) &&
				Objects.equals(cb.getArguments(), arguments);
	}
}
