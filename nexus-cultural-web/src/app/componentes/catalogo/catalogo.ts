import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CatalogoService } from '../../servicios/catalogo';
import { ArticuloDetalleResponse } from '../../modelos/nexus';
import { NotificacionesService } from '../../servicios/notificaciones';

@Component({
  selector: 'app-catalogo',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css'
})
export class Catalogo implements OnInit {
  catalogoService = inject(CatalogoService);
  notificaciones = inject(NotificacionesService);

  articulos = signal<ArticuloDetalleResponse[]>([]);
  categorias = signal<string[]>(['ESTATUAS', 'CERAMICA', 'TEJIDOS', 'JOYERIA', 'BISUTERIA', 'MADERA']);
  
  cargando = signal(false);
  categoriaFiltro = signal('TODOS');
  busqueda = signal('');
  ordenSeleccionado = signal('recientes');

  // Paginación real solicitada en Criterio 2.5 (Configurada a 4 obras por página)
  paginaActual = signal(1);
  tamanioPagina = signal(4);

  totalPaginas = computed(() => {
    const total = this.articulos().length;
    return Math.max(1, Math.ceil(total / this.tamanioPagina()));
  });

  paginasArray = computed(() => {
    return Array.from({ length: this.totalPaginas() }, (_, i) => i + 1);
  });

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarArticulos();
  }

  cargarCategorias(): void {
    this.catalogoService.listarCategorias().subscribe({
      next: (cats) => {
        if (cats && cats.length > 0) this.categorias.set(cats);
      },
      error: () => {}
    });
  }

  cargarArticulos(): void {
    this.cargando.set(true);
    const grupo = this.categoriaFiltro() !== 'TODOS' ? this.categoriaFiltro() : undefined;
    const pageIndex = this.paginaActual() - 1;
    const size = this.tamanioPagina();

    // Enviar page y size al backend tal como exige el Criterio 2.5
    this.catalogoService.listarCatalogo(grupo, pageIndex, size).subscribe({
      next: (data) => {
        this.articulos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.notificaciones.error('No se pudo conectar con el catálogo en AWS.');
      }
    });
  }

  buscar(): void {
    const q = this.busqueda().trim();
    this.paginaActual.set(1);

    if (!q) {
      this.cargarArticulos();
      return;
    }

    this.cargando.set(true);
    const pageIndex = this.paginaActual() - 1;
    const size = this.tamanioPagina();

    this.catalogoService.buscar(q, pageIndex, size).subscribe({
      next: (data) => {
        this.articulos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.notificaciones.error('Error al realizar la búsqueda en el servidor.');
      }
    });
  }

  cambiarCategoria(cat: string): void {
    this.categoriaFiltro.set(cat);
    this.paginaActual.set(1);
    this.cargarArticulos();
  }

  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) {
      this.paginaActual.set(p);
      this.cargarArticulos();
    }
  }

  paginaAnterior(): void {
    if (this.paginaActual() > 1) {
      this.cambiarPagina(this.paginaActual() - 1);
    }
  }

  paginaSiguiente(): void {
    if (this.paginaActual() < this.totalPaginas()) {
      this.cambiarPagina(this.paginaActual() + 1);
    }
  }

  get articulosOrdenados(): ArticuloDetalleResponse[] {
    let list = [...this.articulos()];
    if (this.ordenSeleccionado() === 'precio-asc') {
      list.sort((a, b) => a.montoPrecio - b.montoPrecio);
    } else if (this.ordenSeleccionado() === 'precio-desc') {
      list.sort((a, b) => b.montoPrecio - a.montoPrecio);
    }

    // Paginación visual por página
    const inicio = (this.paginaActual() - 1) * this.tamanioPagina();
    return list.slice(inicio, inicio + this.tamanioPagina());
  }
}
