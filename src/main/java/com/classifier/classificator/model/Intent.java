package com.classifier.classificator.model;

import java.util.HashMap;
import java.util.List;

public class Intent {

    private String key;
    private Long minScore;
    private List<String> keywords;
    private List<String> answers;
    private boolean correlatedEntities;
    private HashMap<String, Entity> entitiesDict;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getMinScore() {
        return minScore;
    }

    public void setMinScore(Long minScore) {
        this.minScore = minScore;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public boolean isCorrelatedEntities() {
        return correlatedEntities;
    }

    public void setCorrelatedEntities(boolean correlatedEntities) {
        this.correlatedEntities = correlatedEntities;
    }

    public HashMap<String, Entity> getEntitiesDict() {
        return entitiesDict;
    }

    public void setEntitiesDict(HashMap<String, Entity> entitiesDict) {
        this.entitiesDict = entitiesDict;
    }

    @Override
    public String toString() {
        return "Intent{" +
                "\n\tkey='" + key + '\'' +
                "\n\tminScore=" + minScore +
                "\n\tkeywords=" + keywords +
                "\n\tanswers=" + answers +
                "\n\tcorrelatedEntities=" + correlatedEntities +
                "\n\tentitiesDict=" + entitiesDict.toString() +
                "\n}";
    }
}
