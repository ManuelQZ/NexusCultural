import { Injectable, signal, computed } from '@angular/core';
import { ArticuloDetalleResponse } from '../modelos/nexus';

export interface ItemCarrito {
  id: string;
  articuloId: string;
  titulo: string;
  descripcion?: string;
  grupo?: string;
  artesanoId?: string;
  artesano?: string;
  region?: string;
  precio: number;
  moneda: string;
  cantidad: number;
  imagen: string;
  stock: number;
}

const STORAGE_KEY = 'nexus_cultural_carrito';

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private itemsSignal = signal<ItemCarrito[]>(this.cargarItemsGuardados());

  readonly items = this.itemsSignal.asReadonly();

  readonly costoEnvio = 15000;

  readonly cantidadTotal = computed(() => {
    return this.itemsSignal().reduce((acc, item) => acc + item.cantidad, 0);
  });

  readonly subtotal = computed(() => {
    return this.itemsSignal().reduce((acc, item) => acc + (item.precio * item.cantidad), 0);
  });

  readonly total = computed(() => {
    const sub = this.subtotal();
    return sub > 0 ? sub + this.costoEnvio : 0;
  });

  readonly pagoArtesanosDirecto = computed(() => {
    return this.subtotal() * 0.90; // 90% pago directo y justo a los artesanos
  });

  agregar(articulo: ArticuloDetalleResponse, cantidad: number = 1): void {
    const list = [...this.itemsSignal()];
    const index = list.findIndex(i => i.articuloId === articulo.id);

    const precioMonto = articulo.montoPrecio ?? 0;
    const moneda = articulo.moneda ?? 'COP';
    const imagen = articulo.imagenes && articulo.imagenes.length > 0
      ? articulo.imagenes[0]
      : 'assets/images/logo.png';

    const region = articulo.municipio
      ? `${articulo.municipio}, ${articulo.departamento || 'Colombia'}`
      : (articulo.departamento || 'Colombia');

    if (index >= 0) {
      const nuevaCantidad = list[index].cantidad + cantidad;
      const cantFinal = Math.min(nuevaCantidad, articulo.stock || 99);
      list[index] = { ...list[index], cantidad: cantFinal };
    } else {
      list.push({
        id: articulo.id,
        articuloId: articulo.id,
        titulo: articulo.titulo,
        descripcion: articulo.descripcion,
        grupo: articulo.grupo,
        artesanoId: articulo.artesanoId,
        artesano: 'Maestro Artesano Tradicional',
        region: region,
        precio: precioMonto,
        moneda: moneda,
        cantidad: Math.min(cantidad, articulo.stock || 1),
        imagen: imagen,
        stock: articulo.stock || 1
      });
    }

    this.actualizar(list);
  }

  incrementar(articuloId: string): void {
    const list = this.itemsSignal().map(item => {
      if (item.articuloId === articuloId && item.cantidad < item.stock) {
        return { ...item, cantidad: item.cantidad + 1 };
      }
      return item;
    });
    this.actualizar(list);
  }

  decrementar(articuloId: string): void {
    const list = this.itemsSignal().map(item => {
      if (item.articuloId === articuloId && item.cantidad > 1) {
        return { ...item, cantidad: item.cantidad - 1 };
      }
      return item;
    });
    this.actualizar(list);
  }

  eliminar(articuloId: string): void {
    const list = this.itemsSignal().filter(i => i.articuloId !== articuloId);
    this.actualizar(list);
  }

  limpiar(): void {
    this.actualizar([]);
  }

  private actualizar(list: ItemCarrito[]): void {
    this.itemsSignal.set(list);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
    } catch (_) {}
  }

  private cargarItemsGuardados(): ItemCarrito[] {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      if (data) {
        return JSON.parse(data);
      }
    } catch (_) {}
    return [];
  }
}
