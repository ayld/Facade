package net.ayld.facade.ui.console.command.resolver.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.command.resolver.CommandResolver;
import net.ayld.facade.ui.console.model.CommandBundle;

public class SpringCommandResolver implements CommandResolver{

	@Autowired
	private Set<Command> commands;
	
	@Override
	public Set<CommandBundle> resolve(String input) {
		// TODO this is just a stub
		throw new RuntimeException("not implemented");
	}
}
