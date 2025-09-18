package com.hoocta.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 处理 map 常用方法工具
 * @author roderickyu Nov 29, 2024
 */
public class MapUtils {
	
	public static <T> void inc(Map<T, Long> counterMap, T t, long inc) {
		Long count = counterMap.get(t);
		if (count == null) {
			count = 0L;
		}
		count += inc;
		counterMap.put(t, count);
	}
	
	public static <K, V> List<V> addValusToList(Map<K, ? extends Collection<V>> map) {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		List<V> ret = new LinkedList<>();
		for (Collection<V> collection : map.values()) {
			if (CollectionUtils.isEmpty(collection)) {
				continue;
			}
			ret.addAll(collection);
		}
		return ret;
	}
	
	public static Map<String, Long> inc(Object obj) {
		Map<String, Long> map = new HashMap<>();
		if (obj == null) {
			return map;
		}
		processElementForStrings(obj, map);
		return map;
	}
	
	private static void processElementForStrings(Object element, Map<String, Long> map) {
	    if (element == null) {
	        return;
	    }
	    if (element instanceof Collection) {
	        for (Object item : (Collection<?>) element) {
	            processElementForStrings(item, map);
	        }
	    } else if (element.getClass().isArray()) {
	        int length = java.lang.reflect.Array.getLength(element);
	        for (int i = 0; i < length; i++) {
	            Object item = java.lang.reflect.Array.get(element, i);
	            processElementForStrings(item, map);
	        }
	    } else if (element instanceof String) {
	        String str = (String) element;
	        inc(map, str, 1);
	    } else {
	    	inc(map, element.toString(), 1);
	    }
	}
	
	public static <K, V> void print(Map<K, V> map) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}
	
	public static <K, V> List<V> findBy(List<K> keys, Map<K, V> map) {
		if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(map)) {
			return null;
		}
		Set<V> set = new HashSet<>();
		for (K k : keys) {
			V v = map.get(k);
			if (v != null) {
				set.add(v);
			}
		}
		return new ArrayList<>(set);
	}
	
	/**
	 * Map 中 value 去重器。
	 * 
	 * 对给定的 Map<K, List<V>>：  
	 * 若多个 key 的列表中包含相同的元素 V，则需要将该元素仅保留在某一个 key 对应的结果列表中。  
	 * 分配规则：  
	 * 1. 若元素只在单一 key 中出现，则直接保留。  
	 * 2. 若元素出现在多个 key 中，则先根据原始列表大小 (listSizes) 选择原始 size 最小的 key。  
	 * 3. 若初选的 key 对应的结果列表中已经存放了较多元素，则可能再次比较其余候选 key 在 result 中已经存放的数量，  
	 *    尝试选择当前 result 列表更短的 key 来放置该元素，从而达到更均衡的分配效果。
	 *    
	 */
	public static <K, V> Map<K, List<V>> removeDuplicates(Map<K, List<V>> originMap) {
        // 结果Map
        Map<K, List<V>> result = new HashMap<>();
        
        // 用来记录每个元素出现在哪些key中
        Map<V, Set<K>> elementToKeys = new HashMap<>();
        
        // 记录每个key对应list的大小
        Map<K, Integer> listSizes = new HashMap<>();
        
        // 第一遍扫描，建立元素到key的映射
        for (Map.Entry<K, List<V>> entry : originMap.entrySet()) {
            K key = entry.getKey();
            List<V> list = entry.getValue();
            
            // 保存list大小
            listSizes.put(key, list.size());
            // 初始化结果Map
            result.put(key, new ArrayList<>());
            
            // 记录每个元素出现在哪些key中
            for (V element : list) {
                Set<K> keys = elementToKeys.get(element);
                if (keys == null) {
                    keys = new HashSet<>();
                    elementToKeys.put(element, keys);
                }
                keys.add(key);
            }
        }
        
        // 处理每个元素
        for (Map.Entry<V, Set<K>> entry : elementToKeys.entrySet()) {
            V element = entry.getKey();
            Set<K> keys = entry.getValue();
            
            if (keys.size() == 1) {
                // 如果元素只出现在一个list中，直接添加
                K key = keys.iterator().next();
                result.get(key).add(element);
            } else {
                // 如果元素出现在多个list中，找出size最小的list
                K fistMinListKey = null;
                int minSize = Integer.MAX_VALUE;
                
                for (K key : keys) {
                    int size = listSizes.get(key);
                    if (size < minSize) {
                        minSize = size;
                        fistMinListKey = key;
                    }
                }
                
                int preResultListSize = result.get(fistMinListKey).size();
                K finalMinSizeRetListKey = fistMinListKey;
                if (preResultListSize > 0) {
                	// 已经有数据了，那么就看看这个 value 对应的其他 key 是否还有没有没存过数据的、存的相对比较少的。
                	for (K key : keys) {
                    	if (key.equals(fistMinListKey)) {
                    		continue;
                    	}
                    	int size = result.get(key).size();
                    	if (size < preResultListSize) {
                    		preResultListSize = size;
                    		finalMinSizeRetListKey = key;
                    	}
                    }
                }
                // 将元素添加到选中的list中
                result.get(finalMinSizeRetListKey).add(element);
            }
        }
        
        return result;
    }
    
	/**
	 * 将 map 中的 value 为空（null or size == 0）的 key 删掉
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <K, V extends Collection<?>> Map<K, V> removeEmptyValue(Map<K, V> map) {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		Map<K, V> retMap = new HashMap<>();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			V value = entry.getValue();
			if (CollectionUtils.isEmpty(value)) {
				continue;
			}
			retMap.put(entry.getKey(), value);
		}
		return retMap;
	}
	
	public static void main(String[] args) {
//		JdKeyWord[name = 角色塑造, desc = 角色塑造的能力指的是演员在表演过程中能够深入理解和诠释角色的特性、 情感和背景， 从而使角色在舞台上生动真实。 这个能力要求演员具备对角色的全面分析和理解， 能够通过肢体语言、 声音变化和情感表达等手段， 将角色的内心世界和外在表现完美结合， 使观众能够感受到角色的真实存在和情感波动。 角色塑造不仅仅是模仿或表面上的表演， 更是对角色内在深度的挖掘和展现。], [在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
//				JdKeyWord[name = 即兴表演能力, desc = 即兴表演能力是指演员在没有事先准备或排练的情况下， 能够迅速反应并创造出符合情境的表演内容的能力。 这种能力要求演员具备敏锐的观察力、 快速的思维反应以及良好的沟通技巧， 以便在表演过程中与其他演员或观众进行互动。 即兴表演不仅能够增强演员的表现力， 还能提升其适应不同场景和角色的灵活性， 是剧场表演中一项重要的技能。], [在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
//				JdKeyWord[name = 声音控制, desc = 声音控制是指演员在表演过程中对自己声音的调节和运用能力。 这包括对音量、 音调、 语速、 语气等方面的掌控， 以便更好地传达角色的情感和意图。 优秀的声音控制能够帮助演员在不同的表演场景中， 适应角色的需求， 增强舞台表现力， 使观众更容易理解和感受到角色的内心世界。 通过声音的变化， 演员可以有效地吸引观众的注意力， 营造出特定的氛围， 提升整体表演的感染力和表现力。], [在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
//				JdKeyWord[name = 舞台表现力, desc = 舞台表现力是指演员在舞台上通过肢体语言、 面部表情、 声音和情感的表达， 能够有效地传达角色的内心世界和故事情节的能力。 这种能力不仅包括对角色的理解和诠释， 还涉及到如何与观众建立情感联系， 使观众能够感受到角色的情感变化和故事的发展。 舞台表现力是演员在表演中展现个性和魅力的重要因素， 能够增强表演的感染力和真实感。], [在演出中使用新颖的音乐伴奏， 提升了整体表演的感染力， 观众对音乐的满意度评分达到4 .9。, 在演出现场设置互动环节， 观众参与度提升， 现场反馈表明80 % 的观众愿意再次观看类似演出。, 在演出中设置观众提问环节， 成功回答50个观众问题， 增强了观众的参与感和满意度。, 在一次评弹演出中， 通过精确的乐器伴奏和与演员的默契配合， 使得演出获得观众好评， 现场观众满意度达到95 % 。, 在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
//				JdKeyWord[name = 表演技巧, desc = 表演技巧是指演员在舞台上通过身体语言、 面部表情、 声音和情感表达等多种方式， 生动地传达角色的内心世界和故事情节的能力。 这包括对角色的深入理解和诠释， 能够在不同情境下灵活运用各种表演方法， 以吸引观众的注意力并引发共鸣。 优秀的表演技巧能够使演员在演出中展现出真实而富有感染力的表演， 提升整体演出效果。], [在演出中使用新颖的音乐伴奏， 提升了整体表演的感染力， 观众对音乐的满意度评分达到4 .9。, 在演出现场设置互动环节， 观众参与度提升， 现场反馈表明80 % 的观众愿意再次观看类似演出。, 通过与地方广播电台合作， 进行演出宣传， 收听率提升至3000人次， 扩大了演出影响力。, 在演出前进行观众需求调研， 调整节目内容， 使得观众满意度从75 % 提升至92 % 。, 实施分组排练策略， 提升小组间的协作， 最终使演出整体质量提升， 观众好评率达到85 % 。, 在演出中设置观众提问环节， 成功回答50个观众问题， 增强了观众的参与感和满意度。, 对演出效果进行定期评估， 制定了改进计划， 成功将演出质量评分从4 .2 提升至4 .6。, 在一次评弹演出中， 通过精确的乐器伴奏和与演员的默契配合， 使得演出获得观众好评， 现场观众满意度达到95 % 。, 在声乐训练中， 运用多种演唱风格， 成功在校内比赛中获得最佳表演奖， 参赛选手超过40人。, 在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
//				JdKeyWord[name = 剧本理解, desc = 剧本理解的能力指的是演员对剧本内容、 情节、 角色关系和主题的深入理解和把握。 这种能力使演员能够准确地诠释角色， 理解角色在故事中的发展和变化， 从而在表演中传达出更深层次的情感和意图。 具备良好的剧本理解能力的演员能够分析剧本中的细节， 识别关键情节， 并将这些元素融入到他们的表演中， 以增强观众的体验和共鸣。], [在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。]
		Map<String, List<String>> map = new HashMap<>();
//		map.put("角色塑造", Arrays.asList("在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("即兴表演能力", Arrays.asList("在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("声音控制", Arrays.asList("在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("舞台表现力", Arrays.asList("在演出中使用新颖的音乐伴奏， 提升了整体表演的感染力， 观众对音乐的满意度评分达到4 .9。", " 在演出现场设置互动环节， 观众参与度提升， 现场反馈表明80 % 的观众愿意再次观看类似演出。", " 在演出中设置观众提问环节， 成功回答50个观众问题， 增强了观众的参与感和满意度。", " 在一次评弹演出中， 通过精确的乐器伴奏和与演员的默契配合， 使得演出获得观众好评， 现场观众满意度达到95 % 。", " 在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("表演技巧", Arrays.asList("在演出中使用新颖的音乐伴奏， 提升了整体表演的感染力， 观众对音乐的满意度评分达到4 .9。, 在演出现场设置互动环节， 观众参与度提升， 现场反馈表明80 % 的观众愿意再次观看类似演出。, 通过与地方广播电台合作， 进行演出宣传， 收听率提升至3000人次， 扩大了演出影响力。, 在演出前进行观众需求调研， 调整节目内容， 使得观众满意度从75 % 提升至92 % 。, 实施分组排练策略， 提升小组间的协作， 最终使演出整体质量提升， 观众好评率达到85 % 。, 在演出中设置观众提问环节， 成功回答50个观众问题， 增强了观众的参与感和满意度。, 对演出效果进行定期评估， 制定了改进计划， 成功将演出质量评分从4 .2 提升至4 .6。, 在一次评弹演出中， 通过精确的乐器伴奏和与演员的默契配合， 使得演出获得观众好评， 现场观众满意度达到95 % 。, 在声乐训练中， 运用多种演唱风格， 成功在校内比赛中获得最佳表演奖， 参赛选手超过40人。, 在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("剧本理解", Arrays.asList("在说书表演中， 运用声乐技巧与情感表达相结合， 使得角色的情感波动更加真实， 观众反馈中90 % 表示被深深打动。"));
//		map.put("A", null);
//		map.put("B", new ArrayList<>());
//		
		
		map = new HashMap<>();
		map.put("A", Arrays.asList("1", "2"));
		map.put("B", Arrays.asList("3", "4"));
		map.put("C", Arrays.asList("5", "B"));
		map.put("D", null);
		
		List<String> ret = addValusToList(map);
		CollectionUtils.print(ret);
		
	}
}
