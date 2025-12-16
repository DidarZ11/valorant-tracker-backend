package com.valorant.tracker.service.api;

import com.fasterxml.jackson.databind.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class ValorantApiService {

    private final RestTemplate restTemplate;

    @Value("${valorant.api.baseurl}")
    private String apiBaseUrl;

    @Value("${valorant.api.key}")
    private String apiKey;

    public JsonNode fetchAccount(String name, String tag, String region) {
        try {
            String url = String.format("%s/account/%s/%s?api_key=%s",
                    apiBaseUrl, name.trim(), tag.trim(), apiKey);

            System.out.println("📡 Fetching account: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            System.out.println("📥 Account response status: " + root.get("status").asInt());

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("❌ API Error: " + root);
            throw new RuntimeException("Player not found: " + name + "#" + tag);

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw new RuntimeException("API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch account: " + e.getMessage());
            throw new RuntimeException("Failed to fetch account: " + name + "#" + tag, e);
        }
    }

    public JsonNode fetchMatches(String puuid, String region) {
        try {
            String url = String.format(
                    "%s/matches/%s/%s?api_key=%s",
                    apiBaseUrl, region, puuid, apiKey
            );

            System.out.println("📡 Fetching matches: " + url);

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("⚠️ Matches API error: " + root);
            return null;

        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("ℹ️ No matches found (404)");
            return null;

        } catch (HttpClientErrorException.TooManyRequests e) {
            System.err.println("⚠️ Rate limit exceeded");
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch matches", e);
        }
    }

    public JsonNode fetchMMRHistory(String puuid, String region) {
        try {
            String url = String.format("%s/by-puuid/mmr-history/%s/%s?api_key=%s",
                    apiBaseUrl, region, puuid, apiKey);

            System.out.println("📡 Fetching MMR history: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            System.out.println("📥 MMR response status: " + root.get("status").asInt());

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("⚠️ MMR API Error: " + root);
            return null;

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch MMR: " + e.getMessage());
            return null;
        }
    }
}