package com.spendstat.application.auth;

import com.spendstat.application.exception.EmailAlreadyExistsException;
import com.spendstat.application.exception.InvalidCredentialsException;
import com.spendstat.application.exception.InvalidTokenException;
import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.RefreshToken;
import com.spendstat.domain.user.TokenPair;
import com.spendstat.domain.user.User;
import com.spendstat.domain.user.port.in.LoginUseCase;
import com.spendstat.domain.user.port.in.RegisterUserUseCase;
import com.spendstat.domain.user.port.out.RefreshTokenRepository;
import com.spendstat.domain.user.port.out.TokenGeneratorPort;
import com.spendstat.domain.user.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private TokenGeneratorPort tokenGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpiryDays", 30L);
    }

    @Test
    void register_createsUserAndReturnsTokenPair() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        User savedUser = User.from(UserId.generate(), Email.of("user@example.com"), "hashed", Instant.now());
        when(userRepository.save(any())).thenReturn(savedUser);
        when(tokenGenerator.generateAccessToken(any())).thenReturn("access-token");
        RefreshToken savedToken = RefreshToken.init(savedUser.getId(), 30);
        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        TokenPair result = authService.register(new RegisterUserUseCase.RegisterCommand("user@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() ->
                authService.register(new RegisterUserUseCase.RegisterCommand("taken@example.com", "pass")))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void login_returnsTokenPairForValidCredentials() {
        UserId userId = UserId.generate();
        User user = User.from(userId, Email.of("user@example.com"), "hashed", Instant.now());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(tokenGenerator.generateAccessToken(any())).thenReturn("access-token");
        RefreshToken savedToken = RefreshToken.init(userId, 30);
        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        TokenPair result = authService.login(new LoginUseCase.LoginCommand("user@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
    }

    @Test
    void login_throwsForUnknownEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(new LoginUseCase.LoginCommand("ghost@example.com", "pass")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_throwsForWrongPassword() {
        User user = User.from(UserId.generate(), Email.of("user@example.com"), "hashed", Instant.now());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new LoginUseCase.LoginCommand("user@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void refresh_throwsForUnknownToken() {
        when(refreshTokenRepository.findByTokenValue(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("unknown-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refresh_throwsForExpiredToken() {
        UserId userId = UserId.generate();
        RefreshToken expired = RefreshToken.from(
                UUID.randomUUID(), userId, "token", Instant.now().minusSeconds(1), Instant.now().minusSeconds(100));
        when(refreshTokenRepository.findByTokenValue("token")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(InvalidTokenException.class);
        verify(refreshTokenRepository).deleteByTokenValue("token");
    }
}
