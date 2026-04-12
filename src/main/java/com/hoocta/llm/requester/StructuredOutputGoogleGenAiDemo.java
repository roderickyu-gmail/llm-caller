package com.hoocta.llm.requester;

import java.util.List;

import com.google.genai.Client;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;

public final class StructuredOutputGoogleGenAiDemo {

	private StructuredOutputGoogleGenAiDemo() {
	}

	public static void main(String[] args) {
		ChatClient chatClient = buildChatClient();
		JobRecommendations recommendations = chatClient.prompt()
			.advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
			.system("You are a concise, practical career advisor.")
			.user("""
				Candidate profile:
				- 4 years Java + Spring Boot
				- Experience with PostgreSQL, Elasticsearch, and observability
				- Interested in backend or platform roles
				Return 3 job recommendations with match reasons and skill highlights.
				""")
			.call()
			.entity(JobRecommendations.class);

		System.out.println(recommendations);
	}

	private static ChatClient buildChatClient() {
		String apiKey = requiredEnv("GOOGLE_API_KEY");
		String model = envOrDefault("GOOGLE_GENAI_MODEL", null);

		Client genAiClient = Client.builder()
			.apiKey(apiKey)
			.build();

		GoogleGenAiChatOptions.Builder optionsBuilder = GoogleGenAiChatOptions.builder()
			.temperature(0.2);
		if (model == null) {
			optionsBuilder.model(GoogleGenAiChatModel.ChatModel.GEMINI_2_0_FLASH);
		} else {
			optionsBuilder.model(model);
		}

		GoogleGenAiChatModel chatModel = GoogleGenAiChatModel.builder()
			.genAiClient(genAiClient)
			.defaultOptions(optionsBuilder.build())
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
