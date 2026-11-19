import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { PedidoResponse, DevolucionDetalleResponse } from '../modelos/nexus';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/pedidos`;

  crearPedido(compradorId: string, articuloId: string, cantidad: number) {
    return this.http.post<PedidoResponse>(this.url, {
      compradorId,
      articuloId,
      cantidad
    });
  }

  listarPorComprador(compradorId: string) {
    return this.http.get<PedidoResponse[]>(`${this.url}/comprador/${compradorId}`);
  }

  listarPorArtesano(artesanoId: string) {
    return this.http.get<PedidoResponse[]>(`${this.url}/artesano/${artesanoId}`);
  }

  procesarPago(request: { idPedido: string; idComprador?: string; monto: { monto: number; moneda: string }; metodoPago: string; referenciaTransaccion?: string }) {
    return this.http.post<PedidoResponse>(`${this.url}/pago`, request);
  }

  confirmarPagoWompi(request: {
    idPedido: string;
    transaccionId?: string;
    referencia?: string;
    metodoPago?: string;
    estado?: string;
    numeroTarjeta?: string;
    cvc?: string;
    expMonth?: string;
    expYear?: string;
    titularTarjeta?: string;
    cuotas?: number;
    emailComprador?: string;
  }) {
    return this.http.post<PedidoResponse>(`${environment.apiUrl}/pagos/wompi/confirmar`, request);
  }

  crearSesionWompi(pedidoId: string, redirectUrl?: string) {
    let params = new HttpParams().set('pedidoId', pedidoId);
    if (redirectUrl) params = params.set('redirectUrl', redirectUrl);
    return this.http.post<any>(`${environment.apiUrl}/pagos/wompi/checkout-session`, {}, { params });
  }

  listarDevoluciones() {
    return this.http.get<DevolucionDetalleResponse[]>(`${this.url}/devoluciones`);
  }

  resolverDevolucion(pedidoId: string, aprobar: boolean) {
    const params = new HttpParams().set('aprobar', aprobar.toString());
    return this.http.patch<PedidoResponse>(`${this.url}/${pedidoId}/devolucion/resolver`, {}, { params });
  }

  solicitarDevolucion(pedidoId: string, compradorId: string, motivo: string, descripcion: string) {
    return this.http.post<any>(`${this.url}/devolucion`, {
      pedidoId,
      compradorId,
      motivo,
      descripcion
    });
  }
}

