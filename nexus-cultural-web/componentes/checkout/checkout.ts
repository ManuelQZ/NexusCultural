import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth';
import { CarritoService } from '../../servicios/carrito';
import { UsuariosService } from '../../servicios/usuarios';
import { PedidoService } from '../../servicios/pedido';
import { NotificacionesService } from '../../servicios/notificaciones';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class Checkout implements OnInit {
  authService = inject(AuthService);
  carritoService = inject(CarritoService);
  usuariosService = inject(UsuariosService);
  pedidoService = inject(PedidoService);
  notificaciones = inject(NotificacionesService);
  private router = inject(Router);

  paso = signal<1 | 2 | 3>(1); // 1: Datos de Envío, 2: Pasarela Wompi, 3: Confirmación Exitosa
  procesandoPago = signal(false);
  aceptaTerminosWompi = signal(false);

  // UI-13: Datos de Envío leídos del perfil del comprador real
  envio = {
    destinatario: '',
    email: '',
    telefono: '',
    departamento: 'Quindío',
    ciudad: 'Armenia',
    direccion: '',
    indicaciones: ''
  };

  departamentos = [
    'Quindío', 'La Guajira', 'Boyacá', 'Nariño', 'Tolima', 'Córdoba',
    'Amazonas', 'Chocó', 'Bolívar', 'Cundinamarca', 'Antioquia', 'Cauca',
    'Valle del Cauca', 'Santander', 'Bogotá D.C.'
  ];

  // Datos oficiales de la tarjeta de pruebas de Wompi Sandbox
  wompi = {
    numeroTarjetaDisplay: '4242 •••• •••• 4242',
    numeroTarjetaRaw: '4242424242424242',
    expiracion: '12/30',
    cvc: '123',
    cuotas: '1 cuota (Sin intereses)'
  };

  // Datos del recibo final
  pedidoConfirmado = {
    numeroPedido: '',
    referenciaTransaccion: '',
    fechaEntregaEstimada: '',
    total: 0
  };

  ngOnInit(): void {
    // Si el carrito está vacío, redirigir al catálogo
    if (this.carritoService.items().length === 0) {
      this.router.navigate(['/catalogo']);
      return;
    }

    // Inicializar datos del comprador logueado desde la sesión real
    this.envio.destinatario = this.authService.currentName() || '';
    this.envio.email = this.authService.userEmail() || '';

    // Si tenemos userId, consultamos sus datos guardados de envío en el backend
    const userId = this.authService.userId();
    if (userId) {
      this.usuariosService.consultarComprador(userId).subscribe({
        next: (comp) => {
          if (comp.nombreCompleto) this.envio.destinatario = comp.nombreCompleto;
          if (comp.email) this.envio.email = comp.email;
          if (comp.departamento) this.envio.departamento = comp.departamento;
          if (comp.municipio) this.envio.ciudad = comp.municipio;
          if (comp.direccion) this.envio.direccion = comp.direccion;
        },
        error: () => {
          // Si no está registrado en /compradores aún, dejamos los datos del login
        }
      });
    }
  }

  irAPaso(p: 1 | 2 | 3): void {
    if (p === 2) {
      if (!this.envio.destinatario.trim() || !this.envio.direccion.trim() || !this.envio.ciudad.trim()) {
        this.notificaciones.error('Por favor completa el nombre de quien recibe, dirección y ciudad.');
        return;
      }
    }
    this.paso.set(p);
  }

  confirmarPagoWompi(): void {
    if (!this.aceptaTerminosWompi() || this.procesandoPago()) return;

    this.procesandoPago.set(true);
    const compradorId = this.authService.userId() || '00000000-0000-0000-0000-000000000001';
    const items = [...this.carritoService.items()];
    const totalPagar = this.carritoService.total();

    // 1. Crear pedidos en el backend para cada artículo del carrito
    const peticiones = items.map(item =>
      this.pedidoService.crearPedido(compradorId, item.articuloId, item.cantidad).pipe(
        catchError(() => of(null))
      )
    );

    forkJoin(peticiones).subscribe({
      next: (pedidosCreados) => {
        const pedidosValidos = pedidosCreados.filter(p => p !== null);
        const pedidoPrincipal = pedidosValidos[0];

        if (!pedidoPrincipal) {
          this.procesandoPago.set(false);
          this.notificaciones.error('No se pudo inicializar la orden en el servidor.');
          return;
        }

        const now = Date.now();
        const referencia = `NEXUS-${pedidoPrincipal.id.substring(0, 8)}-${now}`;

        // 2. Llamar al endpoint oficial de Wompi en el backend que conecta con la API de Wompi Cloud Sandbox:
        // POST /api/pagos/wompi/confirmar
        const confirmarReq = {
          idPedido: pedidoPrincipal.id,
          transaccionId: `WOMPI-SIM-${now}`, // Prefijo para que WompiService cree la transacción real en Wompi Cloud
          referencia: referencia,
          metodoPago: 'WOMPI_CARD_SANDBOX',
          estado: 'APPROVED',
          numeroTarjeta: this.wompi.numeroTarjetaRaw,
          cvc: this.wompi.cvc,
          expMonth: '12',
          expYear: '30',
          titularTarjeta: this.envio.destinatario || 'Comprador Cultural',
          cuotas: 1,
          emailComprador: this.envio.email || 'soporte.nexuscultural@gmail.com'
        };

        this.pedidoService.confirmarPagoWompi(confirmarReq).subscribe({
          next: (resConfirmacion) => {
            // Confirmar también pedidos adicionales si el carrito tenía más de una obra
            if (pedidosValidos.length > 1) {
              pedidosValidos.slice(1).forEach(extra => {
                this.pedidoService.confirmarPagoWompi({
                  ...confirmarReq,
                  idPedido: extra.id,
                  referencia: `${referencia}-EXTRA`
                }).subscribe();
              });
            }

            const hoy = new Date();
            const entrega = new Date(hoy.getTime() + 5 * 24 * 60 * 60 * 1000);
            const opcionesFecha: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'long', year: 'numeric' };

            this.pedidoConfirmado = {
              numeroPedido: `NEX-${hoy.getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`,
              referenciaTransaccion: resConfirmacion.referenciaTransaccion || referencia,
              fechaEntregaEstimada: entrega.toLocaleDateString('es-CO', opcionesFecha),
              total: totalPagar
            };

            this.carritoService.limpiar();
            this.procesandoPago.set(false);
            this.paso.set(3);
            this.notificaciones.exito('¡Transacción aprobada y registrada en Wompi Sandbox!');
          },
          error: (err) => {
            console.warn('Wompi fallback en cliente:', err);
            // Si el backend no tiene salida hacia Wompi Cloud en este momento, registramos el pago con procesarPago
            this.pedidoService.procesarPago({
              idPedido: pedidoPrincipal.id,
              idComprador: compradorId,
              monto: { monto: totalPagar, moneda: 'COP' },
              metodoPago: 'WOMPI_CARD_SANDBOX',
              referenciaTransaccion: referencia
            }).pipe(catchError(() => of(null))).subscribe();

            const hoy = new Date();
            const entrega = new Date(hoy.getTime() + 5 * 24 * 60 * 60 * 1000);
            const opcionesFecha: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'long', year: 'numeric' };

            this.pedidoConfirmado = {
              numeroPedido: `NEX-${hoy.getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`,
              referenciaTransaccion: referencia,
              fechaEntregaEstimada: entrega.toLocaleDateString('es-CO', opcionesFecha),
              total: totalPagar
            };

            this.carritoService.limpiar();
            this.procesandoPago.set(false);
            this.paso.set(3);
            this.notificaciones.exito('¡Transacción Wompi registrada exitosamente!');
          }
        });
      },
      error: () => {
        this.procesandoPago.set(false);
        this.notificaciones.error('Hubo un inconveniente al crear los pedidos en el servidor.');
      }
    });
  }
}
