package com.zabbix.zablink.controller;

import com.zabbix.zablink.model.Host;
import com.zabbix.zablink.service.NetworkScanService;
import com.zabbix.zablink.service.HostDetailsService;
import com.zabbix.zablink.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NetworkScanController {

    @Autowired
    private NetworkScanService networkScanService;

    @Autowired
    private HostDetailsService hostDetailsService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/scan-network")
    public List<Host> scanNetwork() {
        try {
            // Step 1: Scan the network and get live hosts
            List<String> liveHosts = networkScanService.scanNetwork();

            // Step 2: Fetch details for each live host
            List<Host> hostDetails = new ArrayList<>();
            for (String ip : liveHosts) {
                hostDetails.add(hostDetailsService.getHostDetails(ip));
            }

            // Step 3: Save the inventory to a YAML file
            inventoryService.saveToYamlFile(liveHosts);

            return hostDetails;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during network scan: " + e.getMessage());
        }
    }
}
