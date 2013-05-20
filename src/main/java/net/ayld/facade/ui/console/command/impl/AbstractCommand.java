package net.ayld.facade.ui.console.command.impl;

import java.util.Objects;
import java.util.Set;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.model.Argument;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractCommand implements Command {

	private Set<String> supportedNames;
	private Set<String> supportedArgumentNames;
	
	protected final void supportNames(String... names) {
		supportedNames = ImmutableSet.copyOf(names);
	}
	
	protected final void supportArguments(String... args) {
		supportedArgumentNames = ImmutableSet.copyOf(args);
	}
	
	@Override
	public final void execute(Argument... args) {
		for (Argument arg : args) {
			if (!supportedArgumentNames.contains(arg.toString())) {
				throw new IllegalArgumentException("argument " + arg + ", not supported");
			}
		}
		internalExecute(args);
	}
	
	protected abstract void internalExecute(Argument... args);
	
	@Override
	public Set<String> supportedNames() {
		return supportedNames;
	}

	@Override
	public Set<String> supportedArguments() {
		return supportedArgumentNames;
	}

	@Override
	public boolean supportsName(String name) {
		return supportedNames().contains(name);
	}

	@Override
	public boolean supportsArgument(String arg) {
		return supportedArguments().contains(arg);
	}

	@Override
	public int hashCode() {
		return Objects.hash(supportedNames, supportedArgumentNames);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Command)) {
			return false;
		}
		
		final Command c = (Command) obj;
		
		return 
				Objects.equals(c.supportedArguments(), supportedArgumentNames) &&
				Objects.equals(c.supportedNames(), supportedNames);
	}
}
