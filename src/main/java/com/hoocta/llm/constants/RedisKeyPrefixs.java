package com.hoocta.llm.constants;

public interface RedisKeyPrefixs {
	
	public static final String WX_ACCESS_TOKEN = "wx_access_token:";
	
	
	public static final String QR_SESSION_ID = "qr_session_id:";
	

	public static final String DISTRIBUTED_LOCK = "distributed_lock:";
	
	
	public static final String SESSION_OPENID_PAIR = "session_openid:";// session -> openid
	
	public static final String OPENID_USER_PAIR = "openid_user:"; // openid -> user info
	
	public static final String PLATFORM_USER = "platform_user:"; // platform user
	
	public static final String USER= "user:"; // userinfo
	
	public static final String GLOBAL_SQ = "global_sq:"; // generation global id
	
	public static final String USER_ACCESS_TOKEN = "access_token:";// user access token
	
	public static final String USER_CV_IDS = "user_cvids:"; // user & cvids
	
	public static final String CV_CONTENT = "cv_ctt:";// cvid & cv content(for original, new)
	
	public static final String CV_CHECKLIST_RESULT = "cklist_ret:";// checklist result
	
}
