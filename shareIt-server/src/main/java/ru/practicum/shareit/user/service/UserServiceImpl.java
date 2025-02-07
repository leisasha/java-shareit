package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        boolean emailExists = userRepository.existsByEmail(userDto.getEmail());
        if (emailExists) {
            throw new ConflictException("Данный имейл уже используется");
        }

        User user = userRepository.save(UserMapper.toUser(userDto, 0));
        log.error("Пользователь создан в БД: userId = " + user.getId());
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        boolean emailExists = userRepository.existsByEmail(userDto.getEmail()) &&
                !existingUser.getEmail().equals(userDto.getEmail());
        if (emailExists) {
            throw new ConflictException("Данный имейл уже используется");
        }

        UserMapper.updateUserFields(existingUser, userDto);
        return UserMapper.toUserDto(userRepository.save(existingUser));
    }

    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден с ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
