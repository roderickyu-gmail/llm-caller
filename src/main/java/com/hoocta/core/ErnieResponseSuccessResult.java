package com.hoocta.core;

import com.google.gson.annotations.SerializedName;
import com.hoocta.utils.JsonUtils;

public class ErnieResponseSuccessResult extends ErnieResponseResult{

	private String id;
	private String object; // always be chat.completion
	private int created;
	@SerializedName("is_truncated")
	private boolean isTruncated;
	@SerializedName("finish_reason")
	private String finishReason;
	private String result;
	@SerializedName("need_clear_history	")
	private boolean needClearHistory;
	private int flag;
	private Usage usage;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public boolean isTruncated() {
		return isTruncated;
	}
	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}
	public String getFinishReason() {
		return finishReason;
	}
	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public boolean isNeedClearHistory() {
		return needClearHistory;
	}
	public void setNeedClearHistory(boolean needClearHistory) {
		this.needClearHistory = needClearHistory;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public Usage getUsage() {
		return usage;
	}
	public void setUsage(Usage usage) {
		this.usage = usage;
	}
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	
	/**
	 * @return true，如果 AI 返回了所有要返回的内容。
	 */
	public boolean isCompleteMsg() {
		return isTruncated() && ErnieFinishReason.NORMAL.getReason().equals(getFinishReason());
	}
	
}
