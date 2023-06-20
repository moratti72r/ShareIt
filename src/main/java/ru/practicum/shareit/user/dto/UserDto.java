package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private long id;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String name;
    @Email
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String email;

}
