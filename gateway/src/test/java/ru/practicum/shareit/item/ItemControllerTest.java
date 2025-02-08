package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Drill")
            .description("Powerful drill")
            .available(true)
            .requestId(1L)
            .build();

    private final CommentDto commentDto = new CommentDto(1L, "Great item!", "User", LocalDateTime.now());

    @Test
    void createItemShouldReturnCreatedItem() throws Exception {
        when(itemClient.createItem(any(), anyLong())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemDto));

        String itemJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemClient, times(1)).createItem(any(), anyLong());
    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        when(itemClient.updateItem(any(), anyLong(), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

        String itemJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemClient, times(1)).updateItem(any(), anyLong(), anyLong());
    }

    @Test
    void getItemByIdShouldReturnItem() throws Exception {
        when(itemClient.getItemById(anyLong())).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemClient, times(1)).getItemById(anyLong());
    }

    @Test
    void getItemsShouldReturnListOfItems() throws Exception {
        when(itemClient.getItems(anyLong())).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

        verify(itemClient, times(1)).getItems(anyLong());
    }

    @Test
    void searchItemsShouldReturnListOfItems() throws Exception {
        when(itemClient.searchItems(any())).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mockMvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

        verify(itemClient, times(1)).searchItems(any());
    }

    @Test
    void addCommentShouldReturnCreatedComment() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(commentDto));

        String commentJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemClient, times(1)).addComment(anyLong(), anyLong(), any());
    }
}
