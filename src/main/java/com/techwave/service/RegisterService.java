package com.techwave.service;

import com.techwave.utils.Result;


public interface RegisterService {
    Result register(String username, String password, String email, String account);
}
