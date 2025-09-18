package com.hoocta.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PemUtilReader {
	public static String extractResourceToFile(String resourcePath, String targetFilePath) {
		// 获取资源文件的输入流
		InputStream resourceStream = PemUtilReader.class.getResourceAsStream(resourcePath);
		// 创建指定路径的文件
		File targetFile = new File(targetFilePath);
		if (targetFile.exists()) {
			targetFile.delete(); // 如果文件已存在，删除它
		}
		System.out.println("targetFile: " + targetFile.getAbsolutePath());
//		targetFile.getParentFile().mkdirs(); // 确保目录存在

		// 将资源文件内容写入目标文件
		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = resourceStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 返回目标文件的绝对路径
		return targetFile.getAbsolutePath();
	}

	public static void main(String[] args) {
		try {
			// 指定保存文件的路径，例如：当前工作目录下的 fixed_location.pem
			String keyPath = extractResourceToFile("/pay/wx/apiclient_key.pem", "fixed_location.pem");

			System.out.println(keyPath);
			// 传递提取的文件路径
			FileInputStream inputStream = new FileInputStream(keyPath);
			// 现在你可以使用 inputStream 来读取文件内容了

			// 示例：打印文件内容
			String content = new String(inputStream.readAllBytes());
			System.out.println("Private Key Content:\n" + content);

			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
