package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(UserDto userDto);

    User update(User user, UserDto userDto);

    Collection<User> getAll();

    Optional<User> getUserById(long id);

    void deleteUser(long id);

    Optional<User> findByEmail(String email);
}
