package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        if (userRepository.isNonExistEmail(userDto.getEmail())) {
            User user = UserMapper.fromUserDto(new User(), userDto);
            UserDto result = userRepository.addUser(user);

            log.info("Пользователь с id={} добавлен", result.getId());
            return result;
        } else throw new DuplicateValuesException(UserRepositoryImpl.class);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        if (!userRepository.contains(id)) {
            throw new NotFoundException(UserRepositoryImpl.class);
        }
        if (userDto.getEmail() != null
                && !userRepository.isNonExistEmail(userDto.getEmail())
                && !userRepository.findUserById(id).getEmail().equals(userDto.getEmail())) {
            throw new DuplicateValuesException(UserRepositoryImpl.class);
        }
        User user = userRepository.findUserById(id);
        UserMapper.fromUserDto(user, userDto);

        UserDto result = userRepository.updateUser(id, user);
        log.info("Пользователь c id={} успешно изменен", id);

        return result;
    }

    public List<UserDto> findAllUser() {
        List<UserDto> result = userRepository.findAllUser();
        log.info("Список пользователей получен");
        return result;
    }

    public UserDto findUserById(long id) {
        if (userRepository.contains(id)) {

            UserDto result = UserMapper.toUserDto(userRepository.findUserById(id));
            log.info("Пользователь с id={} получен", id);
            return result;
        } else throw new NotFoundException(UserRepositoryImpl.class);
    }

    public void deleteUserById(long id) {
        if (userRepository.contains(id)) {
            userRepository.deleteUserById(id);
            log.info("Пользователь с id={} удален", id);
        } else throw new NotFoundException(UserRepositoryImpl.class);
    }
}
