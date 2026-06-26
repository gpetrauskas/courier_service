package gytis.courier.adapter.in.rest.auth;

import gytis.courier.adapter.in.rest.auth.dto.AuthMapper;
import gytis.courier.adapter.in.rest.auth.dto.LoginRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.auth.LoginResult;
import gytis.courier.application.port.in.auth.LoginUseCase;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.application.port.in.auth.RefreshUseCase;
import gytis.courier.common.CookieUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        LoginResult result = loginUseCase.login(mapper.toCommand(request));

        Cookie jwtCookie = CookieUtil.createCookie(7200, "jwt", result.jwt());
        Cookie refreshCookie = CookieUtil.createCookie(240000, "refresh", result.refresh());

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new ApiResponse("success", "You have successfully logged in"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@Nonnull HttpServletRequest request, HttpServletResponse response) {
       String refreshToken = CookieUtil.getToken(request, "refresh");
        System.out.println("current access token: " + CookieUtil.getToken(request, "jwt"));
       String token = refreshUseCase.refresh(refreshToken);

        System.out.println("new access token: " + token);
       Cookie jwt = CookieUtil.createCookie(7200, "jwt", token);
       response.addCookie(jwt);

       return ResponseEntity.ok().build();
    }
}