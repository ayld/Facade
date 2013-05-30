package net.ayld.facade.ui.console.command.resolver.impl;

import static junit.framework.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import net.ayld.facade.ui.console.command.impl.GangnamCommand;
import net.ayld.facade.ui.console.command.impl.StyleCommand;
import net.ayld.facade.ui.console.command.resolver.CommandResolver;
import net.ayld.facade.ui.console.model.CommandBundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testSpringCommandResolverContext.xml"})
public class TestSpringCommandResolver {

	@Autowired
	private CommandResolver commandResolver;
	
	@Test
	public void resolve() {
		Set<CommandBundle> resolved = commandResolver.resolve("style");
		
		assertTrue(resolved != null);
		assertTrue(resolved.size() == 1);
		assertTrue(resolved.iterator().next().getCommand() instanceof StyleCommand);
		assertTrue(resolved.iterator().next().getArguments() != null);
		assertTrue(resolved.iterator().next().getArguments().size() == 0);
		
		resolved = commandResolver.resolve("gangnam sexey style");
		
		assertTrue(resolved != null);
		assertTrue(resolved.size() == 2);
		assertTrue(resolved.contains(new CommandBundle(new GangnamCommand(), ImmutableSet.of("sexey"))));
		assertTrue(resolved.contains(new CommandBundle(new StyleCommand(), Collections.<String>emptySet())));
		
		resolved = commandResolver.resolve("gangnam sexey leydey style");
		
		assertTrue(resolved != null);
		assertTrue(resolved.size() == 2);
		assertTrue(resolved.contains(new CommandBundle(new GangnamCommand(), ImmutableSet.of("sexey", "leydey"))));
		assertTrue(resolved.contains(new CommandBundle(new StyleCommand(), Collections.<String>emptySet())));
		
		resolved = commandResolver.resolve("style gangnam");
		
		assertTrue(resolved != null);
		assertTrue(resolved.size() == 2);
		assertTrue(resolved.contains(new CommandBundle(new GangnamCommand(), Collections.<String>emptySet())));
		assertTrue(resolved.contains(new CommandBundle(new GangnamCommand(), Collections.<String>emptySet())));
	}
	
	@Test
	public void failResolve() {
		Set<CommandBundle> resolved = commandResolver.resolve("nonexistent");
		
		assertTrue(resolved != null);
		assertTrue(resolved.size() == 0);
	}
}
