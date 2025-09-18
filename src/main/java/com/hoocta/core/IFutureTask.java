package com.hoocta.core;

public interface IFutureTask<K, V> {
	V execute(K key) throws Exception;
}
