package com.hoocta.core.config;

import java.util.Objects;

/**
 * Encapsulates tunable parameters for an LLM invocation. Instances are immutable so they can be
 * reused safely across threads. Use {@link Builder} to create or adjust values.
 */
public final class LLMRequestOptions {

    private final Float temperature;
    private final Integer maxTokens;
    private final Double topP;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Boolean stream;

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
            .temperature(temperature)
            .maxTokens(maxTokens)
            .topP(topP)
            .presencePenalty(presencePenalty)
            .frequencyPenalty(frequencyPenalty)
            .stream(stream);
    }

    public LLMRequestOptions merge(LLMRequestOptions overrides) {
        if (overrides == null) {
            return this;
        }
        if (overrides == this) {
            return this;
        }
        return builder()
            .temperature(firstNonNull(overrides.temperature, temperature))
            .maxTokens(firstNonNull(overrides.maxTokens, maxTokens))
            .topP(firstNonNull(overrides.topP, topP))
            .presencePenalty(firstNonNull(overrides.presencePenalty, presencePenalty))
            .frequencyPenalty(firstNonNull(overrides.frequencyPenalty, frequencyPenalty))
            .stream(firstNonNull(overrides.stream, stream))
            .build();
    }

    public Float getTemperature() {
        return temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Double getTopP() {
        return topP;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Boolean getStream() {
        return stream;
    }

    private static <T> T firstNonNull(T first, T second) {
        return first != null ? first : second;
    }

    private LLMRequestOptions(Builder builder) {
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.topP = builder.topP;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.stream = builder.stream;
    }

    public static final class Builder {
        private Float temperature;
        private Integer maxTokens;
        private Double topP;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Boolean stream;

        private Builder() {
        }

        public Builder temperature(Float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public LLMRequestOptions build() {
            if (temperature != null) {
                Objects.requireNonNull(temperature, "temperature");
            }
            return new LLMRequestOptions(this);
        }
    }
}
