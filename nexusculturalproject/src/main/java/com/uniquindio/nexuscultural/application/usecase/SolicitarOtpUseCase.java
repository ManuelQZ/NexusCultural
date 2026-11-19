package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.adapter.FirebaseNotificadorAdapter;
import org.springframework.stereotype.Service;

@Service
public class SolicitarOtpUseCase {

    private final CodigoOtpRepository otpRepository;
    private final FirebaseNotificadorAdapter firebaseNotificadorAdapter;

    public SolicitarOtpUseCase(CodigoOtpRepository otpRepository,
                               FirebaseNotificadorAdapter firebaseNotificadorAdapter) {
        this.otpRepository = otpRepository;
        this.firebaseNotificadorAdapter = firebaseNotificadorAdapter;
    }

    public MensajeRespuestaResponse ejecutar(SolicitarOtpRequest request) {
        // Reiniciar contador de intentos previos
        ValidarOtpUseCase.reiniciarIntentos(request.email());

        // Se asigna un valor entero que representa los minutos de validez (5 minutos)
        CodigoOtp nuevoOtp = CodigoOtp.generar(5);

        // Se guarda asociado al email
        otpRepository.guardar(request.email(), nuevoOtp);

        // Se notifica al correo
        try {
            firebaseNotificadorAdapter.enviarCorreoOtp(request.email(), nuevoOtp.codigo());
        } catch (Exception e) {
            System.err.println("Error enviando correo OTP: " + e.getMessage());
        }

        System.out.println("📩 [OTP GENERADO]: El código para " + request.email() + " es: " + nuevoOtp.codigo());

        return new MensajeRespuestaResponse("Código OTP enviado con éxito al correo.", true);
    }
}
