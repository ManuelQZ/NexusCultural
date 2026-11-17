// Modelos espejos de los DTOs de Nexus Cultural (Backend AWS)

export interface SolicitarOtpRequest {
  email: string;
}

export interface ValidarOtpRequest {
  email: string;
  codigo: string;
}

export interface AuthTokenResponse {
  token: string;
  tipo: string;
  rol: string;
  nivelAcceso?: string | null;
  nombre?: string | null;
  expiracionEnSegundos: number;
}

export interface MensajeRespuestaResponse {
  mensaje: string;
  exitoso: boolean;
}
