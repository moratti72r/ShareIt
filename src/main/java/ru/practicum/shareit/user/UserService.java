package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User addUser(UserDto userDto) {
        if (userRepository.isNonExistEmail(userDto.getEmail())) {
            User resultUser = userRepository.addUser(userDto);
            log.info("Пользователь с id={} добавлен", resultUser.getId());
            return resultUser;
        } else throw new DuplicateValuesException(UserRepositoryImpl.class);
    }

    public User updateUser(long id, UserDto userDto) {
        if (!userRepository.contains(id)) {
            throw new NotFoundException(UserRepositoryImpl.class);
        }
        if (userDto.getEmail() != null
                && !userRepository.isNonExistEmail(userDto.getEmail())
                && !userRepository.findUserById(id).getEmail().equals(userDto.getEmail())) {
            throw new DuplicateValuesException(UserRepositoryImpl.class);
        }
        log.info("Пользователь c id={} успешно изменен", id);
        return userRepository.updateUser(id, userDto);
    }

    public List<User> findAllUser() {
        log.info("Список пользователей получен");
        return userRepository.findAllUser();
    }

    public User findUserById(long id) {
        if (userRepository.contains(id)) {
            log.info("Пользователь с id={} получен", id);
            return userRepository.findUserById(id);
        } else throw new NotFoundException(UserRepositoryImpl.class);
    }

    public void deleteUserById(long id) {
        if (userRepository.contains(id)) {
            log.info("Пользователь с id={} удален", id);
            userRepository.deleteUserById(id);
        } else throw new NotFoundException(UserRepositoryImpl.class);
    }
}
