import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AccesibilidadService } from '../../servicios/accesibilidad';

interface HistoriaCultural {
  id: string;
  pueblo: string;
  region: string;
  maestro: string;
  titulo: string;
  imagen: string;
  relato: string;
  tecnica: string;
}

@Component({
  selector: 'app-exploracion-cultural',
  imports: [CommonModule, RouterLink],
  templateUrl: './exploracion-cultural.html',
  styleUrl: './exploracion-cultural.css'
})
export class ExploracionCultural {
  accesibilidad = inject(AccesibilidadService);

  historias: HistoriaCultural[] = [
    {
      id: '1',
      pueblo: 'Pueblo Wayuu (La Guajira)',
      region: 'Ranchería Jepirra, Desierto de la Alta Guajira',
      maestro: 'Elena Epieyú Pushaina',
      titulo: 'Wale’kerü y los Hilos de la Creación',
      imagen: 'https://images.unsplash.com/photo-1590736969955-71cc94801759?auto=format&fit=crop&w=700&q=80',
      relato: 'En la cosmovisión wayuu, la araña Wale’kerü tejió durante la noche los primeros diseños Kanaas enseñando la sabiduría del silencio, el linaje materno y el respeto al viento desértico.',
      tecnica: 'Tejeduría Kanaas en telar vertical tradicional'
    },
    {
      id: '2',
      pueblo: 'Pueblo Zenú (Córdoba / Sucre)',
      region: 'Resguardo Indígena de San Andrés de Sotavento',
      maestro: 'Demetrio Polo',
      titulo: 'La Caña Flecha y el Río de la Memoria',
      imagen: 'https://images.unsplash.com/photo-1528459801416-a9e53bbf4e17?auto=format&fit=crop&w=700&q=80',
      relato: 'El sombrero vueltiao es una geografía trenzada. El contraste entre la caña blanca (secada al sol) y la caña negra (tinturada en barro y jagua) narra el curso de los ríos y el ciclo agrícola.',
      tecnica: 'Trenzado de caña flecha ripiada a 21 vueltas'
    },
    {
      id: '3',
      pueblo: 'Comunidad Kamëntsá (Putumayo)',
      region: 'Valle de Sibundoy',
      maestro: 'Taita Juan Jacanamijoy',
      titulo: 'El Bëtsknaté y el Rostro del Perdón',
      imagen: 'https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=700&q=80',
      relato: 'Las máscaras ceremoniales de madera y chaquiras plasman los gestos de alegría, reconciliación y curación. Se usan en el Carnaval del Perdón para honrar la sanación comunitaria.',
      tecnica: 'Talla en cedro con enchapado manual de mostacilla'
    }
  ];

  escucharRelato(h: HistoriaCultural): void {
    this.accesibilidad.alternarLectura(
      `Historia de ${h.pueblo}. Maestro: ${h.maestro}. Título: ${h.titulo}. ${h.relato}`
    );
  }
}

