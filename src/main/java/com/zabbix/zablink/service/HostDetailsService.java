package com.zabbix.zablink.service;

import com.zabbix.zablink.model.Host;
import com.zabbix.zablink.model.VMStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class HostDetailsService {

    public Host getHostDetails(String ip) throws IOException, InterruptedException {
        String name = fetchHostName(ip);
        String osVersion = fetchOSVersion(ip);
        VMStatus status = fetchVMStatus(ip);
        Date createdTime = fetchCreatedTime(ip);

        return new Host(name, osVersion, status, ip, createdTime);
    }

    private String fetchHostName(String ip) {
        return "Host-" + ip.replace(".", "-");
    }

    private String fetchOSVersion(String ip) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("ansible", ip, "-m", "setup", "-a", "filter=ansible_distribution*")
                .start();
        process.waitFor();

        return new String(process.getInputStream().readAllBytes())
                .lines()
                .filter(line -> line.contains("ansible_distribution"))
                .findFirst()
                .orElse("Unknown OS");
    }

    private VMStatus fetchVMStatus(String ip) {
        return VMStatus.RUNNING; // Placeholder for actual logic
    }

    private Date fetchCreatedTime(String ip) {
        return new Date(); // Placeholder for actual logic
    }
}
