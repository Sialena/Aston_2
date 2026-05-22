package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_ShouldReturnListOfUserDtos() throws Exception {
        UserDto dto1 = new UserDto(1L, "Alice", "alice@example.com", 30);
        UserDto dto2 = new UserDto(2L, "Bob", "bob@example.com", 25);

        when(userService.findAll()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUserDto() throws Exception {
        UserDto dto = new UserDto(1L, "Alice", "alice@example.com", 30);
        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void create_ShouldReturnCreatedUserDto() throws Exception {
        UserDto input = new UserDto(null, "Charlie", "charlie@example.com", 28);
        UserDto saved = new UserDto(3L, "Charlie", "charlie@example.com", 28);

        when(userService.create(any(UserDto.class))).thenReturn(saved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"));
    }

    @Test
    void update_WhenExists_ShouldReturnUpdatedUserDto() throws Exception {
        UserDto updated = new UserDto(1L, "Alice Updated", "alice.new@example.com", 31);
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice.new@example.com"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserById_WhenNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new IllegalStateException("User с id 99 не найден"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isInternalServerError());
    }
}