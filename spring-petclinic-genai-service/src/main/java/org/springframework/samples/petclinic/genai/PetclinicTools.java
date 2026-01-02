package org.springframework.samples.petclinic.genai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Vet;

import java.util.List;
import java.util.function.Function;

@Configuration
public class PetclinicTools {

    private static final Logger LOG = LoggerFactory.getLogger(PetclinicTools.class);
    private final AIDataProvider petclinicAiProvider;

    public PetclinicTools(AIDataProvider petclinicAiProvider) {
        this.petclinicAiProvider = petclinicAiProvider;
    }

    // record for parameter-less or specific input calls
    public record EmptyRequest() {}
    public record AddPetRequest(int ownerId, PetRequest petRequest) {}

    @Bean
    @Description("List the owners that the pet clinic has")
    public Function<EmptyRequest, List<OwnerDetails>> listOwners() {
        return request -> {
            LOG.info("Calling listOwners");
            return petclinicAiProvider.getAllOwners();
        };
    }

    @Bean
    @Description("Add a new pet owner to the pet clinic")
    public Function<OwnerRequest, OwnerDetails> addOwner() {
        return request -> {
            LOG.info("Calling addOwner: {}", request);
            return petclinicAiProvider.addOwnerToPetclinic(request);
        };
    }

    @Bean
    @Description("List the veterinarians that the pet clinic has")
    public Function<Vet, List<String>> listVets() {
        return request -> {
            LOG.info("Calling listVets: {}", request);
            try {
                return petclinicAiProvider.getVets(request);
            } catch (Exception e) {
                return List.of();
            }
        };
    }

    @Bean
    @Description("Add a pet to an owner identified by ownerId")
    public Function<AddPetRequest, PetDetails> addPetToOwner() {
        return request -> {
            LOG.info("Calling addPetToOwner: {}", request);
            return petclinicAiProvider.addPetToOwner(request.ownerId(), request.petRequest());
        };
    }
}

record OwnerRequest(String firstName, String lastName, String address, String city, String telephone) {}
