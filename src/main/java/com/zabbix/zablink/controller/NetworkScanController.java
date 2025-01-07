package com.zabbix.zablink.controller;

import com.zabbix.zablink.model.HostFact;
import com.zabbix.zablink.service.NetworkScanService;
import com.zabbix.zablink.service.HostFactsService;
import com.zabbix.zablink.service.HostFactsPlaybookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class NetworkScanController {

    @Autowired
    private NetworkScanService networkScanService;

    @Autowired
    private HostFactsService hostFactsService;

    @Autowired
    private HostFactsPlaybookService hostFactsPlaybookService;

    @GetMapping("/scan-network")
    public ResponseEntity<List<HostFact>> scanNetwork() {
        try {
            // 1. Get IPs
            List<String> allLiveHosts = new ArrayList<>();
            List<String> privateCIDRs = networkScanService.getPrivateCIDRs();

            for (String cidr : privateCIDRs) {
                List<String> liveHosts = networkScanService.scanNetwork(cidr);
                allLiveHosts.addAll(liveHosts);
            }

            System.out.println("Captured IPs");
            System.out.println(allLiveHosts);

            // 2. Save IPs to INI file
            networkScanService.saveToYamlFile(allLiveHosts);

            // 3. Execute Ansible playbook
            hostFactsPlaybookService.execute();

            // 4. Fetch and combine host facts
            List<Map<String, Object>> hostFactsData = hostFactsService.fetchAndCombineHostFacts();

            // 5. Map the host facts to the HostFact model
            List<HostFact> hostFacts = hostFactsData.stream().map(data -> {
                HostFact fact = new HostFact();
                fact.setHostname((String) data.get("hostname"));
                fact.setUptimeSeconds((String) data.get("uptime_seconds"));
                fact.setBiosDate((String) data.get("bios_date"));
                fact.setOsVersion((String) data.get("os_version"));
                return fact;
            }).collect(Collectors.toList());

            // Return as ResponseEntity
            return ResponseEntity.ok(hostFacts);

        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
