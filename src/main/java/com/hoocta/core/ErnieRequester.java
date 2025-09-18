package com.hoocta.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.hoocta.http.ServiceHttpResponse;
import com.hoocta.http.ServiceRequester;
import com.hoocta.llm.constants.BizCode;
import com.hoocta.utils.CollectionUtils;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.StringUtils;

/**
 * 百度文心一言，账号 136...1233
 * 
 * @author roderickyu Mar 18, 2024
 */
public class ErnieRequester {

	private static final String API_KEY = ErnieConfig.API_KEY;
	private static final String SECRET_KEY = ErnieConfig.SECRET_KEY;
	private static final String URL_TOKEN = "https://aip.baidubce.com/oauth/2.0/token"; // 获取调用大模型 api 的 access_token 接口
	private static final String URL_COMPLETION = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro"; // 该接口对应文心一言，默认使用的是
																																		// 4.0（pro）
	private static final String URL_TOKENIZER = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/tokenizer/erniebot"; // 计算
																																	// token
																																	// 长度
	private static final String TOKEN_TEMP = "24.dd2c623620fce84800d01855bac12882.2592000.1713512731.282335-57012821";

	private static String getAccessToken() throws ServiceException {
		if (!StringUtils.isBlank(TOKEN_TEMP)) {
			return TOKEN_TEMP;
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", "client_credentials");
		params.put("client_id", API_KEY);
		params.put("client_secret", SECRET_KEY);
		ServiceHttpResponse response = ServiceRequester.post(URL_TOKEN, headers, params, null, false);
		TokenResponse token = JsonUtils.fromJson(response.getResponseContent(), TokenResponse.class);
		if (token == null || !token.isSuccess()) {
			return null;
		}
		return token.getAccessToken();
	}

	public static ErnieResponseSuccessResult complete(String system, List<Message> messages) throws ServiceException {
		if (CollectionUtils.isEmpty(messages)) {
			System.out.println("Empty messages!");
			return null;
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		Map<String, String> params = new HashMap<>();
		params.put("access_token", getAccessToken());
		System.out.println("Ernie token: " + getAccessToken());
		ErnieCompleteParams completeParams = new ErnieCompleteParams();
		completeParams.setMessages(messages);
		completeParams.setMaxOutputTokens(2048);
		completeParams.setSystem(system);
		long now = System.currentTimeMillis();
		ServiceHttpResponse response = ServiceRequester.post(URL_COMPLETION, headers, params,
				JsonUtils.toJson(completeParams), true);
		System.out.println("Time cost: " + (System.currentTimeMillis() - now) / 1000 + "s. ");
		String responseContent = response.getResponseContent();
		if (StringUtils.isBlank(responseContent)) {
			System.out.println("Response Null or Empty value: " + responseContent);
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
		ErnieResponseResult result = ErnieResponseResult.buildResult(responseContent);
		if (result instanceof ErnieErrorResult) {
			System.out.println("API Error: " + result);
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
		ErnieResponseSuccessResult successResult = (ErnieResponseSuccessResult) result;

		System.out.println("completeParams: " + JsonUtils.toJson(completeParams));
		System.out.println("ServiceHttpResponse: " + response.getResponseContent());
		System.out.println("result: " + successResult);
		System.out.println(successResult.getResult());
		return successResult;
	}

	// 调不通，算了
//	public static ErnieResponseSuccessResult tokenizer(String prompt, AIModelName modelName) throws ServiceException {
//		Map<String, String> headers = new HashMap<>();
//		headers.put("Content-Type", "application/json; charset=UTF-8");
//		Map<String, String> params = new HashMap<>();
//		params.put("access_token", getAccessToken());
//		System.out.println("Ernie token: " + getAccessToken());
//		Prompt promptObj = new Prompt();
//		promptObj.setPrompt(prompt);
//		promptObj.setModel(modelName.getName());
//		long now = System.currentTimeMillis();
//		ServiceHttpResponse response = ServiceRequester.post(URL_TOKENIZER, headers, params,
//				JsonUtils.toJson(promptObj), true);
//		System.out.println("Time cost: " + (System.currentTimeMillis() - now) / 1000 + "s. ");
//		String responseContent = response.getResponseContent();
//		if (StringUtils.isBlank(responseContent)) {
//			System.out.println("Response Null or Empty value: " + responseContent);
//			throw new ServiceException(ErrorCode.SERVER_BUSY);
//		}
//		ErnieResponseResult result = ErnieResponseResult.buildResult(responseContent);
//		if (result instanceof ErnieErrorResult) {
//			System.out.println("API Error: " + result);
//			throw new ServiceException(ErrorCode.SERVER_BUSY);
//		}
//		ErnieResponseSuccessResult successResult = (ErnieResponseSuccessResult) result;
//
//		System.out.println("result: " + successResult);
//		return successResult;
//	}

	private class TokenResponse {
		@SerializedName("access_token")
		private String accessToken;
		@SerializedName("expires_in")
		private String expireIn;
		@SerializedName("error")
		private String error; // 说明：响应失败时返回该字段，成功时不返回

		public String getAccessToken() {
			return accessToken;
		}

		@SuppressWarnings("unused")
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		@SuppressWarnings("unused")
		public String getExpireIn() {
			return expireIn;
		}

		@SuppressWarnings("unused")
		public void setExpireIn(String expireIn) {
			this.expireIn = expireIn;
		}

		public String getError() {
			return error;
		}

		@SuppressWarnings("unused")
		public void setError(String error) {
			this.error = error;
		}

		public boolean isSuccess() {
			return StringUtils.isBlank(getError());
		}

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

	public static void main(String[] args) throws ServiceException {
		List<Message> messages = new LinkedList<>();
//		String userMsg = "## 招聘需求\n" + "- \"熟悉决策规划的典型算法\",\n" + "- \"在计算几何、机器人学、最优化理论、数值计算等某一方面具备专长\",\n"
//				+ "- \"精通C++编程，熟悉C++语言的细节与特点\",\n" + "- \"具备丰富的代码开发经验\",\n" + "- \"具备优秀的创新能力、团队协作能力和沟通能力\"\n" + "\n"
//				+ "## 自我评价"
//				+ "1）英文能力 英 ：PTE口语满分，可作为工作语言，无障碍交流（4年英文本科、6年外企、2年海外留学经历）； 2）学习能力 学 ： • 半年时间，从PTE61分（相当于雅思6.5分）提高至73分（相当于雅思7.5分）； • 留学期间，在赚取生活费、学费和实习的同时，还要兼顾学业，在悉尼大学学术要求非常严苛的情况下，完成全部英文课程考试； 3）执行力 执 ： • 坚持健身6年，拳击4年； • 大学期间，连续4年早晨5点起床练习口语；留学前备考，每天早晨6点起床，凌晨2点左右睡觉，无间断坚持半年； • 2年留学期间，同时要做3件事：学业、校外打工和跨境电商，最终通过所有严苛考试，独立赚取学费生活费，成立了34人团队， 有效陌拜400+客户，并取得了年约300万人民销售额。 4）抗压能力 抗 ： • 入学语言考试难度较高，没有气馁，最终以雅思7.5分顺利通过，单科写作取得7.5分的好成绩。相信不断复盘，将难题分解，可以 逐一攻克； • 留学期间，在没有学费、生活费、完全陌生，及非母语的环境中，完成独立赚取全部学费和生活费。"
//				+ "\n" + "## 注意\n" + "- 按照「OutputFormat」直接返回 JSON 结果，不要有解释说明等任意其他字符内容。\n" + "";
		String userMsg = "## 招聘需求\n"
				+ "## 自我评价\n"
				+ "有团队合作精神，对工作尽职尽责，有良好的沟通，协调及组织能力，性格开朗，抗压性强，乐于学习新知识，学习能力强。\n"
				+ "\n"
				+ "## Steps\n"
				+ "1、分析「自我评价」和「招聘需求」（若有）。\n"
				+ "2、按「常规检查」及「针对性检查」中定义步骤执行检查。\n"
				+ "3、校验判断结果：是否存在自己漏判、误判的情况。\n"
				+ "4、按「OutputFormat」输出结果。\n"
				+ "\n"
				+ "## 注意\n"
				+ "- 按照「OutputFormat」直接返回JSON结果，不要有解释说明等任意其他字符内容。\n"
				+ "- 若「招聘要求」为空，则match字段没必要赋值。\n"
				+ "- 若字段代表没问题，则不返回该字段/该字段的对象。"
				+ "";
		messages.add(Message.buildMessage(LLMRole.USER, userMsg));
//		for (int i = 0 ; i < 10; i++) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						System.out.println(Thread.currentThread().getName() + ": " + complete(messages));
//					} catch (ServiceException e) {
//						e.printStackTrace();
//					}
//				}
//			}, "thread-" + i).start();
//		}
		String system = "## Role\n"
				+ "你是资深的面试官，能从「自我评价」中找出有哪些「常见问题」和是否针对性。\n"
				+ "\n"
				+ "## 常见问题\n"
				+ "1.过于泛泛而谈，缺乏具体事例\n"
				+ "-例: \"我是一个非常有责任心的人，无论做什么事情都会全力以赴。\"（缺少具体如何展现责任心的例子）\n"
				+ "2.态度不够端正：出现了对待求职的态度轻浮、不严肃、消极、骂人、有脏话、过于自负自吹自擂的、过于谦虚表达。\n"
				+ "-例: \"我是团队中最出色的成员，总能带领团队取得成功。\"\n"
				+ "3.啰嗦重复，没有突出重点\n"
				+ "-例: \"我负责过项目管理，管理过项目，做过项目的全面负责人。\"（重复表达同一意思）\n"
				+ "4. 逻辑不清，前后矛盾\n"
				+ "-例: \"我是一个喜欢团队合作的人，独立工作能力也很强。\"（虽然两者不完全矛盾，但这样的表述容易让人感到混乱，不知道候选人的优势究竟是团队合作还是独立工作）\n"
				+ "5.表达过于简单，缺少深度\n"
				+ "-例: \"我是一个很好的人。\"（这种评价过于笼统，没有提供任何支持自己观点的细节或例证）\n"
				+ "6. 显得过于谦虚，未能有效突出个人优势\n"
				+ "-例: \"我只是做了我应该做的事情。\"（这种说法可能让人觉得你没有做出过超出期望的贡献）\n"
				+ "7.过分使用专业术语或不明确的概念\n"
				+ "-例: \"我在利用大数据分析和机器学习技术进行预测性建模方面具有深入的理解。\"（如果不是针对特定的专业岗位，过多的专业术语可能让非专业的HR感到难以理解）\n"
				+ "8.忽略了结果和成就的描述\n"
				+ "-例: \"我参与了多个项目的开发工作。\"（没有说明这些项目的成果或个人在项目中的具体贡献）\n"
				+ "9. 表达过于模糊，缺少可量化的指标\n"
				+ "-例: \"我极大地提高了公司的效率。\"（没有提供具体的数字或百分比来支持这一点）\n"
				+ "10. 过分依赖通用模板，缺乏个性化\n"
				+ "-例: \"我是一个团队合作者，也能独立工作。\"（这种描述过于通用，几乎适用于任何求职者，缺乏区分度）\n"
				+ "11. 过度强调需求而非贡献\n"
				+ "-例: \"我希望找到一个周末双休的工作。\"（过分强调个人需求，而不是能为公司带来的价值）\n"
				+ "12.可能泄露隐私\n"
				+ "-泄露了商业机密，比如注册用户数量、活跃用户数量（DAU、MAU等）、收入等敏感信息用了具体的「绝对数」表示。\n"
				+ "-建议用百分比的量化数字表示，避免泄露商业机密。\n"
				+ "\n"
				+ "## 常规检查\n"
				+ "1、分析并根据人才「自我评价」内容，判断有「常见问题」中的哪些问题。\n"
				+ "2、记住问题名字、问题原文/词语、问题原因。\n"
				+ "\n"
				+ "## 针对性检查\n"
				+ "1、若「招聘内容」为空，则不执行该检查。\n"
				+ "2、分析并根据人才的「自我评价」，来判断该人才自我评价中匹配情况：满足了什么要求、未满足哪些要求。\n"
				+ "3、检查：满足的要求和未满足的要求中，不要有逻辑冲突。\n"
				+ "4、点评：针对满足的要求，做简评。针对未满足的要求做简练清晰的解释说明。\n"
				+ "（1）为什么认为自我评价中没有满足该需求。\n"
				+ "（2）应该怎么该写才可以满足该需求。给具体的建议，指明编写方向。\n"
				+ "\n"
				+ "## OutputFormat\n"
				+ "- JSON格式：{\"sum\":\"\",\"match\":{\"fit\":[\"\"],\"lack\":[{\"name\":\"\",\"why\":\"\",\"should\":\"\"},{}]},\"issues\":[{\"name\":\"\",\"text\":\"\",\"why\":\"\",\"bad\":true/false},{}]}\n"
				+ "## Defination\n"
				+ "- sum：概括「自我评价」及候选人的优点，最多50字\n"
				+ "- match：「针对性检查」结果\n"
				+ "-- match.fit: 人才已满足的招聘要求的列表，字符数组。\n"
				+ "-- match.lack：人才未满足的招聘要求列表，对象数组。\n"
				+ "-- match.name：人才未满足的某个招聘要求的名称\n"
				+ "-- match.why：认为其有问题的原因\n"
				+ "-- match.should：针对避免该问题的编写建议\n"
				+ "- issues：「常规检查」检查出有问题地方，对象数组类型\n"
				+ "-- issues.name：某一问题名（如：逻辑不清），字符串类型\n"
				+ "-- issues.text：出问题的地方原文、原词语\n"
				+ "-- issues.why：对为什么被认定有问题的解释\n"
				+ "-- issues.bad：该项是否有问题，bool类型，true=是\n"
				+ "";

		complete(system, messages);

//		tokenizer(system, AIModelName.ERNIE4);
	}

}
