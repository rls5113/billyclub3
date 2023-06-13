package com.billyclub.points.dto;

import com.billyclub.points.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
        private Long id;
        @NotEmpty(message = "Username should not be empty")
        private String username;
        @NotEmpty(message = "First Name should not be empty")
        private String firstName;
        @NotEmpty(message = "Last Name should not be empty")
        private String lastName;
        @NotEmpty(message = "Email should not be empty")
        @Email(message="Invalid email address")
        private String email;
        @NotEmpty(message = "Password should not be empty")
        private String password;
        private String mobile;
        private Integer points;
        private List<Role> roles;
        private String resetPasswordToken;
        private Boolean active;
        public String getName() {
                String name = (getFirstName()==null) ? "" : getFirstName();
                name += (getLastName()==null) ? "" : " "+ getLastName();
                return name;
        }

}
