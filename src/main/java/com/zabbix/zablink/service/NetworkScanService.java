package com.zabbix.zablink.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkScanService {

    public List<String> scanNetwork() throws IOException, InterruptedException {
        List<String> privateCIDRs = getPrivateCIDRs();
        List<String> liveHosts = new ArrayList<>();

        for (String cidr : privateCIDRs) {
            Process process = new ProcessBuilder("nmap", "-sn", cidr).start();
            process.waitFor();

            liveHosts.addAll(new String(process.getInputStream().readAllBytes())
                    .lines()
                    .filter(line -> line.contains("Nmap scan report for"))
                    .map(line -> line.substring(line.lastIndexOf(' ') + 1).replace("(", "").replace(")", ""))
                    .filter(ip -> !ip.endsWith(".1"))
                    .collect(Collectors.toList()));
        }

        return liveHosts;
    }

    private List<String> getPrivateCIDRs() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("bash", "-c",
                "ip -4 addr show | grep -oP '(?<=inet\\s)\\d+(\\.\\d+){3}/\\d+' | grep -E '^10\\.|^172\\.(1[6-9]|2[0-9]|3[0-1])\\.|^192\\.168\\.'")
                .start();
        process.waitFor();

        return new String(process.getInputStream().readAllBytes())
                .lines()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }
}
