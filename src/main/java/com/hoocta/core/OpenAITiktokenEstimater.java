package com.hoocta.core;

import java.util.LinkedList;
import java.util.List;

import com.hoocta.utils.CollectionUtils;
import com.hoocta.utils.StringUtils;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

/**
 * Token 估算器
 * Online: https://platform.openai.com/tokenizer
 * @author roderickyu Mar 19, 2024
 */
public class OpenAITiktokenEstimater {
	
	private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
	// Get encoding for a specific model via type-safe enum
	private static final Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4);
	
	
	public static int countTokens(List<Message> messages, String lastResponseMsg) {
		int count = 0;
		if (!CollectionUtils.isEmpty(messages)) {
			for (Message msg : messages) {
				if (!StringUtils.isBlank(msg.getContent())) {
					count += encoding.countTokens(msg.getContent());
				}
			}
		}
		if (!StringUtils.isBlank(lastResponseMsg)) {
			count += encoding.countTokens(lastResponseMsg);
		}
		return count;
	}
	
	public static void main(String[] args) {
		System.out.println(countTokens(null, "This is a sample sentence."));
		System.out.println(countTokens(null, "hello &lt;|endoftext|&gt; world"));
		System.out.println(encoding.countTokensOrdinary("hello &lt;|endoftext|&gt; world"));
		System.out.println("=====");
		List<Message> messages = new LinkedList<>();
		messages.add(Message.buildMessage(LLMRole.USER, "This is a sample sentence."));
		System.out.println(countTokens(messages, null));
		System.out.println("=====");
		messages = new LinkedList<>();
		messages.add(Message.buildMessage(LLMRole.USER, "hello &lt;|endoftext|&gt; world"));
		System.out.println(countTokens(messages, null));
		System.out.println("=====");
		messages = new LinkedList<>();
		messages.add(Message.buildMessage(LLMRole.USER, "hello &lt;|endoftext|&gt; world"));
		messages.add(Message.buildMessage(LLMRole.ASISTANT, "This is a sample sentence."));
		messages.add(Message.buildMessage(LLMRole.SYSTEM, "hello &lt;|endoftext|&gt; world"));
		System.out.println(countTokens(messages, null));
		
		messages = new LinkedList<>();
		messages.add(Message.buildMessage(LLMRole.USER, "You are an experienced resume optimizer who understands the needs of corporate recruiters. By reviewing job seekers' resumes, you can offer reasonable, targeted, and effective suggestions for improving the quality of their resumes.\n"
				+ "\n"
				+ "Steps:\n"
				+ "\n"
				+ "Analyze the content given to you, which includes the target position and the job seeker's self-evaluation.\n"
				+ "Identify which of the following \"common issues\" (multiple selections possible) occur and remember the names of these issues (before the colon).\n"
				+ "If there are no issues, directly proceed to \"output results.\" If there are issues, begin the following steps.\n"
				+ "List the 10 abilities that are most helpful in excelling at the target position and showing great potential.\n"
				+ "For these abilities, write 3 directly usable examples for each, following the \"example standards.\"\n"
				+ "Common Issues:\n"
				+ "\n"
				+ "Too generalized evaluations. For example, good at communication, lively, capable, loves learning, steady, generous, serious, cheerful, confident, approachable, sincere, responsible, communication and coordination, responsibility, career-minded, affability, decision-making ability, planning ability, negotiation ability, inspirational and cohesive, etc.\n"
				+ "Too verbose. This type of resume is characterized by a bunch of adjectives and evaluations but rarely gets to the point and lacks quantifiable data.\n"
				+ "Over-exaggeration: Uses adjectives like \"proficient\" that are quite exaggerated and few people can achieve without matching evidence to prove it.\n"
				+ "Negative vocabulary: For instance, not willing to work overtime, cannot withstand pressure, etc., which have a negative impact on work.\n"
				+ "Improper attitude: The description shows that the job seeker does not take job searching seriously, with words showing disdain, impoliteness, condescension, cynicism, demeaning others or companies, etc.\n"
				+ "No factual evidence for these abilities: These facts include specific examples of things, specific certificates, specific achievements, specific quantifiable data, etc.\n"
				+ "Mismatch with the target position: These traits, skills, and doing well in the target job have no direct causal relationship.\n"
				+ "Example Standards:\n"
				+ "\n"
				+ "With data: Quantify work tasks and achievements, quantify quality evaluations, proficient in using numbers and percentages.\n"
				+ "Structured: Start with a summary, then detail, outline the main points, and describe them categorically.\n"
				+ "Concise sentences, clear examples: Use keywords instead of long sentences, replace subjective judgments with examples, control the word count.\n"
				+ "Fully reflect the match between the job characteristics and personal skills.\n"
				+ "Return Results:\n"
				+ "\n"
				+ "Only want JSON content, no other returns such as \"OK\" or any analysis steps of this question.\n"
				+ "If there are no issues, directly return \"{}\".\n"
				+ "If there are issues, return: {\"self\":[{\"problems\":[\"common issue 1\",\"common issue 2\"], \"egs\":[{\"teji_name\":\"a certain skill/trait\", \"eg\":[\"example\"]}]}]}\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ ""));
		System.out.println("EN: " + countTokens(messages, null));
		messages = new LinkedList<>();
		messages.add(Message.buildMessage(LLMRole.USER, "{\n"
				+ "  \"self_eval\": [\n"
				+ "    {\n"
				+ "      \"issue_name\": \"缺乏针对性\",\n"
				+ "      \"why\": [\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"缺少职位匹配\",\n"
				+ "          \"explain\": \"自我评价中没有直接对应招聘职位的要求和描述，未突出与职位相关的技能、成就和特质。\",\n"
				+ "          \"conversion\": \"1.1\"\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"缺少公司文化匹配\",\n"
				+ "          \"explain\": \"自我评价中未展示对公司文化和价值观的了解和匹配。\",\n"
				+ "          \"conversion\": \"1.2\"\n"
				+ "        }\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"issue_name\": \"缺乏具体且量化的成就\",\n"
				+ "      \"why\": [\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"缺少实例支持\",\n"
				+ "          \"explain\": \"自我评价中未提供具体例子或故事来支持描述，如解决问题的能力、领导项目的经历等。\",\n"
				+ "          \"conversion\": \"2.1\"\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"缺少量化成就\",\n"
				+ "          \"explain\": \"自我评价中未量化成就，如提升效率的百分比、管理的团队规模、影响的客户数等。\",\n"
				+ "          \"conversion\": \"2.2\"\n"
				+ "        }\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"issue_name\": \"通用陈述过多\",\n"
				+ "      \"why\": [\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"使用过于通用的形容词\",\n"
				+ "          \"explain\": \"自我评价中使用了“稳重、大方”、“认真”、“开朗自信”等过于通用的形容词，未提供支持这些陈述的具体例子。\",\n"
				+ "          \"conversion\": \"6.1\"\n"
				+ "        }\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"issue_name\": \"缺乏个性化和专业性\",\n"
				+ "      \"why\": [\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"个性化不足\",\n"
				+ "          \"explain\": \"自我评价中未适当展现个性和独特之处，使自我评价更具个性化。\",\n"
				+ "          \"conversion\": \"3.2\"\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"sub_issue\": \"缺乏专业性语言\",\n"
				+ "          \"explain\": \"自我评价虽然没有使用俚语，但过度依赖通用形容词，缺乏专业且准确的语言描述。\",\n"
				+ "          \"conversion\": \"7.1\"\n"
				+ "        }\n"
				+ "      ]\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}"));
		System.out.println("CN：" + countTokens(messages, null));
	}

}
