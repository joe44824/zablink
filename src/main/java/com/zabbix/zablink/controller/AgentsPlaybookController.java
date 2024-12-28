package com.zabbix.zablink.controller;

import com.zabbix.zablink.service.AgentsPlaybookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ansible/agents")
public class AgentsPlaybookController {

    private final AgentsPlaybookService agentsPlaybookService;

    public AgentsPlaybookController(AgentsPlaybookService agentsPlaybookService) {
        this.agentsPlaybookService = agentsPlaybookService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runPlaybook() {
        try {
            agentsPlaybookService.execute();
            return ResponseEntity.ok("Agents playbook executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error running agents playbook: " +
                    e.getMessage());
        }
    }
}
