package com.zabbix.zablink.controller;

import com.zabbix.zablink.service.AgentsPlaybookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AgentsPlaybookController.class)
class AgentsPlaybookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentsPlaybookService agentsPlaybookService;

    @Test
    void testRunPlaybookSuccess() throws Exception {
        // Perform a POST request to the controller endpoint
        mockMvc.perform(post("/api/ansible/agents/run"))
                .andExpect(status().isOk())
                .andExpect(content().string("Agents playbook executed successfully."));

        // Verify the service method was called
        verify(agentsPlaybookService).execute();
    }

    @Test
    void testRunPlaybookFailure() throws Exception {
        // Mock the service to throw an exception
        doThrow(new RuntimeException("Mocked exception"))
                .when(agentsPlaybookService).execute();

        // Perform a POST request to the controller endpoint
        mockMvc.perform(post("/api/ansible/agents/run"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error running agents playbook: Mocked exception"));

        // Verify the service method was called
        verify(agentsPlaybookService).execute();
    }
}
