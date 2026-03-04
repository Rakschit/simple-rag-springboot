package com.example.rag_qa.service;

import java.util.ArrayList;
import java.util.List;

import io.pinecone.clients.Index;
import org.springframework.stereotype.Service;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;
import io.pinecone.proto.UpsertResponse;

@Service
public class PineconeService {
    
    private final Index index;
    
    public PineconeService(Index index){
        this.index = index;
    }

    public String upsert(List<String> chunks, List<List<Float>> embeddings, String filename){
        System.out.println("Preparing to upsert");        
        if(chunks.size() != embeddings.size()){
            throw new IllegalArgumentException("Chunks and embeddings size must be same");
        }

        List<VectorWithUnsignedIndices> vectors = new ArrayList<>();
        String namespace = filename;

        for (int i = 0; i < chunks.size(); i++) {
            String id = filename + "_chunk_" + i;

            Struct metadata = Struct.newBuilder()
            .putFields("text", Value.newBuilder().setStringValue(chunks.get(i)).build())
            .build();

            VectorWithUnsignedIndices vector = new VectorWithUnsignedIndices(
                id, embeddings.get(i), metadata, null);

            vectors.add(vector);
        }

        UpsertResponse response = index.upsert(vectors, namespace);
        System.out.println("Upsert completed"+ response);
        return namespace;
    }

    public List<Struct> doSimilaritySearch(List<Float> queryEmbedding, String namespace, int topK){
        
        List<Struct> metadataList = new ArrayList<>();

        try {
            QueryResponseWithUnsignedIndices response = index.query(
                topK,
                queryEmbedding,
                null,
                null,
                null,
                namespace,
                null,
                false,
                true
            );

            System.out.println(response);
            if (response != null && response.getMatchesList() != null) {
                for (var match : response.getMatchesList()) {
                    Struct metadata = match.getMetadata();
                    if (metadata != null) {
                        metadataList.add(metadata);
                    }
                }
            }

        } catch (Exception e) {
            // Log exception but do not crash
            System.err.println("Similarity search failed: " + e.getMessage());
        }
        return metadataList;
    }
}
