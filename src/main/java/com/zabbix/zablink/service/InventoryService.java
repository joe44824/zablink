package com.zabbix.zablink.service;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class InventoryService {

    private static final String INVENTORY_FILE_PATH = "src/main/resources/ansible/inventory.yml";

    public void saveToYamlFile(List<String> liveHosts) throws IOException {
        Path filePath = Paths.get(INVENTORY_FILE_PATH);
        Files.createDirectories(filePath.getParent());

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("all:\n");
            writer.write("  hosts:\n");

            if (!liveHosts.isEmpty()) {
                String zabbixServer = liveHosts.get(0);
                writer.write("    zabbix_server_01:\n");
                writer.write("      ansible_host: " + zabbixServer + "\n");
                writer.write("      host_groups:\n");
                writer.write("        - \"Linux servers\"\n");

                for (int i = 1; i < liveHosts.size(); i++) {
                    String agentHost = liveHosts.get(i);
                    writer.write("    zabbix_agent_0" + i + ":\n");
                    writer.write("      ansible_host: " + agentHost + "\n");
                    writer.write("      name: zabbix-host-0" + i + "\n");
                    writer.write("      host_groups:\n");
                    writer.write("        - \"Linux servers\"\n");
                    writer.write("      templates:\n");
                    writer.write("        - \"Linux by Zabbix agent\"\n");
                }
            }
        }
    }
}
