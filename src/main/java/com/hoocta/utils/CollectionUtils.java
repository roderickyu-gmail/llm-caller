package com.hoocta.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
	
	
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.size() < 1;
	}
	
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.size() < 1;
	}
	
	public static <T> void printJson(Collection<T> list) {
		if (isEmpty(list)) {
			return;
		}
		for (T t : list) {
			System.out.println(JsonUtils.toJson(t));
		}
	}
	public static <T> void print(Collection<T> list) {
		if (isEmpty(list)) {
			return;
		}
		for (T t : list) {
			System.out.println(t);
		}
	}
	public static <V> Map<Integer, String> printIndex(Collection<V> list) {
		Map<Integer, String> map = StringUtils.toIndexStringValueMap(list);
		System.out.println(StringUtils.toIndexString(map));
		return map;
	}
	
	public static <K,V> void print(Map<K, V> map) {
		if (isEmpty(map)) {
			return;
		}
		for (Map.Entry<K, V> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
	}

	public static <V> List<V> toList(Collection<V> collection) {
		if (isEmpty(collection)) {
			return null;
		}
		List<V> list = new LinkedList<>();
		for (V v : collection) {
			list.add(v);
		}
		return list;
	}
	
	public static void main(String[] args) {
		List<String> list = new LinkedList<String>();
		System.out.println(isEmpty(list));
		list.add("aaa");
		System.out.println(isEmpty(list));
		list = null;
		System.out.println(isEmpty(list));
		System.out.println("========");
		printIndex(null);
	}
}
