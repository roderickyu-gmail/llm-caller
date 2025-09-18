package com.hoocta.utils;

public class NameExtension {

	/**
	 * 不带扩展名的文件名
	 */
	private String baseName;
	/**
	 * 文件扩展名
	 */
	private String extension;

	public NameExtension(String baseName, String extension) {
		super();
		this.baseName = baseName;
		this.extension = extension;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
