package com.classifier.classificator.dto;

import com.classifier.classificator.model.Intent;

import java.util.HashMap;
import java.util.List;

public class AgentDto {

    private Intent intentInMemory;
    private HashMap<String, Intent> intentDict;
    private HashMap<String, List<String>> keywordsDict;
    private HashMap<String, List<String>> composedKeywordsDict;

    public AgentDto intentInMemory(Intent intentInMemory) {
        this.intentInMemory = intentInMemory;
        return this;
    }

    public AgentDto intentDict(HashMap<String, Intent> intentDict) {
        this.intentDict = intentDict;
        return this;
    }

    public AgentDto keywordsDict(HashMap<String, List<String>> keywordsDict) {
        this.keywordsDict = keywordsDict;
        return this;
    }

    public AgentDto composedKeywordsDict(HashMap<String, List<String>> composedKeywordsDict) {
        this.composedKeywordsDict = composedKeywordsDict;
        return this;
    }
}
