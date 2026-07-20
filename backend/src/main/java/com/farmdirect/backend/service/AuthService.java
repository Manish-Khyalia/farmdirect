package com.farmdirect.backend.service;

import com.farmdirect.backend.dto.request.LoginRequest;
import com.farmdirect.backend.dto.request.RegisterRequest;
import com.farmdirect.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}