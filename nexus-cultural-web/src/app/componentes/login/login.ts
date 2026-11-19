import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private fb = inject(FormBuilder);

  // Paso 1: Ingreso de correo para solicitar OTP
  // Paso 2: Ingreso de código OTP de 6 dígitos
  paso = signal<1 | 2>(1);
  isLoading = signal(false);
  mensaje = signal('');
  esError = signal(false);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    codigo: ['', [Validators.pattern('^[0-9]{6}$')]]
  });

  canSolicitar(): boolean {
    return !this.isLoading();
  }

  canVerificar(): boolean {
    return !this.isLoading();
  }

  onSubmit(): void {
    if (this.paso() === 1) {
      this.solicitarCodigo();
    } else {
      this.verificarCodigo();
    }
  }

  solicitarCodigo(): void {
    const emailControl = this.loginForm.get('email');
    emailControl?.markAsTouched();

    const email = emailControl?.value?.trim();
    if (!email || emailControl?.invalid) {
      this.esError.set(true);
      this.mensaje.set('Por favor ingresa un correo electrónico válido.');
      return;
    }

    this.isLoading.set(true);
    this.mensaje.set('');

    this.authService.solicitarOtp({ email })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.isLoading.set(false);
          this.esError.set(false);
          this.mensaje.set(res.mensaje || 'Se ha enviado un código de 6 dígitos a tu correo.');
          this.paso.set(2);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.esError.set(true);
          console.error('Error al solicitar código:', err);
          if (err.status === 0) {
            this.mensaje.set('No se pudo conectar con la plataforma cultural. Por favor verifica tu conexión a internet.');
          } else {
            this.mensaje.set(err.error?.mensaje || err.error?.message || 'No fue posible enviar el código. Verifica el correo ingresado.');
          }
        }
      });
  }

  verificarCodigo(): void {
    const emailControl = this.loginForm.get('email');
    const codigoControl = this.loginForm.get('codigo');
    codigoControl?.markAsTouched();

    const email = emailControl?.value?.trim();
    const codigo = codigoControl?.value?.trim();

    if (!codigo || codigo.length !== 6) {
      this.esError.set(true);
      this.mensaje.set('Por favor ingresa el código numérico de 6 dígitos.');
      return;
    }

    if (!email) {
      this.paso.set(1);
      return;
    }

    this.isLoading.set(true);
    this.mensaje.set('');

    this.authService.validarOtp({ email, codigo })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.authService.guardarSesion(res, email);
          this.isLoading.set(false);
          this.esError.set(false);
          this.mensaje.set('¡Sesión iniciada correctamente! Redirigiendo a tu panel...');

          // REDIRECCIÓN SEGÚN ROL REAL
          setTimeout(() => {
            const destino = this.authService.obtenerRutaPorRol();
            this.router.navigate([destino]);
          }, 800);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.esError.set(true);
          console.error('Error al verificar código:', err);
          if (err.status === 0) {
            this.mensaje.set('No se pudo conectar con la plataforma cultural. Intenta nuevamente.');
          } else {
            this.mensaje.set(err.error?.mensaje || err.error?.message || 'Código de acceso temporal incorrecto o expirado.');
          }
        }
      });
  }

  irAPaso(paso: 1 | 2): void {
    this.paso.set(paso);
    this.mensaje.set('');
    this.esError.set(false);
  }
}
