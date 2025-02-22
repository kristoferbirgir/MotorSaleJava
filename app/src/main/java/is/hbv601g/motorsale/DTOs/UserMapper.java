package is.hbv601g.motorsale.DTOs;

import is.hbv601g.motorsale.entities.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber());
    }

    public static User toEntity(UserDTO dto) {
        User user = new User(dto.getUsername(), dto.getFirstName(), dto.getLastName(), null, dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }
}

