package org.springframework.samples.petclinic.genai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class PetclinicChatClient {

    private static final Logger LOG = LoggerFactory.getLogger(PetclinicChatClient.class);
    private final ChatClient chatClient;

    public PetclinicChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultSystem("You are a friendly AI assistant for the Spring Petclinic.")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new SimpleLoggerAdvisor()
                )
                // Reference the @Bean method names from PetclinicTools
                .defaultFunctions("listOwners", "addOwner", "listVets", "addPetToOwner")
                .build();
    }

    @PostMapping("/chatclient")
    public String exchange(@RequestBody String query) {
        try {
            return this.chatClient
                    .prompt()
                    .user(query)
                    .call()
                    .content();
        } catch (Exception exception) {
            LOG.error("Error processing chat message", exception);
            return "I'm sorry, I encountered an error. Please try again.";
        }
    }
}
