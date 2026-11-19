import { Component, inject } from '@angular/core';
import { NotificacionesService } from '../../servicios/notificaciones';

@Component({
  selector: 'app-notificaciones',
  templateUrl: './notificaciones.html',
  styleUrl: './notificaciones.css'
})
export class Notificaciones {
  servicio = inject(NotificacionesService);
}

