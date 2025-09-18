package com.hoocta.llm.constants;

public class EncryptSecrets {

	/**
	 * 计算 sig 用的 secret
	 */
	public static final String SECRET_SIG = "s!g24w1s";

	/**
	 * 客户端请求的加密配置（服务端用这套配置解密）
	 */
	public static final String REQUEST_KEY = "l5rgZE4r9uQXfeQD1Hn/xg==";
	public static final String REQUEST_IV = "w1Rzoqg2mOIiMUl/lHN0qQ==";

	/**
	 * 服务器响应的数据加密配置（客户端用这套配置解密）
	 */
	public static final String RESPONSE_KEY = "8dG++EBccjJiwkx8+JqBfw==";
	public static final String RESPONSE_IV = "iCtG97K4hErgxGjJX/CdQw==";

	/**
	 * 服务器对内部各种隐私数据进行加密的配置
	 */
	public static final String SERVER_ENCRYPT_KEY = "ZDHDvu0WskCSqw8tmpkxCg==";
	public static final String SERVER_ENCRYPT_IV = "dYVdClP2Jc0zb/E9dxqBWw==";

}
