import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NotificacionesService } from '../../servicios/notificaciones';

@Component({
  selector: 'app-crear-producto',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './crear-producto.html',
  styleUrl: './crear-producto.css'
})
export class CrearProducto {
  notificaciones = inject(NotificacionesService);

  pasoActual = signal<1 | 2 | 3>(1);
  publicacionExitosa = signal(false);

  obra = {
    titulo: 'Vasija Cerámica Espiralada',
    categoria: 'CERAMICA',
    tecnica: 'Moldeado manual en espiral con bruñido en piedra',
    historia: 'Inspirada en los petroglifos precolombinos de la cordillera central, simbolizando el ciclo eterno del agua y la fertilidad.',
    materiales: 'Arcilla roja local, agua de manantial, pigmento de óxido de hierro',
    dimensiones: '35 cm x 28 cm',
    peso: '1.2 kg',
    precio: 210000,
    stock: 5,
    tiempoElaboracion: '14 días'
  };

  imagenesSubidas = signal<string[]>([
    'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?auto=format&fit=crop&w=600&q=80',
    'https://images.unsplash.com/photo-1615529328331-f8917597711f?auto=format&fit=crop&w=600&q=80'
  ]);

  irAPaso(paso: 1 | 2 | 3): void {
    this.pasoActual.set(paso);
  }

  simularSubidaImagen(): void {
    this.imagenesSubidas.update(imgs => [
      ...imgs,
      'https://images.unsplash.com/photo-1590736969955-71cc94801759?auto=format&fit=crop&w=600&q=80'
    ]);
    this.notificaciones.exito('Fotografía cargada correctamente (Almacenamiento S3)');
  }

  eliminarImagen(index: number): void {
    this.imagenesSubidas.update(imgs => imgs.filter((_, i) => i !== index));
  }

  publicarObra(): void {
    this.publicacionExitosa.set(true);
    this.notificaciones.exito('¡Obra publicada exitosamente en el catálogo de Nexus Cultural!');
  }

  reiniciar(): void {
    this.publicacionExitosa.set(false);
    this.pasoActual.set(1);
  }
}

