package ru.practicum.shareit.user.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();

    public User create(UserDto userDto) {
        User user = UserMapper.toUser(userDto, getNextId());
        usersMap.put(user.getId(), user);
        log.trace("Экземпляр {} создан", user);
        return user;
    }

    public User update(User user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        log.trace("Экземпляр {} изменен", user);
        return user;
    }

    public Collection<User> getAll() {
        return usersMap.values();
    }

    public Optional<User> getUserById(long id) {
        if (usersMap.containsKey(id))
            return Optional.ofNullable(usersMap.get(id));
        else
            return Optional.empty();
    }

    public void deleteUser(long id) {
        usersMap.remove(id);
        log.trace("Экземпляр {} удален", id);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return usersMap.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
