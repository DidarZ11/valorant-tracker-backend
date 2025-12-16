package com.valorant.tracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.valorant.tracker.service.api.ValorantApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackerTestController {

    private final ValorantApiService apiService;

    // Убрали {region} из пути, добавили как query параметр
    @GetMapping("/account/{name}/{tag}")
    public JsonNode account(
            @PathVariable String name,
            @PathVariable String tag,
            @RequestParam(defaultValue = "eu") String region) {  // ← query параметр вместо path

        return apiService.fetchAccount(name, tag, region);
    }
}