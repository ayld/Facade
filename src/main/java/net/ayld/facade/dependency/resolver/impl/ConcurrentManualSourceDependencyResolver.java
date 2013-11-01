package net.ayld.facade.dependency.resolver.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.ConcurrentTaskInvoker;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ConcurrentManualSourceDependencyResolver extends ManualParseSourceDependencyResolver{

	private ExecutorService threadPool;
	
	@Override
	public Set<ClassName> resolve(Set<SourceFile> sources) throws IOException {
		final Set<Callable<Set<ClassName>>> resolutionTasks = Sets.newHashSetWithExpectedSize(sources.size());
		for (final SourceFile source : sources) {
			
			final Callable<Set<ClassName>> resolutionTask = new Callable<Set<ClassName>>() {
				
				@Override
				public Set<ClassName> call() throws Exception {
					return resolve(source);
				}
			};
			resolutionTasks.add(resolutionTask);
		}
		try {
			return ImmutableSet.copyOf(
					ConcurrentTaskInvoker.<ClassName>onPool(threadPool).invokeAll(resolutionTasks)
			);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Required
	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
}
