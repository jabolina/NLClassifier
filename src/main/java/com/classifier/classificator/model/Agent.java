package com.classifier.classificator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.nlp.nlptools.service.Normalization.normalize;

public class Agent {

    private Intent intentInMemory;
    private HashMap<String, Intent> intentDict;
    private HashMap<String, List<String>> keywordsDict;
    private HashMap<String, List<String>> composedKeywordsDict;
    private HashMap<String, Double> probability;

    public Agent(List<Intent> intentsDataset) {
        this.probability = new HashMap<>();
        this.generateKeywordsDict(intentsDataset);
        this.generateIntentDict(intentsDataset);
        this.generateComposedKeywordsDict();
    }

    public Intent getIntentInMemory() {

        return intentInMemory;
    }

    public HashMap<String, Intent> getIntentDict() {

        return intentDict;
    }

    public HashMap<String, List<String>> getKeywordsDict() {

        return keywordsDict;
    }

    public HashMap<String, List<String>> getComposedKeywordsDict() {

        return composedKeywordsDict;
    }

    public HashMap<String, Double> getProbability() {

        return probability;
    }

    public void setProbability(HashMap<String, Double> probability) {

        this.probability = probability;
    }

    public void setIntentInMemory(Intent intentInMemory) {

        this.intentInMemory = intentInMemory;
    }

    private void generateKeywordsDict(List<Intent> intentsDataset) {

    /*
	    keywordsDict[keyword] = {
	        intentsKeys: [intentKey]
	    };
     */
        this.keywordsDict = new HashMap<>();

        intentsDataset.forEach(intent -> {
            String intentKey = intent.getKey();
            intent.getKeywords().forEach(s -> {
                String normalized = normalize(s);
                this.keywordsDict.putIfAbsent(normalized, new ArrayList<>());
                if (!this.keywordsDict.get(normalized).contains(intentKey)) {
                    this.keywordsDict.get(normalized).add(intentKey);
                }
            });
        });
    }

    private void generateIntentDict(List<Intent> intentsDataset) {
        /*
	        intentKey = {
	            key: intentKey,
	            minScore: int,
	            keywords: [String],
	            entitiesDict: {
	                entityKey: {
	                    key: entityKey,
	                    keywords: [String]
	                },
	                ...
	            }
	        };
         */

        this.intentDict = new HashMap<>();

        intentsDataset.forEach(intent -> {
            Intent newIntent = new Intent();
            List<String> intentKeyList = new ArrayList<>();
            newIntent.setEntitiesDict(new HashMap<>());
            newIntent.setMinScore(intent.getMinScore());

            intent.getKeywords().forEach(keyword -> intentKeyList.add(normalize(keyword)));

            newIntent.setKey(intent.getKey());
            newIntent.setKeywords(intentKeyList);

            intent.getEntitiesDict().forEach((key, entity) -> {
                List<String> keywords = new ArrayList<>();

                entity.getKeywords().forEach(keyword -> keywords.add(normalize(keyword)));

                newIntent.getEntitiesDict().putIfAbsent(entity.getKey(), new Entity().key(entity.getKey()).keywords(keywords));
            });
            this.intentDict.putIfAbsent(intent.getKey(), newIntent);
        });
    }

    private void generateComposedKeywordsDict() {
        this.composedKeywordsDict = new HashMap<>();

        this.keywordsDict.forEach((keyword, strings) -> {
            if (keyword.contains(" ")) {
                List<String> splittedKeyword = Arrays.asList(keyword.split(" "));
                this.composedKeywordsDict.putIfAbsent(splittedKeyword.get(0), new ArrayList<>());
                if (!this.composedKeywordsDict.get(splittedKeyword.get(0)).contains(keyword)) {
                    this.composedKeywordsDict.get(splittedKeyword.get(0)).add(keyword);
                }
            }
        });
    }

}