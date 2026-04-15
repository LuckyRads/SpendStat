package com.spendstat.application.auth;

import com.spendstat.application.exception.EmailAlreadyExistsException;
import com.spendstat.application.exception.InvalidCredentialsException;
import com.spendstat.application.exception.InvalidTokenException;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.RefreshToken;
import com.spendstat.domain.user.TokenPair;
import com.spendstat.domain.user.User;
import com.spendstat.domain.user.port.in.LoginUseCase;
import com.spendstat.domain.user.port.in.LogoutUseCase;
import com.spendstat.domain.user.port.in.RefreshTokenUseCase;
import com.spendstat.domain.user.port.in.RegisterUserUseCase;
import com.spendstat.domain.user.port.out.RefreshTokenRepository;
import com.spendstat.domain.user.port.out.TokenGeneratorPort;
import com.spendstat.domain.user.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements RegisterUserUseCase, LoginUseCase, RefreshTokenUseCase, LogoutUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.refresh-token.expiry-days}")
    private long refreshTokenExpiryDays;

    @Override
    @Transactional
    public TokenPair register(RegisterCommand command) {
        Email email = Email.of(command.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(command.email());
        }
        User user = User.init(email, passwordEncoder.encode(command.password()));
        user = userRepository.save(user);
        return generateTokenPair(user);
    }

    @Override
    @Transactional
    public TokenPair login(LoginCommand command) {
        Email email = Email.of(command.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return generateTokenPair(user);
    }

    @Override
    @Transactional
    public TokenPair refresh(String refreshTokenValue) {
        RefreshToken token = refreshTokenRepository.findByTokenValue(refreshTokenValue)
                .orElseThrow(InvalidTokenException::new);
        if (token.isExpired()) {
            refreshTokenRepository.deleteByTokenValue(refreshTokenValue);
            throw new InvalidTokenException();
        }
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(InvalidTokenException::new);
        // Rotate: delete old token before issuing new pair
        refreshTokenRepository.deleteByTokenValue(refreshTokenValue);
        return generateTokenPair(user);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.deleteByTokenValue(refreshTokenValue);
    }

    private TokenPair generateTokenPair(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user);
        RefreshToken refreshToken = RefreshToken.init(user.getId(), refreshTokenExpiryDays);
        refreshTokenRepository.save(refreshToken);
        return new TokenPair(accessToken, refreshToken.getTokenValue());
    }
}
