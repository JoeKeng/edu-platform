package com.edusystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ai")
@Tag(name = "AI")
public class ChatBotController {

    //测试
    private final ChatClient chatClient;

    public ChatBotController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("你是一个程序员").build();
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
