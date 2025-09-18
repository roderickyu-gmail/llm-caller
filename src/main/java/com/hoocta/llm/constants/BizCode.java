package com.hoocta.llm.constants;

public interface BizCode {
	int SUCCESS = 1;

	// 1~99 系统级错误
	int SERVER_BUSY = 10;

	// 100~199 参数不合法
	int MISSING_REQUIRED_PARAM = 100;
	int PARAM_INVALID = 101;
	int TOKEN_INVALID = 102;
	
	// 200 业务判断
	int COIN_IS_NOT_ENOUGH = 200;

}
