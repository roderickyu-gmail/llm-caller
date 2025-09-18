package com.hoocta.http;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;

import com.hoocta.core.ServiceException;
import com.hoocta.utils.StringUtils;

/**
 * 
 * @author roderickyu
 */
public class HttpClientMgr {

	private static final HttpClientMgr INSTANCE = new HttpClientMgr();
	private final CloseableHttpClient client;
	private final CloseableHttpClient longSoTimeoutClient;

	private HttpClientMgr() {
		this.client = createClient((ConnectionConfig.custom().setConnectTimeout(2000, TimeUnit.MILLISECONDS)
				.setSocketTimeout(3000, TimeUnit.MILLISECONDS).build()));
		this.longSoTimeoutClient = createClient(
				(ConnectionConfig.custom().setConnectTimeout(2000, TimeUnit.MILLISECONDS)
						.setSocketTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS).build())); // 5 分钟就可以了。
	}
	private static PoolingHttpClientConnectionManager CM;

	private static CloseableHttpClient createClient(ConnectionConfig config) {
		// 创建连接池管理器，并设置其配置
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// 设置最大连接数（TODO：总连接数可以调的大一些。1C1G 服务器单台差不多 1000 左右。）
		cm.setMaxTotal(1000);
		// 设置每个路由（特定目标主机）允许的最大连接数（TODO：单个路由可以开的稍微大一些，暂定开到最大连接数）
		cm.setDefaultMaxPerRoute(1000);
		cm.setDefaultConnectionConfig(config);
		cm.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).build());
		// 创建HttpClient
		CM = cm;
		return HttpClients.custom().setConnectionManager(cm).evictIdleConnections(TimeValue.ofSeconds(30)) // 定期回收空闲连接
				.evictExpiredConnections() // 定期回收过期连接
				.build();
	}

	public static PoolStats getStats() {
		return CM.getTotalStats();
	}
	
	public static HttpClientMgr getInstance() {
		return INSTANCE;
	}

	public CloseableHttpClient getClient() {
		return this.client;
	}

	public CloseableHttpClient getLongSoTimeoutClient() {
		return this.longSoTimeoutClient;
	}

	public static void main(String[] args) throws Exception {

		// 示例使用HttpClientMgr获取HttpClient并执行请求
		CloseableHttpClient httpClient = HttpClientMgr.getInstance().getClient();
		ClassicHttpRequest httpGet = ClassicRequestBuilder.get("https://www.baidu.com").build();

		httpClient.execute(httpGet, response -> {
			System.out.println(response.getCode() + " " + response.getReasonPhrase());
			final HttpEntity entity1 = response.getEntity();
			InputStream is = entity1.getContent();
			System.out.println(StringUtils.toString(is));

			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity1);
			return null;
		});

		ClassicHttpRequest httpPost = ClassicRequestBuilder.post("http://httpbin.org/post")
				.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("username", "vip"),
						new BasicNameValuePair("password", "secret"))))
				.build();
		httpClient.execute(httpPost, response -> {
			System.out.println(response.getCode() + " " + response.getReasonPhrase());
			final HttpEntity entity2 = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity2);
			return null;
		});

		httpClient.execute(httpGet, response -> {
			try {
				ServiceHttpResponse sr = new ServiceHttpResponse();
				System.out.println("==> " + sr.setResponse(response).getResponseContent());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		});

//		Content content = Request.Get("https://www.baidu.com").execute().returnContent();
//		System.out.println(content.asString());
//		content = Request.Post("https://www.baidu.com")
//				.bodyForm(Form.form().add("username", "vip").add("password", "secret").build()).execute()
//				.returnContent();
//		System.out.println(content.asString(Charset.forName("utf-8")));
	}
}
