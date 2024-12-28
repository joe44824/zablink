package com.zabbix.zablink.service;

import com.zabbix.zablink.config.AnsibleConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ServerPlaybookService extends AnsibleBaseService {

    private final AnsibleConfig ansibleConfig;

    public ServerPlaybookService(AnsibleConfig ansibleConfig) {
        this.ansibleConfig = ansibleConfig;
    }

    public void execute() throws IOException, InterruptedException {
        runPlaybook(ansibleConfig.getPlaybooks().getServer());
    }
}
