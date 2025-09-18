package com.hoocta.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaver {

	public static void saveToFile(StringBuilder content, String fileName) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(content.toString());
			System.out.println("文件保存成功: " + fileName);
		} catch (IOException e) {
			System.err.println("保存文件时出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 方法1：使用 NIO 文件写入（Java 7+）
	public static void saveWithNIO(StringBuilder content, String fileName) throws IOException {
		Path path = Paths.get(fileName);
		Files.write(path, content.toString().getBytes(StandardCharsets.UTF_8));
	}

	// 方法2：追加模式写入
	public static void appendToFile(StringBuilder content, String fileName) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 方法3：指定编码格式
	public static void saveWithEncoding(StringBuilder content, String fileName, String encoding) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding))) {
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
