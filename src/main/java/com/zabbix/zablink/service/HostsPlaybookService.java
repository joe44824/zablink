package com.zabbix.zablink.service;

import com.zabbix.zablink.config.AnsibleConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HostsPlaybookService extends AnsibleBaseService {

    private final AnsibleConfig ansibleConfig;

    public HostsPlaybookService(AnsibleConfig ansibleConfig) {
        this.ansibleConfig = ansibleConfig;
    }

    public void execute() throws IOException, InterruptedException {
        runPlaybook(ansibleConfig.getPlaybooks().getHosts());
    }
}
