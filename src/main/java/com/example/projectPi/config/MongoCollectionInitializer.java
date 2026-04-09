package com.example.projectPi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoCollectionInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoCollectionInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        createCollectionIfNotExists("users");
        createCollectionIfNotExists("coaches");
        createCollectionIfNotExists("players");
        createCollectionIfNotExists("referees");
        createCollectionIfNotExists("sponsors");
        createCollectionIfNotExists("sponsor_managers");
        createCollectionIfNotExists("sports");
        
        System.out.println("MongoDB collections initialized successfully");
    }

    private void createCollectionIfNotExists(String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            System.out.println("Collection created: " + collectionName);
        } else {
            System.out.println("Collection already exists: " + collectionName);
        }
    }
}
