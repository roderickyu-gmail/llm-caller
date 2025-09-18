package com.hoocta.core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class AsynTaskExcutors {

	private static final int THREAD_POOL_SIZE = 1000; // 基本都是 IO 密集型，开大点
	private static final ThreadFactory NAMED_THREAD_FACTORY = new NamedThreadFactory("pool-task-thread");
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE, NAMED_THREAD_FACTORY);
	
	public static <T> Future<T> submit(Callable<T> task) {
		return EXECUTOR_SERVICE.submit(task);
	}
	
	public static <T> List<Future<T>> submit(Callable<T> task, int count) {
		List<Future<T>> list = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			list.add(submit(task));
		}
		return list;
	}
	
	public static void submit(Runnable task) {
		EXECUTOR_SERVICE.submit(task);
	}
}
