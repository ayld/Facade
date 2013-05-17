package net.ayld.facade.ui.console.command.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.model.Argument;

public abstract class AbstractCommand implements Command {

	private Set<String> supportedNames;
	private Set<String> supportedArgumentNames;
	
	private AbstractCommand(Set<String> supportedNames, Set<String> supportedArguments) {
		this.supportedNames = supportedNames;
		this.supportedArgumentNames = supportedArguments;
	}

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
	public boolean supportsName(String name) {
		return supportedNames().contains(name);
	}

}
