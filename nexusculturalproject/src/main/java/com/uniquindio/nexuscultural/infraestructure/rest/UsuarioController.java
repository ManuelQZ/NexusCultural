package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.AutenticarUsuarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.request.ValidarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.application.usecase.SolicitarOtpUseCase;
import com.uniquindio.nexuscultural.application.usecase.ValidarOtpUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final SolicitarOtpUseCase solicitarOtpUseCase;
    private final ValidarOtpUseCase validarOtpUseCase;

    // 1. Solicitar código OTP al correo electrónico
    @PostMapping("/solicitar")
    public ResponseEntity<MensajeRespuestaResponse> solicitarCodigoOTP(@Valid @RequestBody SolicitarOtpRequest request) {
        MensajeRespuestaResponse respuesta = solicitarOtpUseCase.ejecutar(request);
        return ResponseEntity.ok(respuesta);
    }

    // 2. Verificar código OTP y retornar token JWT
    @PostMapping("/verificar")
    public ResponseEntity<AuthTokenResponse> validarCodigoOTP(@Valid @RequestBody ValidarOtpRequest request) {
        AuthTokenResponse tokenResponse = validarOtpUseCase.ejecutar(request.email(), request.codigo());
        return ResponseEntity.ok(tokenResponse);
    }

    // 3. Autenticación donde el código OTP actúa como la contraseña
    @PostMapping("/autenticar")
    public ResponseEntity<AuthTokenResponse> autenticarUsuario(@Valid @RequestBody AutenticarUsuarioRequest request) {
        AuthTokenResponse tokenResponse = validarOtpUseCase.ejecutar(request.usuario(), request.contrasena());
        return ResponseEntity.ok(tokenResponse);
    }
}