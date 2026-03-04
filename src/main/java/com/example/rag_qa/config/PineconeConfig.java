package com.example.rag_qa.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PineconeConfig {

    @Bean
    public Index pineconeIndex() {
        // Load environment variables from .env
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("PINECONE_KEY");
        String indexName = "gemmaembed";

        if (apiKey == null || indexName == null) {
            throw new IllegalStateException("Missing Pinecone config in .env");
        }

        Pinecone client = new Pinecone.Builder(apiKey)
                .build();

        return client.getIndexConnection(indexName);
    }
}
