package com.hoocta.core.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a concrete LLM endpoint (model + URL + credentials). A client assembles one or more
 * endpoints to provide retry/failover behaviour.
 */
public final class LLMEndpoint {

    private final String name;
    private final String baseUrl;
    private final String model;
    private final Supplier<String> apiKeySupplier;
    private final Map<String, String> headers;
    private final LLMRequestOptions defaultOptions;
    private final boolean longTimeout;

    private LLMEndpoint(Builder builder) {
        this.name = builder.name;
        this.baseUrl = builder.baseUrl;
        this.model = builder.model;
        this.apiKeySupplier = builder.apiKeySupplier;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        this.defaultOptions = builder.defaultOptions;
        this.longTimeout = builder.longTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }

    public Optional<String> apiKeyOptional() {
        String key = apiKeySupplier.get();
        return Optional.ofNullable(key).filter(k -> !k.isBlank());
    }

    public String getApiKey() {
        String key = apiKeySupplier.get();
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("API key supplier for endpoint '" + safeName() + "' returned blank value");
        }
        return key;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Optional<LLMRequestOptions> getDefaultOptions() {
        return Optional.ofNullable(defaultOptions);
    }

    public boolean isLongTimeout() {
        return longTimeout;
    }

    private String safeName() {
        return name == null ? model : name;
    }

    @Override
    public String toString() {
        return "LLMEndpoint{" +
            "name='" + safeName() + '\'' +
            ", baseUrl='" + baseUrl + '\'' +
            ", model='" + model + '\'' +
            ", headers=" + headers.keySet() +
            ", longTimeout=" + longTimeout +
            '}';
    }

    public static final class Builder {
        private String name;
        private String baseUrl;
        private String model;
        private Supplier<String> apiKeySupplier = () -> null;
        private Map<String, String> headers = new LinkedHashMap<>();
        private LLMRequestOptions defaultOptions;
        private boolean longTimeout = true;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKeySupplier = () -> apiKey;
            return this;
        }

        public Builder apiKeySupplier(Supplier<String> apiKeySupplier) {
            this.apiKeySupplier = Objects.requireNonNull(apiKeySupplier, "apiKeySupplier");
            return this;
        }

        public Builder header(String name, String value) {
            Objects.requireNonNull(name, "header name");
            Objects.requireNonNull(value, "header value");
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            if (headers != null) {
                headers.forEach(this::header);
            }
            return this;
        }

        public Builder defaultOptions(LLMRequestOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public Builder longTimeout(boolean longTimeout) {
            this.longTimeout = longTimeout;
            return this;
        }

        public LLMEndpoint build() {
            if (baseUrl == null || baseUrl.isBlank()) {
                throw new IllegalArgumentException("baseUrl must not be blank");
            }
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException("model must not be blank");
            }
            Objects.requireNonNull(apiKeySupplier, "apiKeySupplier");
            return new LLMEndpoint(this);
        }
    }
}
