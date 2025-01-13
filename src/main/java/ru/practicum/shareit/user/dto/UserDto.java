package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Поле 'name' обязательно для заполнения")
    private String name;
    @NotBlank(message = "Поле 'email' обязательно для заполнения")
    @Email(message = "Некорректный формат email.")
    private String email;
}
