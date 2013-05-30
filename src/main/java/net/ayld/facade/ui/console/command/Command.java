package net.ayld.facade.ui.console.command;

import java.util.Set;

public interface Command {

	public void execute(String... arg);
	
	public Set<String> supportedNames();
	public Set<String> supportedArguments();
	
	public boolean supportsName(String name);
}
