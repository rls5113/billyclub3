package com.billyclub.points.dto;

import com.billyclub.points.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
        @NotEmpty(message = "Username should not be empty")
        private String username;
        @NotEmpty(message = "Password should not be empty")
        private String password;

}
