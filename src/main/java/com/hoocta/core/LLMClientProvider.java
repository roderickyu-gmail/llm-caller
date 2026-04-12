package com.hoocta.core;

/**
 * Supplies a default {@link LLMClient} for applications that want requester singletons to
 * configure themselves through Java's {@link java.util.ServiceLoader} mechanism.
 */
@FunctionalInterface
public interface LLMClientProvider {
	LLMClient get();
}
