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
        private String username;
        @NotEmpty
        private String firstName;
        @NotEmpty
        private String lastName;
        @NotEmpty(message = "Email should not be empty")
        @Email
        private String email;
        @NotEmpty(message = "Password should not be empty")
        private String password;
        private Integer points;
        private List<Role> roles;

        public String getName() {
                String name = (getFirstName()==null) ? "" : getFirstName();
                name += (getLastName()==null) ? "" : " "+ getLastName();
                return name;
        }

}
