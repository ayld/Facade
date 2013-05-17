package net.ayld.facade.ui.console.command.resolver;

import java.util.Set;

import net.ayld.facade.ui.console.model.CommandBundle;

public interface CommandResolver {
	
	public Set<CommandBundle> resolve(String input);
}
