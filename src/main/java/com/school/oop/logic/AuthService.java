package com.school.oop.logic;

public class AuthService {

    public boolean login(String stratizenId, String password) {
        return stratizenId != null
                && stratizenId.matches("[0-9]{6}")
                && password != null;
    }
}
