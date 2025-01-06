package com.zabbix.zablink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zabbix.zablink.service.HostFactsPlaybookService;
import com.zabbix.zablink.service.NetworkScanService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NetworkScanController {

    @Autowired
    private NetworkScanService networkScanService;

    @Autowired
    private HostFactsPlaybookService hostFactsPlaybookService;

    @GetMapping("/scan-network")
    public String scanNetwork() {
        try {
            List<String> allLiveHosts = new ArrayList<>();
            List<String> privateCIDRs = networkScanService.getPrivateCIDRs();

            for (String cidr : privateCIDRs) {
                List<String> liveHosts = networkScanService.scanNetwork(cidr);
                allLiveHosts.addAll(liveHosts);
            }

            System.out.println("Scanned IPs");
            System.out.println(allLiveHosts);

            // 1. save ips to ini file
            networkScanService.saveToInventoryFile(allLiveHosts);

            // 2. based on ini file, call ansible playbook ()
            hostFactsPlaybookService.execute();

            return "Scan complete. Inventory saved to src/main/resources/ansible/inventory.yml";

        } catch (IOException | InterruptedException e) {
            return "Error during network scan: " + e.getMessage();
        }
    }
}
