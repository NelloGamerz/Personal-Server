// package com.example.Personal_Server.Controller;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.autoconfigure.AutoConfiguration;
// // import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.test.web.servlet.MockMvc;

// @SpringBootTest
// @AutoConfigurationMockMvc
// class SystemControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Test
//     void shouldReturnSystemStatus() throws Exception {
//         mockMvc.perform(get("/system/status"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.os").exists())
//                 .andExpect(jsonPath("$.osVersion").exists())
//                 .andExpect(jsonPath("$.javaVersion").exists())
//                 .andExpect(jsonPath("$.availableProcessors").isNumber())
//                 .andExpect(jsonPath("$.freeMemoryMB").isNumber())
//                 .andExpect(jsonPath("$.totalMemoryMB").isNumber())
//                 .andExpect(jsonPath("$.uptime").isNumber());
//     }
// }


// package com.example.Personal_Server.Controller;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(SystemController.class)
// class SystemControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Test
//     void shouldReturnSystemStatus() throws Exception {
//         mockMvc.perform(get("/system/status"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.os").exists())
//                 .andExpect(jsonPath("$.osVersion").exists())
//                 .andExpect(jsonPath("$.javaVersion").exists())
//                 .andExpect(jsonPath("$.availableProcessors").isNumber())
//                 .andExpect(jsonPath("$.freeMemoryMB").isNumber())
//                 .andExpect(jsonPath("$.totalMemoryMB").isNumber())
//                 .andExpect(jsonPath("$.uptime").isNumber());
//     }

//     @Test
//     void shouldReturnJava21() throws Exception {
//         mockMvc.perform(get("/system/status"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.javaVersion").value("21"));
//     }
// }



package com.example.Personal_Server.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
