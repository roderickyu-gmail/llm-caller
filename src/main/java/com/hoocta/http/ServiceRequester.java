package com.hoocta.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.hoocta.core.AsynTaskExcutorUtils;
import com.hoocta.core.ServiceException;
import com.hoocta.llm.constants.BizCode;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.LogUtils;
import com.hoocta.utils.StringUtils;

public class ServiceRequester {
	
	public static ServiceHttpResponse get(String url, Map<String, String> headers,
			Map<String, String> params, boolean longSoTimeoutClient) throws ServiceException {
		return doRequest0(Method.GET, url, headers, params, null, longSoTimeoutClient, false);
	}
	public static ServiceHttpResponse get(String url, Map<String, String> headers,
			Map<String, String> params, boolean longSoTimeoutClient, boolean stream) throws ServiceException {
		return doRequest0(Method.GET, url, headers, params, null, longSoTimeoutClient, stream);
	}
	
	public static ServiceHttpResponse post(String url, Map<String, String> headers,
			Map<String, String> params, String bodyString, boolean longSoTimeoutClient) throws ServiceException {
		return doRequest0(Method.POST, url, headers, params, bodyString, longSoTimeoutClient, false);
	}
	public static ServiceHttpResponse post(String url, Map<String, String> headers,
			Map<String, String> params, String bodyString, boolean longSoTimeoutClient, boolean stream) throws ServiceException {
		return doRequest0(Method.POST, url, headers, params, bodyString, longSoTimeoutClient, stream);
	}
	
	
	private static ServiceHttpResponse doRequest(Method method, String url, Map<String, String> headers,
			Map<String, String> params, String bodyString, boolean longSoTimeoutClient) throws ServiceException {
		return doRequest0(method, url, headers, params, bodyString, longSoTimeoutClient, false);
	}
	private static ServiceHttpResponse doStreamRequest(Method method, String url, Map<String, String> headers,
			Map<String, String> params, String bodyString, boolean longSoTimeoutClient) throws ServiceException {
		return doRequest0(method, url, headers, params, bodyString, longSoTimeoutClient, true);
	}
	
	private static ServiceHttpResponse doRequest0(Method method, String url, Map<String, String> headers,
			Map<String, String> params, String bodyString, boolean longSoTimeoutClient, boolean stream) throws ServiceException {
		CloseableHttpClient httpClient = null;
		if (longSoTimeoutClient) {
			httpClient = HttpClientMgr.getInstance().getLongSoTimeoutClient();
			LogUtils.log("Using long timeout http client");
		}
		else {
			httpClient = HttpClientMgr.getInstance().getClient();
			LogUtils.log("Using short timeout http client");
		}
		ClassicHttpRequest request = null;
		ClassicRequestBuilder builder = null;
		boolean isNeedSetEntity = false;
		if (Method.GET == method) {
			builder = ClassicRequestBuilder.get(url);
		} else {
			// default Method.POST == method
			builder = ClassicRequestBuilder.post(url);
			isNeedSetEntity = true;
		}
		if (isNotEmpty(headers)) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
		}
		if (isNotEmpty(params)) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.addParameter(entry.getKey(), entry.getValue());
			}
		}
		if (isNeedSetEntity && !StringUtils.isBlank(bodyString)) {
			StringEntity entity = new StringEntity(bodyString, StandardCharsets.UTF_8);
			builder.setEntity(entity);
		}
		request = builder.build();
		
		printRequestDetails(request);
		
		ServiceHttpResponse serviceResponse = new ServiceHttpResponse();
		try {
			httpClient.execute(request, response -> {
				HttpEntity entity = response.getEntity();
				if (stream) {
					// 读取响应流并按行处理
					List<String> dataList = new LinkedList<>();
					try (InputStream inputStream = entity.getContent();
							BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
						String line;
						while ((line = reader.readLine()) != null) {
							if (StringUtils.isBlank(line)) {
								continue;
							}
//							System.out.println("Received>>>" + line + "<<<");
							dataList.add(line);
						}
						try {
//							System.out.println("Total Received>>>" + dataList.toString() + "<<<");
							// 注意：这里给 response 的并不是标准 SSE 格式
							serviceResponse.setResponse(JsonUtils.toJson(dataList));
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						// 强制读取响应内容
						String rawResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
						serviceResponse.setResponse(rawResponse);
					} catch (ServiceException e) {
						e.printStackTrace();
					} finally {
						EntityUtils.consumeQuietly(entity); // 确保任何情况下都消费实体
					}
				}

				return null;
			});
			return serviceResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
		
	}
	
	
	private static <T, K> boolean isNotEmpty(Map<T, K> map) {
		return map != null && map.size() > 0;
	}
	
	
	private static void printRequestDetails(ClassicHttpRequest request) {
	    // 打印请求 URL
		LogUtils.appendLogToFile(null, "Actual Request URL: " + request.getRequestUri());
	    // 打印请求方法
//	    LogUtils.log();
		LogUtils.appendLogToFile(null, "Actual Request Method: " + request.getMethod());

	    // 打印请求的 Headers
		LogUtils.appendLogToFile(null, "Actual Request Headers: ");
	    for (Header header : request.getHeaders()) {
	    	LogUtils.appendLogToFile(null, header.getName() + ": " + header.getValue());
	    }

	    // 打印请求的 body 内容
	    if (request.getEntity() != null) {
	        try {
	            String body = EntityUtils.toString(request.getEntity());
	            LogUtils.appendLogToFile(null, "Actual Request Body: " + body);
	        } catch (Exception e) {
	        	System.out.println("Error reading request body: " + e.getMessage());
	        	LogUtils.appendLogToFile(null, "Error reading request body: " + e.getMessage());
	        }
	    } else {
	    	LogUtils.appendLogToFile(null, "Actual Request Body: None");
	    }
	}
	
	public static void main(String[] args) throws ServiceException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		Map<String, String> params = new HashMap<>();
		params.put("id", "0e7a4c10-e077-11ee-8a3d-33ad310ffae2"); 
		params.put("secret", "86437e88-b3c4-4c38-8474-63d93febf3ec");
//		ServiceHttpResponse response = get("https://api.xiaoxizn.com/v1/plan", headers, params, false);
		System.out.println("Stats: " + HttpClientMgr.getStats());
		AsynTaskExcutorUtils.executeTasks(() -> get("https://api.xiaoxizn.com/v1/plan", headers, params, false, true), 10);
//		System.out.println(response);
		System.out.println("Stats: " + HttpClientMgr.getStats());
//		String filepath = "/Users/roderick/Documents/曲女士-项目采购主管(J18965)-原始简历.pdf";
		
//		XiaoxiCVParser.parse(filepath);
		
		System.exit(0);
	}
}
