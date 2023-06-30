package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    private long id;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    @Size(max = 255)
    private String name;
    @Email
    @Size(max = 255)
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String email;

}
