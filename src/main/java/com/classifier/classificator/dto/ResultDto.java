package com.classifier.classificator.dto;

import java.util.HashMap;

public class ResultDto {

    private HashMap<String, Long> probability;

    public HashMap<String, Long> getProbability() {

        return probability;
    }

    public ResultDto probability(HashMap<String, Long> probability) {
        this.probability = probability;
        return this;
    }
}
