package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotNull(message = "Поле 'name' обязательно для заполнения")
    private String name;
    @NotNull(message = "Поле 'description' обязательно для заполнения")
    private String description;
    @NotNull(message = "Поле 'available' обязательно для заполнения")
    private Boolean available;
}
