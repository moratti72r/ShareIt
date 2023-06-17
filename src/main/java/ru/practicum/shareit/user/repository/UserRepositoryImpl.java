package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final Map<Long,User> users = new HashMap<>();

    @Override
    public User addUser(User user) {

        user.setId(getId());
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(UserDto userDto) {
        if (users.containsKey(userDto.getId())){
            UserMapper.fromUserDto(users.get(userDto.getId()), userDto);
        }
        return users.get(userDto.getId());
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteUserById(long id) {
        if (users.containsKey(id)){
            users.remove(id);
        }
    }

    private long getId() {
        long lastId = users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return lastId+1;
    }

    public boolean isNonExistEmail (String email){
        return users.values().stream()
                .noneMatch(user -> user.getEmail().equals(email));
    }

    public boolean contains (long id) {
        return users.containsKey(id);
    }
}
