package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserDto createUser(UserDto userDto) {
        Optional<User> alreadyExistUser = userStorage.findByEmail(userDto.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new ConflictException("Данный имейл уже используется");
        }

        return UserMapper.toUserDto(userStorage.create(userDto));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User updatedUser = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Optional<User> alreadyExistUser = userStorage.findByEmail(userDto.getEmail());
        if (alreadyExistUser.isPresent() && alreadyExistUser.get().getId() != userId) {
            throw new ConflictException("Данный имейл уже используется");
        }

        return UserMapper.toUserDto(userStorage.update(updatedUser, userDto));
    }

    public UserDto getUserById(long userId) {
        return userStorage.getUserById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public List<UserDto> getUsers() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
