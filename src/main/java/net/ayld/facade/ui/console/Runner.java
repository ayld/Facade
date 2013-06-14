package net.ayld.facade.ui.console;

import java.util.Set;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.command.resolver.CommandResolver;
import net.ayld.facade.ui.console.model.CommandBundle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Joiner;

public class Runner {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/ui-console-context");
		final CommandResolver commandResolver = (CommandResolver) context.getBean("commandResolver");
		
		for (CommandBundle bundle : commandResolver.resolve(Joiner.on(" ").join(args))) {
			
			final Command command = bundle.getCommand();
			final Set<String> arguments = bundle.getArguments();
			final String[] argumentArray = arguments.toArray(new String[arguments.size()]);
			
			command.execute(argumentArray);
		}
	}
}
