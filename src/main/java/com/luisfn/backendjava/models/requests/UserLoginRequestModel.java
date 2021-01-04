package com.luisfn.backendjava.models.requests;

//Esto es lo que tienes que enviar en el body para el Login del usuario
public class UserLoginRequestModel {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
