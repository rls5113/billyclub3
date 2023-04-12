package com.billyclub.points.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResetPasswordDto {
    private String password;
    private String confirmPassword;
}
