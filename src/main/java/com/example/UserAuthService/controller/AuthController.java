package com.example.UserAuthService.controller;


import com.example.UserAuthService.dto.AuthRequest;
import com.example.UserAuthService.dto.LoginResponse;
import com.example.UserAuthService.dto.UserDto;
import com.example.UserAuthService.model.Role;
import com.example.UserAuthService.model.User;
import com.example.UserAuthService.service.AuthService;
import com.example.UserAuthService.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signupByEmail(@RequestBody AuthRequest authRequest) {
        User user = authService.signup(authRequest.getEmail(), authRequest.getPassword());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setId(user.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest authRequest) {
        String token = authService.login(authRequest.getEmail(), authRequest.getPassword());
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDto> validateToken(
            @RequestHeader("Authorization") String header)
    {
            if(!header.startsWith("Bearer "))
            {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            try {
                Claims claims = jwtService.parseToken(header.substring("Bearer ".length()));
                UserDto userDto = new UserDto();
                userDto.setEmail(claims.getSubject());
                userDto.setRole(Role.valueOf((String) claims.get("role")));
                return ResponseEntity.ok(userDto);
            }catch (JwtException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
    }

    @GetMapping("/hello")
    public String hello()
    {
        return "Hello";
    }
}
