package gytis.courier.adapter.in.rest.auth;

import gytis.courier.adapter.in.rest.auth.dto.AuthMapper;
import gytis.courier.adapter.in.rest.auth.dto.LoginRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.in.auth.LoginUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.application.port.in.auth.RefreshUseCase;
import gytis.courier.common.CookieUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;
    private final AuthMapper mapper;
    @Value("${jwt.refresh.expiry}")
    private int refreshExpiry;

    public AuthController(LoginUseCase loginUseCase, RefreshUseCase refreshUseCase, AuthMapper mapper) {
        this.loginUseCase = loginUseCase;
        this.refreshUseCase = refreshUseCase;
        this.mapper = mapper;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<AuthenticatedPerson> me(@AuthenticationPrincipal AuthenticatedPerson person) {
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        setCookies(loginUseCase.login(mapper.toCommand(request)), response);

        return ResponseEntity.ok(new ApiResponse("success", "You have successfully logged in"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@Nonnull HttpServletRequest request, HttpServletResponse response) {
       String refreshToken = CookieUtil.getToken(request, "refresh");

       setCookies(refreshUseCase.refresh(refreshToken), response);
       return ResponseEntity.ok().build();
    }

    private void setCookies(AuthTokens tokens, HttpServletResponse response) {
        Cookie jwt = CookieUtil.createCookie(refreshExpiry, "jwt", tokens.jwt());
        Cookie refresh = CookieUtil.createCookie(refreshExpiry, "refresh", tokens.refresh());

        response.addCookie(jwt);
        response.addCookie(refresh);
    }
}