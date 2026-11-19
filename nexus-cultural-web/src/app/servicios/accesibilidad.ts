import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AccesibilidadService {
  isSpeaking = signal(false);
  private synth: SpeechSynthesis | null = null;
  private currentUtterance: SpeechSynthesisUtterance | null = null;

  constructor() {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      this.synth = window.speechSynthesis;
    }
  }

  hablar(texto: string): void {
    if (!this.synth || !texto) return;

    this.detener();

    const utterance = new SpeechSynthesisUtterance(texto);
    utterance.lang = 'es-CO'; // Español colombiano
    utterance.rate = 0.9;     // Cadencia suave para lectura cómoda
    utterance.pitch = 1.0;

    utterance.onstart = () => this.isSpeaking.set(true);
    utterance.onend = () => this.isSpeaking.set(false);
    utterance.onerror = () => this.isSpeaking.set(false);

    this.currentUtterance = utterance;
    this.synth.speak(utterance);
  }

  detener(): void {
    if (this.synth) {
      this.synth.cancel();
      this.isSpeaking.set(false);
    }
  }

  alternarLectura(textoPorDefecto: string = 'Bienvenido a Nexus Cultural, ecosistema digital de comercio ancestral.'): void {
    if (this.isSpeaking()) {
      this.detener();
    } else {
      this.hablar(textoPorDefecto);
    }
  }
}

