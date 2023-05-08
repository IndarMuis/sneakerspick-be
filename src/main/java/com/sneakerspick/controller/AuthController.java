package com.sneakerspick.controller;

import com.sneakerspick.Model.request.LoginRequest;
import com.sneakerspick.Model.request.RegisterRequest;
import com.sneakerspick.Model.response.APIResponse;
import com.sneakerspick.Model.response.AuthResponse;
import com.sneakerspick.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
@Slf4j
public class AuthController {

	private AppUserService appUserService;

	@PostMapping("/v1/auth/register")
	public ResponseEntity<APIResponse<?>> register(@RequestBody RegisterRequest request) {
		AuthResponse register = appUserService.register(request);

		log.info("AUTH RESPONSE : {}", register);
		APIResponse<?> response = APIResponse.builder()
				.message("Register Success")
				.code(HttpStatus.CREATED.value())
				.result(register)
				.build();

		return ResponseEntity.created(URI.create("/v1/auth/register")).body(response);
	}

	@PostMapping("/v1/auth/login")
	public ResponseEntity<APIResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
		return null;
	}

}
