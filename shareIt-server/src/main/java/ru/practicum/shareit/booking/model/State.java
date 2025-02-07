package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.NotFoundException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State fromString(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new NotFoundException("Unknown state: " + state);
        }
    }
}
