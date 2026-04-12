package com.hoocta.llm.requester;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

public final class StructuredOutputSpringAiDemo {

	private StructuredOutputSpringAiDemo() {
	}

	public static void main(String[] args) {
		ChatClient chatClient = buildChatClient();
		JobRecommendations recommendations = chatClient.prompt()
			.advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
			.system("You are a concise, practical career advisor.")
			.user("""
				Candidate profile:
				- 5 years Java + Spring Boot
				- Experience with Kafka and distributed systems
				- Interested in backend or platform roles
				Return 3 job recommendations with match reasons and skill highlights.
				""")
			.call()
			.entity(JobRecommendations.class);

		System.out.println(recommendations);
	}

	private static ChatClient buildChatClient() {
		String apiKey = requiredEnv("OPENAI_API_KEY");
		String model = envOrDefault("OPENAI_MODEL", "gpt-4o-mini");
		String baseUrl = envOrDefault("OPENAI_BASE_URL", null);

		OpenAiApi.Builder apiBuilder = OpenAiApi.builder().apiKey(apiKey);
		if (baseUrl != null) {
			apiBuilder.baseUrl(baseUrl);
		}
		OpenAiApi openAiApi = apiBuilder.build();

		OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
			.model(model)
			.temperature(0.2)
			.build();

		OpenAiChatModel chatModel = OpenAiChatModel.builder()
			.openAiApi(openAiApi)
			.defaultOptions(defaultOptions)
			.build();

		return ChatClient.builder(chatModel).build();
	}

	private static String requiredEnv(String name) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException("Missing required env var: " + name);
		}
		return value;
	}

	private static String envOrDefault(String name, String fallback) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			return fallback;
		}
		return value;
	}

	public record JobRecommendation(String title, String matchReason, String skillHighlight) {
	}

	public record JobRecommendations(List<JobRecommendation> items) {
	}
}
