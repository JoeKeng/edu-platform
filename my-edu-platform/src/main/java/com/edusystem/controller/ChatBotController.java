package com.edusystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ai")
public class ChatBotController {

    //TODO: 待完善
    private final ChatClient chatClient;

    public ChatBotController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("你是一个翻译官").build();
    }

    @GetMapping(value =  "/chat/{message}")
    public String chat(@PathVariable("message") String message) {
        log.info("message:{}", message);
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}
