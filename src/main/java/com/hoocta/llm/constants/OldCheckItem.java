package com.hoocta.llm.constants;

/**
 * 实际的检查项。后端按照 section 来去检查，实际展示时可以不展示 section，直接展示各个问题项。
 * @author roderickyu Jul 17, 2024
 */
public class OldCheckItem {
	public static final OldCheckItem BASIC_MISSING_RUIQUIRED_INFO 			= new OldCheckItem(1, "basic_info", "是否缺少基本必备信息（姓名、邮箱、电话）");
	public static final OldCheckItem BASIC_PRIVACY_LEAKS 				= new OldCheckItem(2, "basic_info", "是否有与求职无关的隐私泄露");
	public static final OldCheckItem BASIC_UNPROFESSIONAL_EMAIL_NAME 	= new OldCheckItem(3, "basic_info", "是否邮箱名不够专业");
	
	private int itemId;
	private String section;
	private String itemDesc;
	
	public OldCheckItem(int id, String section, String itemDesc) {
		super();
		this.itemId = id;
		this.section = section;
		this.itemDesc = itemDesc;
	}
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getItem() {
		return itemDesc;
	}
	public void setItem(String item) {
		this.itemDesc = item;
	}
}
