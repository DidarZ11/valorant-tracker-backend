package com.valorant.tracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.valorant.tracker.repository.AgentRepository;
import com.valorant.tracker.service.api.ValorantApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final ValorantApiService apiService;
    private final AgentRepository agentRepository;

    @PostMapping("/update-agent-icons")
    public String updateAgentIcons() {
        try {
            JsonNode response = apiService.fetchAgentData();

            if (response == null || !response.isArray()) {
                return "Failed to fetch agent data";
            }

            int updated = 0;

            for (JsonNode agent : response) {
                if (agent.has("isPlayableCharacter") && agent.get("isPlayableCharacter").asBoolean()) {
                    String name = agent.get("displayName").asText();
                    String iconUrl = agent.get("displayIcon").asText();

                    agentRepository.findByAgentName(name).ifPresent(dbAgent -> {
                        dbAgent.setDisplayIconUrl(iconUrl);
                        agentRepository.save(dbAgent);
                        System.out.println("✅ Updated icon for: " + name + " -> " + iconUrl);
                    });

                    updated++;
                }
            }

            return "✅ Updated " + updated + " agent icons!";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/list-agents")
    public String listAgents() {
        try {
            JsonNode response = apiService.fetchAgentData();

            if (response == null || !response.isArray()) {
                return "Failed to fetch agent data";
            }

            StringBuilder result = new StringBuilder();
            result.append("<html><head><style>body{font-family:Arial;padding:20px;background:#f5f5f5;}");
            result.append(".agent{background:white;margin:20px 0;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);}");
            result.append("img{border-radius:8px;}</style></head><body>");
            result.append("<h1>Valorant Agents</h1>");

            for (JsonNode agent : response) {
                if (agent.has("isPlayableCharacter") && agent.get("isPlayableCharacter").asBoolean()) {
                    String name = agent.get("displayName").asText();
                    String uuid = agent.get("uuid").asText();
                    String iconUrl = agent.get("displayIcon").asText();

                    result.append("<div class='agent'>");
                    result.append("<h3>").append(name).append("</h3>");
                    result.append("<p><strong>UUID:</strong> ").append(uuid).append("</p>");
                    result.append("<img src='").append(iconUrl).append("' width='150'/><br><br>");
                    result.append("<strong>SQL:</strong><br>");
                    result.append("<textarea style='width:100%;height:60px;font-family:monospace;'>")
                            .append("UPDATE agents SET display_icon_url = '").append(iconUrl)
                            .append("' WHERE agent_name = '").append(name).append("';")
                            .append("</textarea>");
                    result.append("</div>");
                }
            }

            result.append("</body></html>");
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/list-maps")
    public String listMaps() {
        try {
            JsonNode response = apiService.fetchMapsData();

            if (response == null || !response.isArray()) {
                return "Failed to fetch maps data";
            }

            StringBuilder result = new StringBuilder();
            result.append("<html><head><style>body{font-family:Arial;padding:20px;background:#f5f5f5;}");
            result.append(".map{background:white;margin:20px 0;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);}");
            result.append("img{border-radius:8px;max-width:100%;}</style></head><body>");
            result.append("<h1>Valorant Maps</h1>");

            for (JsonNode map : response) {
                String name = map.get("displayName").asText();
                String uuid = map.get("uuid").asText();
                String splash = map.get("splash").asText();

                result.append("<div class='map'>");
                result.append("<h3>").append(name).append("</h3>");
                result.append("<p><strong>UUID:</strong> ").append(uuid).append("</p>");
                result.append("<img src='").append(splash).append("' width='400'/><br><br>");
                result.append("<strong>TypeScript code:</strong><br>");
                result.append("<textarea style='width:100%;height:40px;font-family:monospace;'>'")
                        .append(name).append("': '").append(splash).append("',</textarea>");
                result.append("</div>");
            }

            result.append("</body></html>");
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}