package org.springframework.samples.petclinic.genai;

import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.JsonReader;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JacksonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

@Component
public class VectorStoreController {

    private final Logger logger = LoggerFactory.getLogger(VectorStoreController.class);
    private final VectorStore vectorStore;
    private final WebClient webClient;

    public VectorStoreController(VectorStore vectorStore, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @EventListener
    public void loadVetDataToVectorStoreOnStartup(ApplicationStartedEvent event) throws IOException {
        Resource resource = new ClassPathResource("vectorstore.json");

        if (resource.exists()) {
            File file = resource.getFile();
            ((SimpleVectorStore) this.vectorStore).load(file);
            logger.info("vector store loaded from existing vectorstore.json file");
            return;
        }

        String vetsHostname = "http://vets-service/";
        List<Vet> vets = webClient
                    .get()
                    .uri(vetsHostname + "vets")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Vet>>() {})
                    .block();

        Resource vetsAsJson = convertListToJsonResource(vets);
        DocumentReader reader = new JsonReader(vetsAsJson);

        List<Document> documents = reader.get();
        this.vectorStore.add(documents);

        if (vectorStore instanceof SimpleVectorStore store) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            File file = Files.createTempFile("vectorstore", ".json", attr).toFile();
            store.save(file);
            logger.info("vector store contents written to {}", file.getAbsolutePath());
        }
    }

    public Resource convertListToJsonResource(List<Vet> vets) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(vets);
            byte[] jsonBytes = json.getBytes();
            return new ByteArrayResource(jsonBytes);
        }
        catch (JacksonException e) {
            logger.error("Error processing JSON", e);
            return null;
        }
    }
}
