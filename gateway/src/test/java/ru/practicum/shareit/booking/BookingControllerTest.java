package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(1L)
            .build();

    @Test
    void createBookingShouldReturnCreatedBooking() throws Exception {
        when(bookingClient.createBooking(any(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(bookingDto));

        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingClient, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void createBookingWithInvalidDatesShouldReturnBadRequest() throws Exception {
        BookingDto invalidBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        String bookingJson = objectMapper.writeValueAsString(invalidBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(any(), anyLong());
    }

    @Test
    void updateBookingStatusShouldReturnUpdatedBooking() throws Exception {
        when(bookingClient.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingClient, times(1)).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingShouldReturnBooking() throws Exception {
        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingClient, times(1)).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getUserBookingsShouldReturnBookingsList() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));

        verify(bookingClient, times(1)).getUserBookings(anyLong(), anyString());
    }

    @Test
    void getOwnerBookingsShouldReturnBookingsList() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));

        verify(bookingClient, times(1)).getOwnerBookings(anyLong(), anyString());
    }
}
