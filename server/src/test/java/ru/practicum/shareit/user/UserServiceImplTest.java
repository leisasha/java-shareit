package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserService userService;
    private UserRepository userRepository;

    private User user1;
    private UserDto userDto1;

    private User user2;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        userService = new UserServiceImpl(userRepository);

        user1 = new User();
        user1.setId(1L);
        user1.setName("testName1");
        user1.setEmail("test1@google.com");
        userDto1 = UserMapper.toUserDto(user1);

        user2 = new User();
        user2.setId(2L);
        user2.setName("testName2");
        user2.setEmail("test2@google.com");
        userDto2 = UserMapper.toUserDto(user2);
    }


    @Test
    void createUserSimplePositive() {
        UserDto result = userService.createUser(userDto1);
        assertNotNull(result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUserShouldSaveUser() {
        when(userRepository.existsByEmail(userDto1.getEmail())).thenReturn(false);
        UserDto result = userService.createUser(userDto1);
        assertNotNull(result);
        assertEquals(userDto1.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUserShouldThrowConflictExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(userDto1.getEmail())).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.createUser(userDto1));
        verify(userRepository, never()).save(any());
    }


    @Test
    void updateUserShouldUpdateExistingUser() {
        when(userRepository.findById(userDto1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.existsByEmail(any())).thenReturn(false);

        UserDto result = userService.updateUser(userDto1.getId(), userDto1);

        assertNotNull(result);
        assertEquals(userDto1.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUserShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, userDto1));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserShouldThrowConflictExceptionWhenEmailExists() {
        when(userRepository.findById(userDto1.getId())).thenReturn(Optional.of(user2));
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(userDto1.getId(), userDto1));
        verify(userRepository, never()).save(any());
    }


    @Test
    void getUserByIdShouldReturnUser() {
        when(userRepository.findById(userDto1.getId())).thenReturn(Optional.of(user1));

        UserDto result = userService.getUserById(userDto1.getId());

        assertNotNull(result);
        assertEquals(userDto1.getId(), result.getId());
        verify(userRepository, times(1)).findById(userDto1.getId());
    }

    @Test
    void getUserByIdShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }


    @Test
    void getUsers() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> result = userService.getUsers();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void deleteUserShouldDeleteUser() {
        when(userRepository.existsById(userDto1.getId())).thenReturn(true);
        userService.deleteUser(userDto1.getId());
        verify(userRepository, times(1)).deleteById(userDto1.getId());
    }

    @Test
    void deleteUserShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
