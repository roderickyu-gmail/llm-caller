package com.hoocta.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetUtils {
	
	
	public static <K, V> Set<V> toValueSet(Map<K, V> map) {
		Set<V> retSet = new HashSet<>();
		if (CollectionUtils.isEmpty(map)) {
			return retSet;
		}
		for (V v : map.values()) {
			retSet.add(v);
		}
		return retSet;
	}

	public static <K, V> Set<V> toCollectionValueSet(Map<K, ? extends Collection<V>> map) {
		Set<V> retSet = new HashSet<>();
		if (CollectionUtils.isEmpty(map)) {
			return retSet;
		}
		for (Collection<V> c : map.values()) {
			for (V v : c) {
				retSet.add(v);
			}
		}
		return retSet;
	}

	public static void main(String[] args) {
		Map<String, List<String>> map = new HashMap<>();
		map.put("A", Arrays.asList("a", "b"));
		map.put("B", Arrays.asList("c", "b"));
		
		Set<String> ret = toCollectionValueSet(map);
		System.out.println(ret);
	}
}
