package com.example.rag_qa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {
    
    private final OllamaEmbeddingModel embeddingModel;
    private final OllamaChatModel chatModel;


    public OllamaService(OllamaEmbeddingModel embeddingModel, OllamaChatModel chatModel) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
    }

    public List<Float> createEmbeddings(String text) {
        List<String> textsToEmbed = List.of(text);

        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("embeddinggemma:latest")
                .truncate(false)
                .build();

        EmbeddingRequest embeddingRequest = new EmbeddingRequest(textsToEmbed, ollamaOptions);
        EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);

        // Convert float[] to List<Float>
        float[] rawEmbedding = embeddingResponse.getResult().getOutput();
        List<Float> embedding = new ArrayList<>(rawEmbedding.length);
        for (float f : rawEmbedding) {
            embedding.add(f);
        }

        return embedding;
    }

    public String generateAnswer(StringBuilder context, String query) {
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("gemma3:4b")
                .build();

        // Build the prompt
        StringBuilder promptText = new StringBuilder();
        promptText.append("You are a helpful assistant.\n");
        promptText.append("Answer the following question clearly, while reading the context:\n\n");
        promptText.append("CONTEXT:\n").append(context).append("\n\n");
        promptText.append("Question: ").append(query);


        Prompt prompt = new Prompt(promptText.toString(), ollamaOptions);

        // Send promptText as a String
        String response = this.chatModel.call(prompt).getResult().getOutput().getText();

        return response;
    }
}

