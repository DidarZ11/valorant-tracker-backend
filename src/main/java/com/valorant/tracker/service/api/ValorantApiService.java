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

    public JsonNode fetchAccount(String name, String tag) {
        try {
            String url = String.format("%s/account/%s/%s?api_key=%s",
                    apiBaseUrl, name.trim(), tag.trim(), apiKey);

            System.out.println("📡 Fetching account: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("❌ API Error: " + root);
            throw new RuntimeException("Player not found: " + name + "#" + tag);

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch account: " + e.getMessage());
            throw new RuntimeException("Failed to fetch account: " + name + "#" + tag, e);
        }
    }

    public JsonNode fetchMatchList(String region, String puuid) {
        try {
            String url = String.format("https://api.henrikdev.xyz/valorant/v3/by-puuid/matches/%s/%s?size=20&api_key=%s",
                    region, puuid, apiKey);

            System.out.println("📡 Fetching match list: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("⚠️ Match list API error: " + root);
            return null;

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch match list: " + e.getMessage());
            return null;
        }
    }

    public JsonNode fetchMatchDetails(String region, String matchId) {
        try {
            String url = String.format("%s/../v1/match/%s/%s?api_key=%s",
                    apiBaseUrl, region, matchId, apiKey);

            url = url.replace("/v1/../v1", "/v1");

            System.out.println("📡 Fetching match details: " + matchId);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("⚠️ Match details API error: " + root);
            return null;

        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("⚠️ Match not found: " + matchId);
            return null;
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch match details: " + e.getMessage());
            return null;
        }
    }

    public JsonNode fetchMMR(String region, String puuid) {
        try {
            String url = String.format("%s/by-puuid/mmr/%s/%s?api_key=%s",
                    apiBaseUrl, region, puuid, apiKey);

            System.out.println("📡 Fetching MMR: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            return null;
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch MMR: " + e.getMessage());
            return null;
        }
    }

    public JsonNode fetchAgentData() {
        try {
            String url = "https://valorant-api.com/v1/agents";

            System.out.println("📡 Fetching agents: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("❌ Failed to fetch agents. Status: " + root.get("status"));
            return null;

        } catch (Exception e) {
            System.err.println("❌ Error fetching agents: " + e.getMessage());
            return null;
        }
    }

    public JsonNode fetchMapsData() {
        try {
            String url = "https://valorant-api.com/v1/maps";

            System.out.println("📡 Fetching maps: " + url);

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("status") && root.get("status").asInt() == 200) {
                return root.get("data");
            }

            System.err.println("❌ Failed to fetch maps. Status: " + root.get("status"));
            return null;

        } catch (Exception e) {
            System.err.println("❌ Error fetching maps: " + e.getMessage());
            return null;
        }
    }
}