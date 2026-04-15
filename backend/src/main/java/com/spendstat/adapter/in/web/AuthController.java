package com.spendstat.adapter.in.web;

import com.spendstat.adapter.in.web.dto.auth.LoginRequest;
import com.spendstat.adapter.in.web.dto.auth.RefreshRequest;
import com.spendstat.adapter.in.web.dto.auth.RegisterRequest;
import com.spendstat.adapter.in.web.dto.auth.TokenResponse;
import com.spendstat.domain.user.port.in.LoginUseCase;
import com.spendstat.domain.user.port.in.LogoutUseCase;
import com.spendstat.domain.user.port.in.RefreshTokenUseCase;
import com.spendstat.domain.user.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return TokenResponse.from(registerUserUseCase.register(
                new RegisterUserUseCase.RegisterCommand(request.email(), request.password())
        ));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return TokenResponse.from(loginUseCase.login(
                new LoginUseCase.LoginCommand(request.email(), request.password())
        ));
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return TokenResponse.from(refreshTokenUseCase.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request) {
        logoutUseCase.logout(request.refreshToken());
    }
}
