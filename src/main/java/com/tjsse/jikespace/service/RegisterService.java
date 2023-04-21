package com.tjsse.jikespace.service;

import com.tjsse.jikespace.utils.Result;


public interface RegisterService {
    Result register(String username, String password, String email);
}
