package ru.practicum.shareit.usertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.xml.bind.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createUser() throws Exception {
        UserDto userDto = makeUserDto(1, "Ivan", "ivan@email");
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void catchValidationExceptionWhenCreateUserWithEmailNotValid() throws Exception {
        UserDto userDto = makeUserDto(1, "Ivan", "ivanel");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
    }

    @Test
    void catchValidationExceptionWhenCreateUserWithNameNotValid() throws Exception {
        UserDto userDto = makeUserDto(1, "", "ivanel");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
    }

    @Test
    void patchUser() throws Exception {

        UserDto userDto = makeUserDto(1, "Ivan", "new@email");

        UserDto updateUser = makeUserDto(1, null, "new@email");

        when(userService.updateUser(1, updateUser))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void catchNotFoundExceptionPatchUserWithUserIsNotFound() throws Exception {

        UserDto updateUser = makeUserDto(99, null, "duplicate@email");

        when(userService.updateUser(99, updateUser))
                .thenThrow(new NotFoundException(UserRepository.class));

        mvc.perform(patch("/users/{id}", 99)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void catchDuplicateValuesExceptionPatchUserWithDuplicateEmail() throws Exception {

        UserDto updateUser = makeUserDto(1, null, "duplicate@email");

        when(userService.updateUser(1, updateUser))
                .thenThrow(new DuplicateValuesException(UserRepository.class));

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(DuplicateValuesException.class));
    }

    @Test
    void findAll() throws Exception {

        UserDto userDto1 = makeUserDto(1, "Ivan1", "ivan1@email");
        UserDto userDto2 = makeUserDto(2, "Ivan2", "ivan2@email");
        UserDto userDto3 = makeUserDto(3, "Ivan3", "ivan3@email");

        when(userService.findAllUser())
                .thenReturn(Arrays.asList(userDto1, userDto2, userDto3));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(userDto1, userDto2, userDto3))));
    }

    @Test
    void findUserById() throws Exception {

        UserDto userDto1 = makeUserDto(1, "Ivan", "ivan@email");

        when(userService.findUserById(1))
                .thenReturn(userDto1);

        mvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }



    @Test
    void catchNotFoundExceptionWhenFindNotFoundUserById() throws Exception {

        when(userService.findUserById(1))
                .thenThrow(new NotFoundException(UserRepository.class));

        mvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    private UserDto makeUserDto(long id, String name, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }
}
