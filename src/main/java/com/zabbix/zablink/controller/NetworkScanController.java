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
import java.util.*;
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
            // Map to store private IPs associated with live hosts
            Map<String, String> hostToPrivateIpMap = new HashMap<>();

            // 1. Get IPs
            List<String> privateCIDRs = networkScanService.getPrivateCIDRs();
            for (String cidr : privateCIDRs) {
                List<String> liveHosts = networkScanService.scanNetwork(cidr);
                for (String host : liveHosts) {
                    hostToPrivateIpMap.put(host, cidr);
                }
            }

            System.out.println("Captured IPs");
            System.out.println(hostToPrivateIpMap);

            // 2. Save IPs to INI file
            networkScanService.saveToYamlFile(new ArrayList<>(hostToPrivateIpMap.keySet()));

            // 3. Execute Ansible playbook
            hostFactsPlaybookService.execute();

            // 4. Fetch and combine host facts
            List<Map<String, Object>> hostFactsData = hostFactsService.fetchAndCombineHostFacts();

            // 5. Map the host facts to the HostFact model
            List<HostFact> hostFacts = hostFactsData.stream().map(data -> {
                HostFact fact = new HostFact();
                String hostname = (String) data.get("hostname");

                fact.setHostname(hostname);
                fact.setUptimeSeconds((String) data.get("uptime_seconds"));
                fact.setBiosDate((String) data.get("bios_date"));
                fact.setOsVersion((String) data.get("os_version"));
                fact.setIp(hostToPrivateIpMap.getOrDefault(hostname, "Unknown"));
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
