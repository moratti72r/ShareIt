package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final EntityManager entityManager;

    private final UserService userService;

    @Test
    void saveUser() {
        UserDto userDto = makeUserDto("John", "john@mail.ru");

        userService.addUser(userDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findAllUser() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan", "ivan@email"),
                makeUserDto("Petr", "petr@email"),
                makeUserDto("Vasilii", "vasilii@email")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.fromUserDto(user);
            entityManager.persist(entity);
        }
        entityManager.flush();

        List<UserDto> targetUsers = userService.findAllUser();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void findUserById() {
        UserDto userDto = makeUserDto("John", "john@mail.ru");
        User entity = UserMapper.fromUserDto(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        Long query = entityManager.createQuery("Select u.id from User u where u.email = :email", Long.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto result = userService.findUserById(query);

        assertThat(result.getId(), equalTo(query));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userService.findUserById(2);
        });
        assertTrue("ru.practicum.shareit.user.repository.UserRepository".contains(exception.getMessage()));
    }

    @Test
    void updateUser() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan", "ivan@email"),
                makeUserDto("Petr", "petr@email"),
                makeUserDto("Vasilii", "vasilii@email")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.fromUserDto(user);
            entityManager.persist(entity);
        }
        entityManager.flush();

        UserDto userDto1 = makeUserDto(null, "petr@email");
        Long query = entityManager.createQuery("Select u.id from User u where u.email = :email", Long.class)
                .setParameter("email", sourceUsers.get(0).getEmail())
                .getSingleResult();

        Exception exception = assertThrows(DuplicateValuesException.class, () -> {
            userService.updateUser(query, userDto1);
        });
        assertTrue("ru.practicum.shareit.user.repository.UserRepository".contains(exception.getMessage()));

        UserDto userDto2 = makeUserDto(null, "john@mail.ru");
        UserDto result = userService.updateUser(query, userDto2);
        assertThat(result.getId(), equalTo(query));
        assertThat(result.getName(), equalTo("Ivan"));
        assertThat(result.getEmail(), equalTo(userDto2.getEmail()));
    }

    @Test
    void deleteUserById() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan", "ivan@email"),
                makeUserDto("Petr", "petr@email"),
                makeUserDto("Vasilii", "vasilii@email")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.fromUserDto(user);
            entityManager.persist(entity);
        }
        entityManager.flush();
        Long query = entityManager.createQuery("Select u.id from User u where u.email = :email", Long.class)
                .setParameter("email", sourceUsers.get(1).getEmail())
                .getSingleResult();

        userService.deleteUserById(query);

        List<User> users = entityManager.createQuery("Select u from User u", User.class).getResultList();

        assertThat(users, hasSize(2));

    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }
}
