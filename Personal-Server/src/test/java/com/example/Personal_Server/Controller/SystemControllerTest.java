package com.example.Personal_Server.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSystemStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/system/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.os").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("$.javaVersion").exists())
                .andExpect(jsonPath("$.availableProcessors").isNumber())
                .andExpect(jsonPath("$.freeMemoryMB").isNumber())
                .andExpect(jsonPath("$.totalMemoryMB").isNumber())
                .andExpect(jsonPath("$.uptime").isNumber())
                .andReturn();

        System.out.println("\n================ TEST RESPONSE ================");
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("==============================================\n");
    }

    // @Test
    // void shouldReturnJava21() throws Exception {
    //     mockMvc.perform(get("/system/status"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.javaVersion").value("21"));
    // }
}
