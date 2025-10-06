package com.unicorn.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
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

        var chatMemory = MessageWindowChatMemory.builder()
			.maxMessages(20)
			.build();

        this.chatClient = chatClient
			.defaultSystem(DEFAULT_SYSTEM_PROMPT)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
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