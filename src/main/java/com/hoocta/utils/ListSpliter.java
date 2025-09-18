package com.hoocta.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListSpliter {

	 /**
     * 将列表按指定大小分割成子列表
     * @param tasks 原始任务列表
     * @param count 每组最大任务数
     * @return 分组后的任务列表
     */
	public static List<List<String>> splitTasksIntoGroups(List<String> tasks, int count) {
		Collections.shuffle(tasks);// 重置一下顺序，非常重要。很多挨着的 task 在之前可能已经保证过不重复。
		int groupCount = (tasks.size() + count - 1) / count; // 计算分组数
		List<List<String>> subTasksList = new ArrayList<>();

		for (int i = 0; i < groupCount; i++) {
			int start = i * count;
			int end = Math.min(start + count, tasks.size());
			// 创建子列表，避免直接使用 subList 以防止原始列表修改影响
			List<String> subTasks = new ArrayList<>(tasks.subList(start, end));
			subTasksList.add(subTasks);
		}
		return subTasksList;
	}
	
	public static void main(String[] args) {
		List<String> list = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
		List<List<String>> retList = splitTasksIntoGroups(list, 2);
		System.out.println(JsonUtils.toJson(retList));
//		System.out.println(JsonUtils.toJson(retList));
	}
}
