package com.zabbix.zablink.service;

import com.zabbix.zablink.config.AnsibleConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class AnsibleBaseService {

    @Autowired
    private AnsibleConfig ansibleConfig;

    protected void runPlaybook(String playbookName) throws IOException, InterruptedException {
        // Build full paths for the inventory, playbook, and vars file
        String playbookPath = ansibleConfig.getBaseDir() + "/" + playbookName;
        String inventoryPath = ansibleConfig.getBaseDir() + "/" + ansibleConfig.getInventoryDir();
        String privateKeyPath = ansibleConfig.getSshKeyDir();
        String varsFilePath = ansibleConfig.getBaseDir() + "/vars.yml";

        // Build the ansible-playbook command
        List<String> command = new ArrayList<>();
        command.add("ansible-playbook");
        command.add("-i");
        command.add(inventoryPath);
        command.add(playbookPath);
        command.add("-u");
        command.add("root");
        command.add("--private-key");
        command.add(privateKeyPath);
        command.add("-e");
        command.add("ansible_ssh_common_args='-o StrictHostKeyChecking=no'");
        command.add("-e");
        command.add("interpreter_python=/usr/bin/python3");
        command.add("-e");
        command.add("@" + varsFilePath); // Include vars.yml dynamically

        System.out.println("[Ansible] Running playbook: " + playbookName);

        // Execute the process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Merge error and output streams
        Process process = processBuilder.start();

        // Capture and log output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ansible] " + line);
            }
        }

        // Check the process exit code
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException(
                    "Playbook execution failed with exit code: " + exitCode +
                            ". Check the logs above for more details.");
        }

        System.out.println("[Ansible] Playbook executed successfully: " + playbookName);
    }
}
