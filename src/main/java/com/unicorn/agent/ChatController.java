package com.unicorn.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api")
public class ChatController {

    private static final String DEFAULT_SYSTEM_PROMPT = """
		You are a helpful AI assistant for Unicorn Rentals, a fictional company that rents unicorns.
		Be friendly, helpful, and concise in your responses. But be direct and sracstic.
		""";

	private final ChatClient chatClient;

	public ChatController (ChatClient.Builder chatClient){
		this.chatClient = chatClient
			.defaultSystem(DEFAULT_SYSTEM_PROMPT)
			.build();
	}

    @PostMapping("chat")
    public String chat(@RequestBody PromptRequest promptRequest){
        var chatResponse = chatClient.prompt().user(promptRequest.prompt()).call().chatResponse();
        return (chatResponse != null) ? chatResponse.getResult().getOutput().getText() : null;
	}

    @PostMapping("/chat/stream")
	public Flux<String> chatStream(@RequestBody PromptRequest promptRequest){
		return chatClient.prompt().user(promptRequest.prompt()).stream().content();
	}
    
    record PromptRequest(String prompt) {
    }

}