import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CarritoService, ItemCarrito } from '../../servicios/carrito';

@Component({
  selector: 'app-carrito',
  imports: [CommonModule, RouterLink],
  templateUrl: './carrito.html',
  styleUrl: './carrito.css'
})
export class Carrito {
  carritoService = inject(CarritoService);

  readonly items = this.carritoService.items;
  readonly subtotal = this.carritoService.subtotal;
  readonly total = this.carritoService.total;
  readonly costoEnvio = this.carritoService.costoEnvio;

  incrementar(item: ItemCarrito): void {
    this.carritoService.incrementar(item.articuloId);
  }

  decrementar(item: ItemCarrito): void {
    this.carritoService.decrementar(item.articuloId);
  }

  eliminar(id: string): void {
    this.carritoService.eliminar(id);
  }
}
