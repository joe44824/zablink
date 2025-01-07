package com.zabbix.zablink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HostFactsService {

    private static final String HOST_FACTS_DIRECTORY = "src/main/resources/ansible/host_facts/reachable";

    private final ObjectMapper objectMapper;

    public HostFactsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Combine all JSON files in the reachable directory into a single JSON array.
     *
     * @return List of combined JSON objects.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAndCombineHostFacts() {
        List<Map<String, Object>> combinedFacts = new ArrayList<>();

        try {
            Path dirPath = Paths.get(HOST_FACTS_DIRECTORY);

            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                throw new RuntimeException("Directory not found: " + dirPath.toString());
            }

            Files.list(dirPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            Map<String, Object> fact = objectMapper.readValue(path.toFile(), Map.class);
                            combinedFacts.add(fact);
                        } catch (IOException e) {
                            System.err.println("Error reading JSON file: " + path);
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException("Error fetching host facts: " + e.getMessage());
        }

        return combinedFacts;
    }
}
