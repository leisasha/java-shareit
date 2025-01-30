package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @NotNull(message = "Поле 'start' обязательно для заполнения")
    private LocalDateTime start;
    @NotNull(message = "Поле 'end' обязательно для заполнения")
    private LocalDateTime end;
    @NotNull(message = "Поле 'itemId' обязательно для заполнения")
    private Long itemId;
    private Status status;
    private UserDto booker;
    private ItemDto item;
}
