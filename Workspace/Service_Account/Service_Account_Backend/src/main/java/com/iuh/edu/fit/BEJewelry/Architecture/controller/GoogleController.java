package com.iuh.edu.fit.BEJewelry.Architecture.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping("/oauth2")
public class GoogleController {

    @PostMapping("/auth/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            System.out.println("Received token: " + token);

            // Giải mã Google ID Token
            DecodedJWT decodedJWT = JWT.decode(token);
            String payloadJson = new String(Base64.getDecoder().decode(decodedJWT.getPayload()));

            // Kiểm tra tính hợp lệ
            String audience = decodedJWT.getAudience().get(0);
            if (!"909859862292-ssitcqus7ah3pg6kef552vv71ufmkbk1.apps.googleusercontent.com".equals(audience)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token Audience");
            }

            return ResponseEntity.ok(Map.of("payload", payloadJson));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
        }
    }

    @GetMapping("/auth/success")
    public ResponseEntity<Map<String, Object>> success(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return ResponseEntity.ok(oAuth2User.getAttributes());
    }

    @GetMapping("/auth/failure")
    public ResponseEntity<Map<String, String>> failure() {
        return ResponseEntity.badRequest().body(Map.of("error", "Google login failed!"));
    }
}
