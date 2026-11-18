import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './componentes/header/header';
import { Footer } from './componentes/footer/footer';
import { Notificaciones } from './componentes/notificaciones/notificaciones';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, Notificaciones],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
