package com.classifier.classificator.controller;


import com.classifier.classificator.model.Agent;
import com.classifier.classificator.model.Intent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.classifier.classificator.service.AgentService.chat;
import static com.nlp.nlptools.service.Normalization.normalize;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private Agent agent;

    @PostMapping(value = "/predict", produces = "application/json")
    public ResponseEntity probabilities(@RequestBody HashMap<String, String> message) {

        return (message.containsKey("message")) ?
                ResponseEntity.status(HttpStatus.OK).body(chat(this.agent, normalize(message.get("message")))) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<Agent> initialize(@RequestBody List<Intent> intentsDataset) {
        this.agent = new Agent(intentsDataset);
        return ResponseEntity.status(HttpStatus.OK).body(agent);
    }
}
