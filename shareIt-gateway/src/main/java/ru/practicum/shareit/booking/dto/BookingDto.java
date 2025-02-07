package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @FutureOrPresent
    @NotNull(message = "Поле 'start' обязательно для заполнения")
    private LocalDateTime start;
    @Future
    @NotNull(message = "Поле 'end' обязательно для заполнения")
    private LocalDateTime end;
    @NotNull(message = "Поле 'itemId' обязательно для заполнения")
    private Long itemId;
    private Status status;
    private UserDto booker;
    private ItemDto item;
}
