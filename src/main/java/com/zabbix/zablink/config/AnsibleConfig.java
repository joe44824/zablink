package com.zabbix.zablink.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ansible")
public class AnsibleConfig {

    private String baseDir;
    private Playbooks playbooks;
    private String rolesDir;
    private String collections;
    private String inventoryDir;
    private String groupVars;
    private String sshKeyDir;

    @Data
    public static class Playbooks {
        private String agents;
        private String hosts;
        private String server;
        private String hostFacts;
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("=== Ansible Config ===");
        System.out.println("Base Directory: " + baseDir);
        System.out.println("Inventory File: " + inventoryDir);
        System.out.println("Group Variables File: " + groupVars);
        System.out.println("Roles Directory: " + rolesDir);
        System.out.println("SSH Key Directory: " + sshKeyDir);
        System.out.println("Agents Playbook: " + playbooks.getAgents());
        System.out.println("Hosts Playbook: " + playbooks.getHosts());
        System.out.println("Server Playbook: " + playbooks.getServer());
        System.out.println("HostFacts Playbook: " + playbooks.getHostFacts());
        System.out.println("======================");
    }
}