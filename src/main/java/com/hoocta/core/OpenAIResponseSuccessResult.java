package com.hoocta.core;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.hoocta.utils.JsonUtils;

public class OpenAIResponseSuccessResult extends OpenAIResponseResult {
	
	
	private String id;
	private int created;
	private String model;
	@SerializedName("system_fingerprint")
	private String systemFingerprint;
	private Usage usage;
	private List<Choice> choices;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSystemFingerprint() {
		return systemFingerprint;
	}
	public void setSystemFingerprint(String systemFingerprint) {
		this.systemFingerprint = systemFingerprint;
	}
	public Usage getUsage() {
		return usage;
	}
	public void setUsage(Usage usage) {
		this.usage = usage;
	}
	public List<Choice> getChoices() {
		return choices;
	}
	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	
}
