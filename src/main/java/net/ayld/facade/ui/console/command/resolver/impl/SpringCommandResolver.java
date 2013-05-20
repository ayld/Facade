package net.ayld.facade.ui.console.command.resolver.impl;

import java.util.Set;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.command.resolver.CommandResolver;
import net.ayld.facade.ui.console.model.Argument;
import net.ayld.facade.ui.console.model.CommandBundle;
import net.ayld.facade.util.Tokenizer;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SpringCommandResolver implements CommandResolver{

	@Autowired
	private Set<Command> commands;
	
	@Override
	public Set<CommandBundle> resolve(String input) {
		final Set<CommandBundle> result = Sets.newHashSet();
		
		final Tokenizer inputTokenizer = Tokenizer.delimiter(" ").tokenize(input);
		final Set<Argument> arguments = Sets.newHashSet();
		for (int i = inputTokenizer.tokens().size() - 1; i >= 0; i--) {
			
			final String currentToken = inputTokenizer.tokenByIndex(i);
			
			final Command found = find(currentToken);
			if (found != null) {
				
				for (Argument arg : arguments) {
					if (!found.supportsArgument(arg.toString())) {
						throw new IllegalArgumentException("command: " + found + ", does not support argument: " + arg);
					}
				}
				
				result.add(new CommandBundle(found, ImmutableSet.copyOf(arguments)));
				arguments.clear();
			}
			else {
				arguments.add(Argument.fromString(currentToken));
			}
		}
		return result;
	}

	private Command find(String cmd) {
		for (Command c : commands) {
			if (c.supportedNames().contains(cmd)) {
				return c;
			}
		}
		return null;
	}
}
