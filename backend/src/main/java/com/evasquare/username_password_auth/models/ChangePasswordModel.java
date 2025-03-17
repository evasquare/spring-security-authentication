package com.evasquare.username_password_auth.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordModel {
    private String originalPassword;
    private String newPassword;
    private String newPasswordConfirmation;
}
