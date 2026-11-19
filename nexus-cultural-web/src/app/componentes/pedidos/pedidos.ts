import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth';
import { PedidoService } from '../../servicios/pedido';
import { CatalogoService } from '../../servicios/catalogo';
import { NotificacionesService } from '../../servicios/notificaciones';
import { PedidoResponse } from '../../modelos/nexus';

export interface PedidoVista {
  id: string;
  codigo: string;
  fecha: string;
  artesano: string;
  obra: string;
  total: number;
  estado: string; // 'PENDIENTE' | 'CONFIRMADO' | 'EN_CAMINO' | 'ENTREGADO' | 'DEVUELTO'
  guiaTransportadora: string;
  transportadora: string;
  imagen: string;
  referenciaWompi?: string;
  motivoDevolucion?: string | null;
  estadoDevolucion?: string | null;
}

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos implements OnInit {
  private authService = inject(AuthService);
  private pedidoService = inject(PedidoService);
  private catalogoService = inject(CatalogoService);
  private notificaciones = inject(NotificacionesService);

  cargando = signal(true);
  pedidos = signal<PedidoVista[]>([]);
  pedidoSeleccionado = signal<PedidoVista | null>(null);
  mostrarModalDevolucion = signal(false);
  devolucionEnviada = signal(false);
  enviandoDevolucion = signal(false);

  // Formulario de Devolución (UI-19)
  devolucion = {
    motivo: 'DANO_TRANSPORTE',
    descripcion: ''
  };

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial(): void {
    const compradorId = this.authService.userId();
    if (!compradorId) {
      this.cargando.set(false);
      this.pedidos.set([]);
      return;
    }

    this.cargando.set(true);
    this.pedidoService.listarPorComprador(compradorId).subscribe({
      next: (pedidosBackend) => {
        if (!pedidosBackend || pedidosBackend.length === 0) {
          this.pedidos.set([]);
          this.pedidoSeleccionado.set(null);
          this.cargando.set(false);
          return;
        }

        // Consultamos la información de las obras para mostrar imágenes y títulos reales
        this.catalogoService.listarCatalogo().subscribe({
          next: (catalogo) => {
            const catalogoMap = new Map(catalogo.map(a => [a.id, a]));
            const vistas: PedidoVista[] = pedidosBackend.map(p => {
              const art = catalogoMap.get(p.articuloId);
              const fechaObj = p.fechaCreacion ? new Date(p.fechaCreacion) : new Date();
              const fechaStr = fechaObj.toLocaleDateString('es-CO', {
                day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
              });

              const refWompi = p.referenciaTransaccion || 'WOMPI-CONFIRMADO';
              const img = (art?.imagenes && art.imagenes.length > 0)
                ? art.imagenes[0]
                : 'assets/images/logo.png';

              return {
                id: p.id,
                codigo: `NEX-${fechaObj.getFullYear()}-${p.id.substring(0, 8).toUpperCase()}`,
                fecha: fechaStr,
                artesano: art?.departamento ? `Artesano de ${art.departamento}` : 'Maestro Artesano Nexus',
                obra: art?.titulo || `Artesanía #${p.articuloId.substring(0, 6)}`,
                total: Number(p.total) || 0,
                estado: p.estado || 'CONFIRMADO',
                guiaTransportadora: `ENV-${p.id.substring(0, 8).toUpperCase()}-CO`,
                transportadora: 'Coordinadora Mercantil Artesanal',
                imagen: img,
                referenciaWompi: refWompi,
                motivoDevolucion: p.motivoDevolucion,
                estadoDevolucion: p.estadoDevolucion
              };
            });

            this.pedidos.set(vistas);
            this.pedidoSeleccionado.set(vistas[0] || null);
            this.cargando.set(false);
          },
          error: () => {
            // Mapeo básico sin catálogo
            const vistas: PedidoVista[] = pedidosBackend.map(p => ({
              id: p.id,
              codigo: `NEX-2026-${p.id.substring(0, 8).toUpperCase()}`,
              fecha: p.fechaCreacion || 'Reciente',
              artesano: 'Maestro Artesano Nexus',
              obra: `Artesanía #${p.articuloId.substring(0, 6)}`,
              total: Number(p.total) || 0,
              estado: p.estado || 'CONFIRMADO',
              guiaTransportadora: `ENV-${p.id.substring(0, 8).toUpperCase()}-CO`,
              transportadora: 'Coordinadora Mercantil',
              imagen: 'assets/images/logo.png',
              referenciaWompi: p.referenciaTransaccion || '',
              motivoDevolucion: p.motivoDevolucion,
              estadoDevolucion: p.estadoDevolucion
            }));
            this.pedidos.set(vistas);
            this.pedidoSeleccionado.set(vistas[0] || null);
            this.cargando.set(false);
          }
        });
      },
      error: () => {
        this.pedidos.set([]);
        this.pedidoSeleccionado.set(null);
        this.cargando.set(false);
      }
    });
  }

  seleccionarPedido(p: PedidoVista): void {
    this.pedidoSeleccionado.set(p);
  }

  abrirDevolucion(): void {
    this.mostrarModalDevolucion.set(true);
    this.devolucionEnviada.set(false);
    this.devolucion = { motivo: 'DANO_TRANSPORTE', descripcion: '' };
  }

  cerrarDevolucion(): void {
    this.mostrarModalDevolucion.set(false);
  }

  enviarDevolucion(): void {
    const ped = this.pedidoSeleccionado();
    const compId = this.authService.userId();
    if (!ped || !compId) return;

    if (!this.devolucion.descripcion.trim()) {
      this.notificaciones.error('Por favor describe el motivo de la devolución.');
      return;
    }

    this.enviandoDevolucion.set(true);
    this.pedidoService.solicitarDevolucion(ped.id, compId, this.devolucion.motivo, this.devolucion.descripcion).subscribe({
      next: () => {
        this.enviandoDevolucion.set(false);
        this.devolucionEnviada.set(true);
        this.notificaciones.exito('Solicitud de devolución registrada en el sistema.');
        this.cargarHistorial();
        setTimeout(() => {
          this.mostrarModalDevolucion.set(false);
        }, 1800);
      },
      error: (err) => {
        this.enviandoDevolucion.set(false);
        this.notificaciones.error(err.error?.mensaje || 'Error al radicar solicitud de devolución.');
      }
    });
  }
}
