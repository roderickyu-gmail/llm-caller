package com.hoocta.http;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;

import com.hoocta.core.ServiceException;
import com.hoocta.llm.constants.BizCode;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.StringUtils;

/**
 * 
 * @author roderickyu
 */
public class ServiceHttpResponse{
	
	private int status = HttpStatus.SC_OK;
	private String content = null;
    private String connectionInfo;

	public ServiceHttpResponse setResponse(ClassicHttpResponse response) throws ServiceException {
		try {
			String rawContent = StringUtils.toString(response.getEntity().getContent());
			setResponse(rawContent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		} 
		return this;
	}
	public ServiceHttpResponse setResponse(String rawResponse) throws ServiceException {
		try {
			this.content = rawResponse.replaceAll("\\r\\n|\\r|\\n", ""); // 去除换行符
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		} 
		return this;
	}
	public int getStatus() {
		return status;
	}
	public String getResponseContent() throws ServiceException {
		return this.content;
	}
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	public String getConnectionInfo() {
		return connectionInfo;
	}
	public void setConnectionInfo(String connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

}
