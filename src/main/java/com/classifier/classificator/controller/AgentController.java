package com.classifier.classificator.controller;


import com.classifier.classificator.dto.ResultDto;
import com.classifier.classificator.model.Agent;
import com.classifier.classificator.model.Intent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.nlp.nlptools.service.Normalization.normalize;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private Agent agent;

    @PostMapping(value = "/predict", produces = "application/json")
    public ResultDto probabilities(@RequestBody HashMap<String, String> message) {
        return (message.containsKey("message")) ?
            agent.chat(normalize(message.get("message"))) :
                null;
    }

    @PostMapping(produces = "application/json")
    public Agent initialize(@RequestBody List<Intent> intentsDataset) {
        this.agent = new Agent(intentsDataset);
        return this.agent;
    }
}
