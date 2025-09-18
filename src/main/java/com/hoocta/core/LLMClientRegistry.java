package com.hoocta.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Simple global registry so applications can wire a default {@link LLMClient} without relying on
 * framework-specific dependency injection. Call {@link #setDefault(LLMClient)} during
 * application bootstrap.
 */
public final class LLMClientRegistry {

    private static volatile LLMClient defaultClient;

    private LLMClientRegistry() {
    }

    public static void setDefault(LLMClient client) {
        defaultClient = Objects.requireNonNull(client, "client");
    }

    public static Optional<LLMClient> get() {
        return Optional.ofNullable(defaultClient);
    }

    public static LLMClient getRequired() {
        LLMClient client = defaultClient;
        if (client == null) {
            throw new IllegalStateException("No default LLMClient configured. Call LLMClientRegistry.setDefault(...) during startup.");
        }
        return client;
    }

    public static void clear() {
        defaultClient = null;
    }
}
