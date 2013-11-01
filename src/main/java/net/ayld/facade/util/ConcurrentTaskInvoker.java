package net.ayld.facade.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;

/** 
 * This utility is intended for internal framework usage, since it won't make much sense outside of it.
 * It just submits tasks common to concurrent components to a given thread pool and returns a result
 * */
public class ConcurrentTaskInvoker<T> {
	
	private int terminationTimeoutValue = 1;
	private TimeUnit terminationTimeoutUnit = TimeUnit.DAYS;
	
	private final ExecutorService threadPool;

	private ConcurrentTaskInvoker(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	public static <RT> ConcurrentTaskInvoker<RT> onPool(ExecutorService threadPool) {
		return new ConcurrentTaskInvoker<RT>(threadPool); 
	}

	public void awaitTermination(int terminationTimeout) {
		this.terminationTimeoutValue = terminationTimeout;
	}

	public void awaitTerminationUnit(TimeUnit terminationUnit) {
		this.terminationTimeoutUnit = terminationUnit;
	}

	/** 
	 * Invokes all {@link Callable}s in the given tasks set and stores their result in the given result set.
	 * Tasks are invoked in the same ways as {@link ExecutorService#invokeAll(java.util.Collection)}.
	 * 
	 * After tasks are invoked this method calls {@link ExecutorService#shutdown()} on the threadPool.
	 * Also it awaits termination for a period specified by terminationTimeoutUnit and terminationTimeoutValue.
	 * 
	 * @param tasks the tasks to invoke
	 * 
	 * @return a set containing the results of the given {@link Callable}s
	 * */
	public Set<T> invokeAll(Set<Callable<Set<T>>> tasks) throws InterruptedException, ExecutionException {
		final Set<T> result = Sets.newHashSet();
		
		final List<Future<Set<T>>> futures = threadPool.invokeAll(tasks);
		for (Future<Set<T>> f : futures) {
			
			result.addAll(f.get());
			
		}
		threadPool.shutdown();
		threadPool.awaitTermination(terminationTimeoutValue, terminationTimeoutUnit);
		
		return result;
	}
}
