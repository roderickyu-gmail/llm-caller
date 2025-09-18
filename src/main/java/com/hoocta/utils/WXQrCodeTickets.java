package com.hoocta.utils;

/**
 * 维护所有的永久带参二维码
 * 前端拿到 ticket 通过该域名来请求二维码图片：https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=$ticket
 * @author roderickyu Jul 9, 2024
 */
public class WXQrCodeTickets {
	// 用于测试的永久带参数二维码（已 URLEncode）
	public static final String TICKETS_URLENCODED_SCENE_TEST = "gQEt8jwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyTUpYQ0FwRVplM0YxMDAwMDAwN1gAAgS_C41mAwQAAAAA";
	
	// 首页主动点击注册登录的场景（已 URLEncode）
	public static final String TICKETS_URLENCODED_HOMEPAGE_SCENE_LOGIN_PROACTIVE = "gQFS8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAybkxadUJHRVplM0YxMDAwME0wN0wAAgQBYo5mAwQAAAAA";

	
}
