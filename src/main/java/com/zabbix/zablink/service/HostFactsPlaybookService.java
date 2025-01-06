package com.zabbix.zablink.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.zabbix.zablink.config.AnsibleConfig;

@Service
public class HostFactsPlaybookService extends AnsibleBaseService {

    private final AnsibleConfig ansibleConfig;

    public HostFactsPlaybookService(AnsibleConfig ansibleConfig) {
        this.ansibleConfig = ansibleConfig;
    }

    public void execute() throws IOException, InterruptedException {
        runPlaybook(ansibleConfig.getPlaybooks().getHostFacts());
    }
}