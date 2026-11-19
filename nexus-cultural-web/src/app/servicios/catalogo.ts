import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ArticuloDetalleResponse } from '../modelos/nexus';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/catalogo`;
  private urlArticulos = `${environment.apiUrl}/articulos`;

  listarCatalogo(grupo?: string, page: number = 0, size: number = 4) {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (grupo && grupo !== 'TODOS') {
      params = params.set('grupo', grupo);
    }
    return this.http.get<ArticuloDetalleResponse[]>(this.url, { params });
  }

  listarTodos() {
    return this.http.get<ArticuloDetalleResponse[]>(`${this.url}/todos`);
  }

  buscar(query: string, page: number = 0, size: number = 4) {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ArticuloDetalleResponse[]>(`${this.url}/buscar`, { params });
  }

  listarCategorias() {
    return this.http.get<string[]>(`${this.url}/categorias`);
  }

  obtenerArticulo(id: string) {
    return this.http.get<ArticuloDetalleResponse>(`${this.urlArticulos}/${id}`);
  }

  listarPorArtesano(artesanoId: string) {
    return this.http.get<ArticuloDetalleResponse[]>(`${this.url}/artesano/${artesanoId}`);
  }
}

