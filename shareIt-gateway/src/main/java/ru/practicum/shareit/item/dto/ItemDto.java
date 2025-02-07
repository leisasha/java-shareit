package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(message = "Поле 'name' обязательно для заполнения")
    private String name;
    @NotBlank(message = "Поле 'description' обязательно для заполнения")
    private String description;
    @NotNull(message = "Поле 'available' обязательно для заполнения")
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
