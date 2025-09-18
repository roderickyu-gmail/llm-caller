package com.hoocta.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynTaskExcutorUtils {

	/**
	 * 提交任务并执行异步操作，返回结果映射。
	 *
	 * @param keys 要处理的任务键集合
	 * @param task 根据键生成任务的逻辑
	 * @param <K>  键的类型
	 * @param <V>  返回值的类型
	 * @return 任务结果的映射
	 */
	public static <K, V> Map<K, V> executeTasks(Collection<K> keys, final IFutureTask<K, V> task) {
		Map<K, Future<V>> futureMap = new HashMap<K, Future<V>>();
		for (final K key : keys) {
			futureMap.put(key, AsynTaskExcutors.submit(new Callable<V>() {
				public V call() throws Exception {
					return task.execute(key);
				}
			}));
		}

		Map<K, V> resultMap = new HashMap<K, V>();
		for (Map.Entry<K, Future<V>> entry : futureMap.entrySet()) {
			try {
				resultMap.put(entry.getKey(), entry.getValue().get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	public static <T> List<T> executeTasks(Callable<T> task, int count) {
		List<Future<T>> futureList = AsynTaskExcutors.submit(task, count);
		List<T> retList = new LinkedList<>();
		for (Future<T> f : futureList) {
			try {
				T t = f.get();
				retList.add(t);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return retList;
	}
}
