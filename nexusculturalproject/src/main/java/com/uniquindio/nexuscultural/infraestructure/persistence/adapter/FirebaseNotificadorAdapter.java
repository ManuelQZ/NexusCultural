package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import org.springframework.stereotype.Component;

@Component
public class FirebaseNotificadorAdapter {

    public void enviarCorreoOtp(String email, String codigo) {
        // Simulación del envío de correo electrónico a través de Firebase/servicio de notificaciones
        System.out.println("📩 [NOTIFICADOR] Código OTP enviado a " + email + ": " + codigo);
    }
}
