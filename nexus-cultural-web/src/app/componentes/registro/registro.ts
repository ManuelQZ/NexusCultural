import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { UsuariosService } from '../../servicios/usuarios';
import { NotificacionesService } from '../../servicios/notificaciones';

type TipoRol = 'ARTESANO' | 'COMPRADOR';

@Component({
  selector: 'app-registro',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {
  private usuariosService = inject(UsuariosService);
  private notificaciones = inject(NotificacionesService);
  private router = inject(Router);

  rolSeleccionado = signal<TipoRol>('COMPRADOR');
  isLoading = signal(false);
  mensajeError = signal('');
  registroCompletado = signal(false);

  // Formulario Artesano (DTO: RegistrarArtesanoRequest)
  artesano = {
    nombreCompleto: '',
    email: '',
    anosExperiencia: 5,
    reconocimientosTexto: 'Maestro Artesano Tradicional',
    departamento: 'La Guajira',
    municipio: 'Uribia',
    direccion: 'Ranchería Central'
  };

  // Formulario Comprador (DTO: RegistrarCompradorRequest)
  comprador = {
    nombreCompleto: '',
    email: '',
    departamento: 'Quindío',
    municipio: 'Armenia',
    direccion: ''
  };

  departamentos = [
    'Quindío', 'La Guajira', 'Boyacá', 'Nariño', 'Tolima', 'Córdoba', 
    'Amazonas', 'Chocó', 'Bolívar', 'Cundinamarca', 'Antioquia', 'Cauca', 'Putumayo'
  ];

  seleccionarRol(rol: TipoRol): void {
    this.rolSeleccionado.set(rol);
    this.mensajeError.set('');
  }

  enviarRegistro(): void {
    this.isLoading.set(true);
    this.mensajeError.set('');

    if (this.rolSeleccionado() === 'COMPRADOR') {
      const req = {
        nombreCompleto: this.comprador.nombreCompleto.trim(),
        email: this.comprador.email.trim(),
        departamento: this.comprador.departamento,
        municipio: this.comprador.municipio.trim(),
        direccion: this.comprador.direccion.trim()
      };

      if (!req.nombreCompleto || !req.email || !req.direccion || !req.municipio) {
        this.isLoading.set(false);
        this.mensajeError.set('Por favor completa todos los campos requeridos.');
        return;
      }

      this.usuariosService.registrarComprador(req).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.registroCompletado.set(true);
          this.notificaciones.exito('¡Comprador registrado exitosamente!');
        },
        error: (err) => {
          this.isLoading.set(false);
          this.mensajeError.set(err.error?.mensaje || 'No se pudo completar el registro en la plataforma.');
        }
      });
    } else {
      // ARTESANO
      const reconocimientos = this.artesano.reconocimientosTexto
        ? [this.artesano.reconocimientosTexto.trim()]
        : [];

      const req = {
        nombreCompleto: this.artesano.nombreCompleto.trim(),
        email: this.artesano.email.trim(),
        anosExperiencia: Number(this.artesano.anosExperiencia) || 0,
        reconocimientos,
        departamento: this.artesano.departamento,
        municipio: this.artesano.municipio.trim(),
        direccion: this.artesano.direccion.trim()
      };

      if (!req.nombreCompleto || !req.email || !req.direccion || !req.municipio) {
        this.isLoading.set(false);
        this.mensajeError.set('Por favor completa todos los campos requeridos del artesano.');
        return;
      }

      this.usuariosService.registrarArtesano(req).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.registroCompletado.set(true);
          this.notificaciones.exito('¡Artesano registrado exitosamente!');
        },
        error: (err) => {
          this.isLoading.set(false);
          this.mensajeError.set(err.error?.mensaje || 'No se pudo completar el registro del maestro artesano.');
        }
      });
    }
  }

  reiniciar(): void {
    this.registroCompletado.set(false);
    this.mensajeError.set('');
  }
}
