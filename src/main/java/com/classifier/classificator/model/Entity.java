package com.classifier.classificator.model;

import java.util.List;

public class Entity {

    private String key;
    private List<String> keywords;
    private List<String> answers;


    public String getKey() {
        return key;
    }

    public Entity key(String key) {
        this.key = key;
        return this;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public Entity keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public Entity answers(List<String> answers) {
        this.answers = answers;
        return this;
    }


    @Override
    public String toString() {
        return "Entity{" +
                "\n\tkey='" + key + '\'' +
                "\n\tkeywords=" + keywords +
                "\n}";
    }
}
