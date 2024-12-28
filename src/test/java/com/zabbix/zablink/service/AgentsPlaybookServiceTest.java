package com.zabbix.zablink.service;

import com.zabbix.zablink.config.AnsibleConfig;
import com.zabbix.zablink.config.AnsibleConfig.Playbooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class AgentsPlaybookServiceTest {

    @Mock
    private AnsibleConfig ansibleConfig;

    @Mock
    private Playbooks playbooks;

    private AgentsPlaybookService agentsPlaybookService;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Stub the methods to return valid values
        when(ansibleConfig.getBaseDir()).thenReturn("src/main/resources/ansible");
        when(ansibleConfig.getInventoryDir()).thenReturn("inventory.ini");
        when(ansibleConfig.getRolesDir()).thenReturn("roles");
        when(ansibleConfig.getGroupVars()).thenReturn("group_vars/vars.yml");
        when(ansibleConfig.getPlaybooks()).thenReturn(playbooks);
        when(playbooks.getAgents()).thenReturn("00-zabbix-agents.yml");

        // Manually create AgentsPlaybookService with the mocked AnsibleConfig
        agentsPlaybookService = new AgentsPlaybookService(ansibleConfig);
    }

    @Test
    void testExecute() throws IOException, InterruptedException {
        // Spy on the service to verify method calls
        AgentsPlaybookService spyService = spy(agentsPlaybookService);

        // Execute the method
        spyService.execute();

        // Verify that runPlaybook was called with the correct playbook
        verify(spyService).runPlaybook("00-zabbix-agents.yml");
    }
}
