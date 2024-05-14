package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.config.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = MainController.class)
@Import({SecurityTestConfig.class})
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/ [GET] mapping test")
    public void rootURIGetMappingTest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("main route"));
    }

}
