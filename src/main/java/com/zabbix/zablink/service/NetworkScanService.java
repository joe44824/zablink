package com.zabbix.zablink.service;

// Service Class
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkScanService extends AnsibleBaseService {

    private static final String INVENTORY_FILE_PATH = "src/main/resources/ansible/inventory.yml";

    public List<String> getPrivateCIDRs() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("bash", "-c",
                "ip -4 addr show | grep -oP '(?<=inet\\s)\\d+(\\.\\d+){3}/\\d+' | grep -E '^10\\.|^172\\.(1[6-9]|2[0-9]|3[0-1])\\.|^192\\.168\\.' | tail -n +2")
                .start();
        process.waitFor();

        return new String(process.getInputStream().readAllBytes())
                .lines()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> scanNetwork(String cidr) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("nmap", "-sn", cidr).start();
        process.waitFor();

        return new String(process.getInputStream().readAllBytes())
                .lines()
                .filter(line -> line.contains("Nmap scan report for"))
                .map(line -> line.substring(line.lastIndexOf(' ') + 1).replace("(", "").replace(")", ""))
                .filter(ip -> !ip.endsWith(".1"))
                .collect(Collectors.toList());
    }

    public void saveToInventoryFile(List<String> liveHosts) throws IOException {
        Path filePath = Paths.get(INVENTORY_FILE_PATH);
        Files.createDirectories(filePath.getParent()); // Ensure the directory exists

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("all:\n");
            writer.write("  hosts:\n");

            if (!liveHosts.isEmpty()) {
                // First host is zabbix_server_01
                String zabbixServer = liveHosts.get(0);
                writer.write("    zabbix_server_01:\n");
                writer.write("      ansible_host: " + zabbixServer + "\n");
                writer.write("      host_groups:\n");
                writer.write("        - \"Linux servers\"\n");

                // Remaining hosts are zabbix_agents
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

            writer.write("\n  children:\n");
            writer.write("    zabbix_server:\n");
            writer.write("      hosts:\n");
            writer.write("        zabbix_server_01:\n");
            writer.write("    zabbix_agents:\n");
            writer.write("      hosts:\n");

            for (int i = 1; i < liveHosts.size(); i++) {
                writer.write("        zabbix_agent_0" + i + ":\n");
            }
        }
    }
}
