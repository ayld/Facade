package net.ayld.facade.bundle.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import net.ayld.facade.event.model.JarExtractionStartEvent;
import net.ayld.facade.model.ExplodedJar;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class CuncurrentManualJarExploder extends ManualJarExploder {

	private ExecutorService threadPool;
	
	@Override
	public Set<ExplodedJar> explode(Set<JarFile> jars) throws IOException {
		eventBus.post(new JarExtractionStartEvent("Starting concurrent extraction", this.getClass()));
		
		final Set<ExplodedJar> result = Sets.newHashSet();

		final Set<Callable<ExplodedJar>> extractionTasks = Sets.newHashSetWithExpectedSize(jars.size());
		for (final JarFile jar : jars) {
			
			final Callable<ExplodedJar> extractionTask = new Callable<ExplodedJar>() {
				
				@Override
				public ExplodedJar call() throws Exception {
					return explode(jar);
				}
			};
			extractionTasks.add(extractionTask);
		}
		try {
			final List<Future<ExplodedJar>> futures = threadPool.invokeAll(extractionTasks);
			for (Future<ExplodedJar> f : futures) {
				
				result.add(f.get());
				
			}
			threadPool.shutdown();
			threadPool.awaitTermination(1, TimeUnit.DAYS); // FOREVER !!!
			
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		return ImmutableSet.copyOf(result);
	}

	@Required
	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
}
