package com.classifier.classificator.service;

import com.classifier.classificator.dto.ResultDto;
import com.classifier.classificator.model.Agent;
import com.classifier.classificator.model.Entity;
import com.classifier.classificator.model.Intent;

import java.math.BigDecimal;
import java.util.*;

public class AgentService {

    public static ResultDto chat(Agent agent, String message) {

        Intent relatedIntent = null;
        List<Entity> relatedEntities = new ArrayList<>();

        if (agent.getIntentInMemory() != null) {
            relatedIntent = agent.getIntentInMemory();

            relatedEntities = (relatedIntent.isCorrelatedEntities()) ?
                    defineRelatedEntity(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict()) :
                    defineRelatedEntities(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict());
        }

        if (agent.getIntentInMemory() == null || relatedEntities.isEmpty()) {
            relatedIntent = defineRelatedIntent(agent, Arrays.asList(message.split(" ")));

            if (relatedIntent == null) return new ResultDto().setResponse("NOT RELATED QUESTION");

            relatedEntities = ((relatedIntent.isCorrelatedEntities())) ?
                    defineRelatedEntity(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict()) :
                    defineRelatedEntities(Arrays.asList(message.split(" ")), relatedIntent.getEntitiesDict());
        }

        if (relatedIntent != null) {
            if (!relatedIntent.equals(agent.getIntentInMemory()) && relatedEntities.size() == 0) {
                agent.setIntentInMemory(relatedIntent);
            } else {
                agent.setIntentInMemory(null);
            }
        }

        return new ResultDto()
                .probability(agent.getProbability())
                .defineResponse();
    }

    private static List<Entity> defineRelatedEntities(List<String> splittedMessage, HashMap<String, Entity> entitiesDict) {

        List<Entity> relatedEntity = new ArrayList<>();

        entitiesDict.forEach((key, entity) -> {
            for (String word: entity.getKeywords()) {
                if (splittedMessage.contains(word)) {
                    System.out.println(entity.getKey());
                    relatedEntity.add(entity);
                    break;
                }
            }
        });

        return relatedEntity;
    }

    private static List<Entity> defineRelatedEntity(List<String> splittedMessage, HashMap<String, Entity> entitiesDict) {

        List<Entity> relatedEntity = new ArrayList<>();
        Set<String> keys = entitiesDict.keySet();

        for (String key: keys) {
            Entity entity = entitiesDict.get(key);
            for (String word: entity.getKeywords()) {
                if (splittedMessage.contains(word)) {
                    relatedEntity.add(entity);
                    return relatedEntity;
                }
            }
        }

        return relatedEntity;
    }

    private static Intent defineRelatedIntent(Agent agent, List<String> splittedMessage) {
        Intent[] relatedIntent = {null};
        double[] maxScore = {0L};
        final Long[] totalScore = {0L};
        HashMap<String, Double> scores = new HashMap<>();
        HashMap<String, Double> localProbability = new HashMap<>();

        splittedMessage.forEach(word -> {
            if (agent.getKeywordsDict().containsKey(word)) {
                agent.getKeywordsDict().get(word).forEach(keyword -> {
                    scores.putIfAbsent(keyword, 0D);
                    scores.put(keyword, (scores.get(keyword) + (1L / agent.getKeywordsDict().get(word).size())));
                });
            }

            if (agent.getComposedKeywordsDict().containsKey(word)) {
                agent.getComposedKeywordsDict().get(word).forEach(derivedKeyword -> {
                    if (String.join(" ", splittedMessage).contains(derivedKeyword)) {
                        agent.getKeywordsDict().get(derivedKeyword).forEach(key -> {
                            scores.putIfAbsent(key, 0D);
                            scores.put(key, (scores.get(key) + (1L / agent.getComposedKeywordsDict().get(word).size())));
                        });
                    }
                });
            }
        });

        scores.forEach((key, score) -> {
            if ((score.compareTo(maxScore[0]) > 0) &&
                    (score.compareTo(agent.getIntentDict().get(key).getMinScore())) >= 0) {
                relatedIntent[0] = agent.getIntentDict().get(key);
                maxScore[0] = score.doubleValue();
            }
            totalScore[0] += score.longValue();
        });

        scores.forEach((key, score) -> {
            localProbability.putIfAbsent(key, score / totalScore[0]);
        });

        agent.setProbability(localProbability);

        return relatedIntent[0];
    }
}
