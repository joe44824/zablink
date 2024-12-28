package com.zabbix.zablink.controller;

import com.zabbix.zablink.service.HostsPlaybookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ansible/hosts")
public class HostsPlaybookController {

    private final HostsPlaybookService hostsPlaybookService;

    public HostsPlaybookController(HostsPlaybookService hostsPlaybookService) {
        this.hostsPlaybookService = hostsPlaybookService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runPlaybook() {
        try {
            hostsPlaybookService.execute();
            return ResponseEntity.ok("Hosts playbook executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error running hosts playbook: " + e.getMessage());
        }
    }
}
