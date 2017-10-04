package com.classifier.classificator.dto;

import java.util.HashMap;

public class ResultDto {

    private String response;
    private HashMap<String, Double> probability;


    public ResultDto setResponse(String response) {

        this.response = response;
        return this;
    }

    public ResultDto defineResponse() {
        Double maxScore = 0D;

        probability.forEach((key, score) -> {
            if (score.compareTo(maxScore) > 0) this.response = key;
        });
        return this;
    }

    public ResultDto probability(HashMap<String, Double> probability) {
        this.probability = probability;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public HashMap<String, Double> getProbability() {
        return probability;
    }
}
