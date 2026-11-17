import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
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

  canSolicitar = computed(() => {
    const emailControl = this.loginForm.get('email');
    return !this.isLoading() && !!emailControl?.valid;
  });

  canVerificar = computed(() => {
    const emailControl = this.loginForm.get('email');
    const codigoControl = this.loginForm.get('codigo');
    return !this.isLoading() && !!emailControl?.valid && !!codigoControl?.value && codigoControl.value.length === 6;
  });

  onSubmit(): void {
    if (this.paso() === 1) {
      this.solicitarCodigo();
    } else {
      this.verificarCodigo();
    }
  }

  solicitarCodigo(): void {
    const email = this.loginForm.get('email')?.value?.trim();
    if (!email) return;

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
          this.mensaje.set(err.error?.mensaje || 'Error al solicitar el código. Verifica el correo ingresado.');
        }
      });
  }

  verificarCodigo(): void {
    const email = this.loginForm.get('email')?.value?.trim();
    const codigo = this.loginForm.get('codigo')?.value?.trim();
    if (!email || !codigo) return;

    this.isLoading.set(true);
    this.mensaje.set('');

    this.authService.validarOtp({ email, codigo })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.authService.guardarSesion(res);
          this.isLoading.set(false);
          this.esError.set(false);
          this.mensaje.set('¡Sesión iniciada correctamente! Redirigiendo...');
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 1000);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.esError.set(true);
          this.mensaje.set(err.error?.mensaje || 'Código OTP inválido o expirado. Intenta de nuevo.');
        }
      });
  }

  irAPaso(paso: 1 | 2): void {
    this.paso.set(paso);
    this.mensaje.set('');
    this.esError.set(false);
  }
}
