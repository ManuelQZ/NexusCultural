import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuariosService } from '../../servicios/usuarios';
import { CatalogoService } from '../../servicios/catalogo';
import { ArticuloService } from '../../servicios/articulo';
import { PedidoService } from '../../servicios/pedido';
import { NotificacionesService } from '../../servicios/notificaciones';
import { AuthService } from '../../servicios/auth';
import {
  AdministradorResponse,
  ArticuloDetalleResponse,
  DevolucionDetalleResponse,
  CrearAdministradorRequest
} from '../../modelos/nexus';

type PestanaAdmin = 'ADMINS' | 'MODERACION' | 'DEVOLUCIONES';

@Component({
  selector: 'app-admin-panel',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel implements OnInit {
  usuariosService = inject(UsuariosService);
  catalogoService = inject(CatalogoService);
  articuloService = inject(ArticuloService);
  pedidoService = inject(PedidoService);
  notificaciones = inject(NotificacionesService);
  authService = inject(AuthService);

  pestana = signal<PestanaAdmin>('ADMINS');
  cargando = signal(false);

  administradores = signal<AdministradorResponse[]>([]);
  todasLasObras = signal<ArticuloDetalleResponse[]>([]);
  devoluciones = signal<DevolucionDetalleResponse[]>([]);

  // Formulario interno para registrar nuevo administrador
  nuevoAdmin: CrearAdministradorRequest = {
    nombreCompleto: '',
    email: '',
    nivelAcceso: 'SUPER_ADMIN'
  };

  ngOnInit(): void {
    this.cargarAdministradores();
    this.cargarObras();
    this.cargarDevoluciones();
  }

  cambiarPestana(p: PestanaAdmin): void {
    this.pestana.set(p);
  }

  cargarAdministradores(): void {
    this.cargando.set(true);
    this.usuariosService.listarAdmins().subscribe({
      next: (admins) => {
        this.administradores.set(admins);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }

  cargarObras(): void {
    this.catalogoService.listarTodos().subscribe({
      next: (obras) => this.todasLasObras.set(obras),
      error: () => {
        this.catalogoService.listarCatalogo().subscribe({
          next: (obras) => this.todasLasObras.set(obras),
          error: () => this.todasLasObras.set([])
        });
      }
    });
  }

  cargarDevoluciones(): void {
    this.pedidoService.listarDevoluciones().subscribe({
      next: (devs) => this.devoluciones.set(devs),
      error: () => this.devoluciones.set([])
    });
  }

  registrarNuevoAdmin(): void {
    if (!this.nuevoAdmin.nombreCompleto || !this.nuevoAdmin.email) {
      this.notificaciones.error('Por favor completa el nombre y el correo institucional.');
      return;
    }

    this.usuariosService.registrarAdmin(this.nuevoAdmin).subscribe({
      next: (admin) => {
        this.notificaciones.exito(`Administrador ${admin.nombreCompleto} registrado exitosamente.`);
        this.nuevoAdmin = { nombreCompleto: '', email: '', nivelAcceso: 'SUPER_ADMIN' };
        this.cargarAdministradores();
      },
      error: (err) => this.notificaciones.error(err.error?.mensaje || 'Error al registrar administrador.')
    });
  }

  eliminarAdmin(id: string): void {
    if (!confirm('¿Estás seguro de revocar el acceso a este administrador?')) return;

    this.usuariosService.eliminarAdmin(id).subscribe({
      next: () => {
        this.notificaciones.exito('Administrador eliminado correctamente.');
        this.cargarAdministradores();
      },
      error: (err) => this.notificaciones.error(err.error?.mensaje || 'Error al eliminar administrador.')
    });
  }

  inactivarObra(id: string): void {
    this.articuloService.inactivar(id).subscribe({
      next: () => {
        this.notificaciones.info('Obra inactivada de la vista pública.');
        this.cargarObras();
      },
      error: (err) => this.notificaciones.error(err.error?.mensaje || 'Error al inactivar obra.')
    });
  }

  reactivarObra(id: string): void {
    this.articuloService.reactivar(id).subscribe({
      next: () => {
        this.notificaciones.exito('Obra reactivada con éxito.');
        this.cargarObras();
      },
      error: (err) => this.notificaciones.error(err.error?.mensaje || 'Error al reactivar obra.')
    });
  }

  resolverDevolucion(pedidoId: string, aprobar: boolean): void {
    this.pedidoService.resolverDevolucion(pedidoId, aprobar).subscribe({
      next: () => {
        this.notificaciones.exito(`Devolución ${aprobar ? 'APROBADA' : 'RECHAZADA'} exitosamente.`);
        this.cargarDevoluciones();
      },
      error: (err) => this.notificaciones.error(err.error?.mensaje || 'Error al resolver devolución.')
    });
  }
}

