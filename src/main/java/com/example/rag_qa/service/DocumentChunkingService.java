package com.example.rag_qa.service;

import ai.djl.sentencepiece.SpTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentChunkingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentChunkingService.class);
    private static final Path MODE_PATH = Paths.get("src/main/resources/tokenizer.model");

    private static final int CHUNK_SIZE = 256;
    private static final int OVERLAP_SIZE = 50;

    private final SpTokenizer tokenizer;

    public DocumentChunkingService(){
        try{
            this.tokenizer = new SpTokenizer(MODE_PATH);
        } catch(IOException ex){
            throw new RuntimeException("Could not initialize tokenizer");
        }
    }

    public String extractText(Resource uploadFile){
        TikaDocumentReader documentReader = new TikaDocumentReader(uploadFile);
        List<Document> document = documentReader.get();
        String combinedText = document.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---//---\n\n"));
        
        combinedText = cleanText(combinedText);
        return combinedText;
    }

    public String cleanText(String text){
        if (text == null) return "";

        // 1. Normalize line breaks, tabs, and multiple spaces
        text = text.replaceAll("[\\n\\t]", " ");
        text = text.replaceAll("\\s+", " ");
        text = text.replaceAll("\\bimage\\d+\\.png\\b", "");

        // 2. Remove unwanted separators
        text = text.replaceAll("---//---", " ");

        // 3. Remove non-printable or weird unicode characters
        text = text.replaceAll("[^\\p{Print}]", "");

        // 4. Normalize common punctuation issues
        text = text.replaceAll("[-–—]+", "-"); // unify dashes
        text = text.replaceAll("[.]{2,}", "."); // multiple dots to single
        text = text.replaceAll("[!]{2,}", "!"); // multiple exclamations
        text = text.replaceAll("[?]{2,}", "?"); // multiple question marks

        // 5. Remove repeated short patterns (like page numbers)
        text = text.replaceAll("\\b(page|pg|p)\\s*\\d+\\b", "");

        // 6. Optional: remove standalone numbers (if not meaningful)
        // text = text.replaceAll("\\b\\d+\\b", "");

        // 7. Trim and lowercase
        text = text.trim().toLowerCase();

        return text;
    }

    public int countTokens(String text){
        if(text == null || text.isEmpty()){
            return 0;
        }
        return this.tokenizer.tokenize(text).size();
    }

    private List<String> chunksTokens(int[] tokenIds, int chunkSize, int overlapSize){
        if(overlapSize >= chunkSize){
            throw new IllegalArgumentException("Overlap size must be smaller than chunk size");
        }
        List<String> chunks = new ArrayList<>();
        int totalTokens = tokenIds.length;

        if(totalTokens <= chunkSize){
            String decoded = this.tokenizer.getProcessor().decode(tokenIds);
            chunks.add(decoded);
            return chunks;
        }

        int stride = chunkSize - overlapSize;
        for(int i = 0; i < totalTokens; i+=stride){
            int end = Math.min(i + chunkSize, totalTokens);
            
            int[] chunk = Arrays.copyOfRange(tokenIds, i, end);

            String decoded = this.tokenizer.getProcessor().decode(chunk);
            chunks.add(decoded);

            if(end == totalTokens){
                break;
            }
        }
        return chunks;
    }

    public List<String> getChunks(Resource uploadFile){
        String text = extractText(uploadFile);
        if(text != null) log.info("Text Extracted Succesfully");
        int tokenCount = countTokens(text);
        log.info("Token Count: "+ tokenCount);

        int []allTokenIds = this.tokenizer.getProcessor().encode(text);
        List<String> chunks =  chunksTokens(allTokenIds, CHUNK_SIZE, OVERLAP_SIZE);
        log.info("Total Chunks: "+chunks.size());
        return chunks;
    }

}

