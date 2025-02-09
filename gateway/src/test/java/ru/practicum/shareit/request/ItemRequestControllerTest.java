package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.now(), List.of());

    @Test
    void createRequestShouldReturnCreatedRequest() throws Exception {
        when(itemRequestClient.createRequest(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(requestDto));

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));

        verify(itemRequestClient, times(1)).createRequest(anyLong(), any());
    }

    @Test
    void getUserRequestsShouldReturnListOfRequests() throws Exception {
        when(itemRequestClient.getUserRequests(anyLong())).thenReturn(ResponseEntity.ok(List.of(requestDto)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        verify(itemRequestClient, times(1)).getUserRequests(anyLong());
    }

    @Test
    void getAllRequestsShouldReturnListOfRequests() throws Exception {
        when(itemRequestClient.getAllRequests(anyLong())).thenReturn(ResponseEntity.ok(List.of(requestDto)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        verify(itemRequestClient, times(1)).getAllRequests(anyLong());
    }

    @Test
    void getRequestByIdShouldReturnRequest() throws Exception {
        when(itemRequestClient.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));

        verify(itemRequestClient, times(1)).getRequestById(anyLong(), anyLong());
    }
}
