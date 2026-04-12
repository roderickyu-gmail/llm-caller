package com.hoocta.core;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Simple global registry so applications can wire a default {@link LLMClient} without relying on
 * framework-specific dependency injection. Call {@link #setDefault(LLMClient)} during
 * application bootstrap.
 */
public final class LLMClientRegistry {

    private static volatile LLMClient defaultClient;
    private static final Object DEFAULT_CLIENT_LOCK = new Object();

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
            client = loadFromProviders();
        }
        if (client == null) {
            throw new IllegalStateException("No default LLMClient configured. Call LLMClientRegistry.setDefault(...) during startup, or register an LLMClientProvider through META-INF/services.");
        }
        return client;
    }

    public static void clear() {
        defaultClient = null;
    }

    private static LLMClient loadFromProviders() {
        synchronized (DEFAULT_CLIENT_LOCK) {
            LLMClient client = defaultClient;
            if (client != null) {
                return client;
            }
            ServiceLoader<LLMClientProvider> loader = ServiceLoader.load(LLMClientProvider.class);
            for (LLMClientProvider provider : loader) {
                client = provider.get();
                if (client != null) {
                    defaultClient = client;
                    return client;
                }
            }
            return null;
        }
    }
}
