package com.hoocta.utils;

public class StatCount {
	
	private int succCount;
	private int failCount;
	public int getSuccCount() {
		return succCount;
	}
	public void setSuccCount(int succCount) {
		this.succCount = succCount;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	public StatCount(int succCount, int failCount) {
		super();
		this.succCount = succCount;
		this.failCount = failCount;
	}
	
	public void incSuccCount(int succCount) {
		setSuccCount(getSuccCount() + succCount); 
	}
	
	public void incFailCount(int failCount) {
		setFailCount(getFailCount() + failCount);
	}

	public static void main(String[] args) {
		StatCount stat = new StatCount(0, 0);
		
		stat.incFailCount(3);
		stat.incSuccCount(4);
		
		stat.incFailCount(10);
		stat.incSuccCount(40);
		
		System.out.println(JsonUtils.toJson(stat));
		
	}
}
