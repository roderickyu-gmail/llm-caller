package com.hoocta.core;

/**
 * OpenAI 的 finish reason 定义
 * @author roderickyu Jul 16, 2024
 */
public interface FinishReason {

	/**
	 * "stop"：表示生成完成，是因为模型生成到了一个自然的停止点，通常是因为生成到了结束符（例如句号），或者满足了内在的停止条件。
	 * "stop"：通常情况下可以认为生成结果是完整且自然结束的，可以直接使用。
	 */
	public static final String STOP = "";

	/**
	 * "length"：表示生成完成，是因为达到了最大长度限制。API 调用时可以通过参数指定最大生成长度，当生成的内容达到这个长度时，生成过程会停止，并返回
	 * finish_reason: "length"。
	 * 
	 * "length"：如果需要生成更多内容，可以考虑再次调用 API 继续生成。
	 */
	public static final String LENGTH = "length";

	/**
	 * "content_filter"：表示生成的内容被内容过滤器截断。这种情况发生在生成的内容被检测到可能违反内容政策时，模型会截断生成过程。
	 * "content_filter"：需要检查生成的内容，确保没有违规或不适当的内容。
	 */
	public static final String CONTENT_FILTER = "content_filter";

	/**
	 * "null"（或 None）：表示未能明确识别出停止原因。这可能是因为生成过程中发生了错误或其他不确定的情况。
	 * "null"（或 None）：需要进一步检查生成过程中的其他参数和响应，以确定是否有问题需要处理。
	 */
	public static final String NULL = "null";
	public static final String NONE = "None";

}
