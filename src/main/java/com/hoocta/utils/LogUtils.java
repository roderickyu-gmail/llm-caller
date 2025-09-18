package com.hoocta.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hoocta.core.AsynTaskExcutors;

public class LogUtils {

	private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("errorAndRequestLogger"); // FIXME：为什么是 error logger？
	private static final Logger BIZ_LOGGER = LoggerFactory.getLogger("bizLogger");

	public static void log(String log) {
		AsynTaskExcutors.submit(new Runnable() {

			@Override
			public void run() {
				System.out.println(log);
				REQUEST_LOGGER.info(log);
			}
		});
	}
	
	public static void sysout(String log) {
		System.out.println(log);
		log(log);
	}
	public static void syserr(String log) {
		System.err.println(log);
		log(log);
	}
	
	
	public static void mattersLog(String log) {
		log("***********AttentionLog***********: " + log);
	}
	
	public static void logException(String log, Exception e) {
		REQUEST_LOGGER.error(log, e);
	}

	public static void recordBizLog(long time, String bizLog) {
		AsynTaskExcutors.submit(new Runnable() {
			@Override
			public void run() {
				BIZ_LOGGER.info("{} {}", DateTimeUtils.toTimePatternStr(time), bizLog);
			}
		});
	}

	public static void recordLog(long userId, long time, String event, String placeTag, String result) {
		AsynTaskExcutors.submit(new Runnable() {
			@Override
			public void run() {
				BIZ_LOGGER.info("{} {} {} {} {}", event, placeTag, userId, DateTimeUtils.toTimePatternStr(time),
						result);
			}
		});
	}

	public static void logMap(Map<?, ?> map) {
		AsynTaskExcutors.submit(new Runnable() {

			@Override
			public void run() {
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					REQUEST_LOGGER.info("{}, {}", entry.getKey(), entry.getValue());
				}
			}
		});

	}

	public static synchronized void appendLogToFile(String filePath, String message) {
		if (StringUtils.isBlank(filePath)) {
			String projectPath = Paths.get("").toAbsolutePath().toString();
			filePath = projectPath + "/work.log";
		}
		try (FileWriter fw = new FileWriter(filePath, true); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(message);
			bw.newLine(); // 写入新行
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		Map<String, String> map = new HashMap<>();
//		map.put("aa", "bb");
//		logMap(map);
//
//		// 测试输出
//		loggerConsole.info("This message will be logged to console only.");
//		loggerFile.info("This message will be logged to file only.");
//		loggerBoth.info("This message will be logged to both console and file.");
//		
		appendLogToFile("", "begin-testing");
		String projectPath = Paths.get("").toAbsolutePath().toString();
		System.out.println("Current project directory: " + projectPath);
		 
	}
}
