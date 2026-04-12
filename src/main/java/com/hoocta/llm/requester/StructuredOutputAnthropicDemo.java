package com.hoocta.llm.requester;

import java.util.List;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;

public final class StructuredOutputAnthropicDemo {

	private StructuredOutputAnthropicDemo() {
	}

	public static void main(String[] args) {
		ChatClient chatClient = buildChatClient();
		JobRecommendations recommendations = chatClient.prompt()
			.advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
			.system("You are a concise, practical career advisor.")
			.user("""
				Candidate profile:
				- 7 years Java + Spring Boot
				- Experience with Kafka, Redis, and distributed systems
				- Interested in backend or platform roles
				Return 3 job recommendations with match reasons and skill highlights.
				""")
			.call()
			.entity(JobRecommendations.class);

		System.out.println(recommendations);
	}

	private static ChatClient buildChatClient() {
		String apiKey = requiredEnv("ANTHROPIC_API_KEY");
		String defaultModel = AnthropicApi.ChatModel.CLAUDE_SONNET_4_5.getValue();
		String model = envOrDefault("ANTHROPIC_MODEL", defaultModel);
		String baseUrl = envOrDefault("ANTHROPIC_BASE_URL", null);

		AnthropicApi.Builder apiBuilder = AnthropicApi.builder().apiKey(apiKey);
		if (baseUrl != null) {
			apiBuilder.baseUrl(baseUrl);
		}
		AnthropicApi anthropicApi = apiBuilder.build();

		AnthropicChatOptions defaultOptions = AnthropicChatOptions.builder()
			.model(model)
			.temperature(0.2)
			.maxTokens(512)
			.build();

		AnthropicChatModel chatModel = AnthropicChatModel.builder()
			.anthropicApi(anthropicApi)
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
