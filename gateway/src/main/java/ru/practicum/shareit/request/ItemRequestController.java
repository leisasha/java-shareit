package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Positive @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
