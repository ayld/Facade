package net.ayld.facade.ui.console.command;

import java.util.Set;

import net.ayld.facade.ui.console.model.Argument;

public interface Command {

	public void execute(Argument... arg);
	
	public Set<String> supportedNames();
	public Set<String> supportedArguments();
	
	public boolean supportsName(String name);
	public boolean supportsArgument(String arg);
}
