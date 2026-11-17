import { Injectable, signal } from '@angular/core';

export interface Aviso {
  id: number;
  tipo: 'exito' | 'error' | 'info';
  texto: string;
}

@Injectable({ providedIn: 'root' })
export class NotificacionesService {
  private siguienteId = 0;
  avisos = signal<Aviso[]>([]);

  exito(texto: string): void {
    this.mostrar('exito', texto);
  }
  error(texto: string): void {
    this.mostrar('error', texto);
  }
  info(texto: string): void {
    this.mostrar('info', texto);
  }

  quitar(id: number): void {
    this.avisos.update(lista => lista.filter(aviso => aviso.id !== id));
  }

  private mostrar(tipo: Aviso['tipo'], texto: string): void {
    const id = ++this.siguienteId;
    this.avisos.update(lista => [...lista, { id, tipo, texto }]);
    setTimeout(() => this.quitar(id), 4000);
  }
}

