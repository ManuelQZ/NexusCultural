import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RouterOutlet, RouterLink } from '@angular/router'


@Component({
  imports: [RouterOutlet, RouterLink],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('nexusCultural');
  footer = signal('Universidad del Quindío');
}
