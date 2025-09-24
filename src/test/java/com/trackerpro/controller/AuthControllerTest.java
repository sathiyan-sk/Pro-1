package com.trackerpro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.StudentRegistrationRequest;
import com.trackerpro.service.AuthenticationService;
import com.trackerpro.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @MockBean
    private StudentService studentService;
    
    @Test
    public void testLoginEndpoint() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@tracker.com", "admin123");
        
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testRegisterEndpoint() throws Exception {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        request.setFirstName("Test");
        request.setLastName("Student");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setGender("Male");
        request.setDob("01/01/2003");
        request.setAge(21);
        request.setLocation("Test City");
        request.setMobileNo("1234567890");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpected(status().isOk());
    }
}