package com.hoocta.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Captures the ordered set of endpoints the client should try. Separate tiers allow consumers to
 * define different quality/cost levels (for example, standard vs. high quality).
 */
public final class LLMClientConfiguration {

    public enum Tier {
        STANDARD,
        SMART
    }

    private final Map<Tier, List<LLMEndpoint>> tieredEndpoints;
    private final LLMRequestOptions defaultOptions;

    private LLMClientConfiguration(Builder builder) {
        Map<Tier, List<LLMEndpoint>> copy = new EnumMap<>(Tier.class);
        for (Map.Entry<Tier, List<LLMEndpoint>> entry : builder.tieredEndpoints.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        this.tieredEndpoints = Collections.unmodifiableMap(copy);
        this.defaultOptions = builder.defaultOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<LLMEndpoint> endpoints(Tier tier) {
        return tieredEndpoints.getOrDefault(tier, Collections.emptyList());
    }

    public boolean hasTier(Tier tier) {
        return !endpoints(tier).isEmpty();
    }

    public LLMRequestOptions defaultOptions() {
        return defaultOptions;
    }

    @Override
    public String toString() {
        return "LLMClientConfiguration{" +
            "tiers=" + tieredEndpoints.keySet() +
            '}';
    }

    public static final class Builder {
        private final Map<Tier, List<LLMEndpoint>> tieredEndpoints = new EnumMap<>(Tier.class);
        private LLMRequestOptions defaultOptions;

        private Builder() {
        }

        public Builder addEndpoint(Tier tier, LLMEndpoint endpoint) {
            Objects.requireNonNull(tier, "tier");
            Objects.requireNonNull(endpoint, "endpoint");
            tieredEndpoints.computeIfAbsent(tier, key -> new ArrayList<>()).add(endpoint);
            return this;
        }

        public Builder standardEndpoint(LLMEndpoint endpoint) {
            return addEndpoint(Tier.STANDARD, endpoint);
        }

        public Builder smartEndpoint(LLMEndpoint endpoint) {
            return addEndpoint(Tier.SMART, endpoint);
        }

        public Builder defaultOptions(LLMRequestOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public LLMClientConfiguration build() {
            if (tieredEndpoints.values().stream().allMatch(List::isEmpty)) {
                throw new IllegalStateException("At least one endpoint must be registered");
            }
            return new LLMClientConfiguration(this);
        }
    }
}
