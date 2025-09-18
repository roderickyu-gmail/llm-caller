package com.hoocta.core;

import com.google.gson.annotations.SerializedName;

public class OpenAIParams extends CommonParams {

    private String model;
    private Float temperature;
    @SerializedName("max_tokens")
    private Integer maxTokens;
    @SerializedName("top_p")
    private Double topP;
    @SerializedName("presence_penalty")
    private Double presencePenalty;
    @SerializedName("frequency_penalty")
    private Double frequencyPenalty;
    private boolean stream;
    private StreamOptions stream_options;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public StreamOptions getStream_options() {
        return stream_options;
    }

    public void setStream_options(StreamOptions stream_options) {
        this.stream_options = stream_options;
    }
}
