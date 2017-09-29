package com.classifier.classificator.model;

import com.classifier.classificator.dto.ResultDto;
import com.nlp.nlptools.service.Normalization;

import java.util.*;

import static com.nlp.nlptools.service.Normalization.*;
import static com.nlp.nlptools.service.Normalization.normalize;

public class Agent {

    private Intent intentInMemory;
    private HashMap<String, Intent> intentDict;
    private HashMap<String, List<String>> keywordsDict;
    private HashMap<String, List<String>> composedKeywordsDict;
    private HashMap<String, Long> probability;

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

    private List<Entity> defineRelatedEntities(List<String> splittedMessage, HashMap<String, Entity> entitiesDict) {

        List<Entity> relatedEntities = new ArrayList<>();

        entitiesDict.forEach((s, entity) ->
                entity.getKeywords().forEach(s1 -> {
                    for (String word: s1.split(" ")) {
                        if (splittedMessage.contains(word)) {
                            relatedEntities.add(entity);
                            break;
                        }
                    }
                })
        );

        return relatedEntities;
    }

    private List<Entity> defineRelatedEntity(List<String> splittedMessage, HashMap<String, Entity> entitiesDict) {

        List<Entity> relatedEntity = new ArrayList<>();

        entitiesDict.forEach((s, entity) ->
                entity.getKeywords().forEach(s1 -> {
                    if (relatedEntity.isEmpty()) {
                        for (String word : s1.split(" ")) {
                            if (splittedMessage.contains(word)) {
                                relatedEntity.add(entity);
                            }
                            break;
                        }
                    }
                })
        );

        return relatedEntity;
    }

    private Intent defineRelatedIntent(List<String> splittedMessage) {
        Intent[] relatedIntent = {null};
        Long[] maxScore = {0L};
        HashMap<String, Long> scores = new HashMap<>();

        splittedMessage.forEach(word -> {
            if (this.keywordsDict.containsKey(word)) {
                this.keywordsDict.get(word).forEach(keyword -> {
                    scores.putIfAbsent(keyword, 0L);
                    scores.put(keyword, (scores.get(keyword) + (1L / this.keywordsDict.get(word).size())));
                });
            }

            if (this.composedKeywordsDict.containsKey(word)) {
                this.composedKeywordsDict.get(word).forEach(derivedKeyword -> {
                    if (String.join(" ", splittedMessage).contains(derivedKeyword)) {
                        keywordsDict.get(derivedKeyword).forEach(key -> {
                            scores.putIfAbsent(key, 0L);
                            scores.put(key, (scores.get(key) + (1L / this.keywordsDict.get(derivedKeyword).size())));
                        });
                    }
                });
            }
        });

        scores.forEach((key, score) -> {
            if ((score > maxScore[0]) && (score >= intentDict.get(key).getMinScore())) {
                this.probability.putIfAbsent(intentDict.get(key).getKey(), score);
                relatedIntent[0] = intentDict.get(key);
                maxScore[0] = score;
            }
        });

        return relatedIntent[0];
    }

    public ResultDto chat(String message) {

        Intent relatedIntent = null;
        List<Entity> relatedEntities = new ArrayList<>();

        if (this.intentInMemory != null) {
            relatedIntent = this.intentInMemory;

            relatedEntities = (relatedIntent.isCorrelatedEntities()) ?
                    defineRelatedEntity(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict()) :
                    defineRelatedEntities(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict());
        }

        if (this.intentInMemory == null || relatedEntities.isEmpty()) {
            relatedIntent = defineRelatedIntent(Arrays.asList(message.split(" ")));

            relatedEntities = ((relatedIntent != null) && (relatedIntent.isCorrelatedEntities())) ?
                    defineRelatedEntity(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict()) :
                    defineRelatedEntities(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict());

        }


        if (!relatedIntent.equals(this.intentInMemory) && relatedEntities.size() == 0)
        { this.intentInMemory = relatedIntent; } else { this.intentInMemory = null; }

        return new ResultDto()
                .probability(this.probability);
    }

    @Override
    public String toString() {
        return "Agent{" +
                "\n\tintentInMemory=" + intentInMemory +
                "\n\tintentDict=" + intentDict.toString() +
                "\n\tkeywordsDict=" + keywordsDict +
                "\n\tcomposedKeywordsDict=" + composedKeywordsDict +
                "\n\tprobability=" + probability +
                "\n}";
    }
}
