package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testSerializePositive() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("test@google.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.name")
                .hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(userDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(userDto.getName()));
        assertThat(result).extractingJsonPathStringValue("$.email")
                .satisfies(email -> assertThat(email).isEqualTo(userDto.getEmail()));
    }
}
