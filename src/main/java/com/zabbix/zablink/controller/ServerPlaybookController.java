package com.zabbix.zablink.controller;

import com.zabbix.zablink.service.ServerPlaybookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ansible/server")
public class ServerPlaybookController {

    private final ServerPlaybookService serverPlaybookService;

    public ServerPlaybookController(ServerPlaybookService serverPlaybookService) {
        this.serverPlaybookService = serverPlaybookService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runPlaybook() {
        try {
            serverPlaybookService.execute();
            return ResponseEntity.ok("Server playbook executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error running server playbook: " + e.getMessage());
        }
    }
}
