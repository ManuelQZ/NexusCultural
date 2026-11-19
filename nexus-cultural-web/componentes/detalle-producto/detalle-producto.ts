import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CatalogoService } from '../../servicios/catalogo';
import { CarritoService } from '../../servicios/carrito';
import { AccesibilidadService } from '../../servicios/accesibilidad';
import { NotificacionesService } from '../../servicios/notificaciones';
import { ArticuloDetalleResponse } from '../../modelos/nexus';

@Component({
  selector: 'app-detalle-producto',
  imports: [CommonModule, RouterLink],
  templateUrl: './detalle-producto.html',
  styleUrl: './detalle-producto.css'
})
export class DetalleProducto implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private catalogoService = inject(CatalogoService);
  private carritoService = inject(CarritoService);
  accesibilidad = inject(AccesibilidadService);
  notificaciones = inject(NotificacionesService);

  cargando = signal(true);
  cantidad = signal(1);
  imagenActiva = signal('assets/images/logo.png');
  galeria = signal<string[]>([]);

  obra = signal<ArticuloDetalleResponse | null>(null);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.cargarPieza(id);
      } else {
        this.cargarPrimeraPieza();
      }
    });
  }

  cargarPieza(id: string): void {
    this.cargando.set(true);
    this.catalogoService.obtenerArticulo(id).subscribe({
      next: (art) => {
        this.asignarObra(art);
      },
      error: () => {
        this.catalogoService.listarCatalogo().subscribe({
          next: (todos) => {
            const encontrado = todos.find(p => p.id === id) || todos[0];
            if (encontrado) {
              this.asignarObra(encontrado);
            } else {
              this.cargando.set(false);
            }
          },
          error: () => this.cargando.set(false)
        });
      }
    });
  }

  cargarPrimeraPieza(): void {
    this.cargando.set(true);
    this.catalogoService.listarCatalogo().subscribe({
      next: (todos) => {
        if (todos.length > 0) {
          this.asignarObra(todos[0]);
        } else {
          this.cargando.set(false);
        }
      },
      error: () => this.cargando.set(false)
    });
  }

  private asignarObra(art: ArticuloDetalleResponse): void {
    this.obra.set(art);
    const imgs = art.imagenes && art.imagenes.length > 0
      ? art.imagenes
      : ['assets/images/logo.png'];
    this.galeria.set(imgs);
    this.imagenActiva.set(imgs[0]);
    this.cantidad.set(1);
    this.cargando.set(false);
  }

  incrementar(): void {
    const art = this.obra();
    if (art && this.cantidad() < art.stock) {
      this.cantidad.update(c => c + 1);
    }
  }

  decrementar(): void {
    if (this.cantidad() > 1) {
      this.cantidad.update(c => c - 1);
    }
  }

  cambiarImagen(url: string): void {
    this.imagenActiva.set(url);
  }

  agregarAlCarrito(): void {
    const art = this.obra();
    if (!art) return;
    this.carritoService.agregar(art, this.cantidad());
    this.notificaciones.exito(`¡${this.cantidad()}x "${art.titulo}" agregado al carrito de compras!`);
  }

  comprarAhora(): void {
    const art = this.obra();
    if (!art) return;
    this.carritoService.agregar(art, this.cantidad());
    this.router.navigate(['/checkout']);
  }

  leerHistoria(): void {
    const art = this.obra();
    if (!art) return;
    this.accesibilidad.alternarLectura(
      `Obra artesanal: ${art.titulo}. Categoría: ${art.grupo}. ${art.descripcion}. Precio: ${art.montoPrecio} pesos colombianos.`
    );
  }
}
