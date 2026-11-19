import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { ArtesanoPanel } from './componentes/artesano-panel/artesano-panel';
import { AdminPanel } from './componentes/admin-panel/admin-panel';
import { Catalogo } from './componentes/catalogo/catalogo';
import { DetalleProducto } from './componentes/detalle-producto/detalle-producto';
import { Carrito } from './componentes/carrito/carrito';
import { Checkout } from './componentes/checkout/checkout';
import { Pedidos } from './componentes/pedidos/pedidos';
import { authGuard, guestGuard, artisanGuard, adminGuard } from './guards/auth-guard';

export const routes: Routes = [
  // 1. Pantalla de Bienvenida y Puerta de Entrada (Solo botones de Login y Registro)
  { path: '', component: Inicio },

  // 2. Autenticación y Registro Público (Solo Comprador y Artesano)
  { path: 'login', component: Login, canActivate: [guestGuard] },
  { path: 'registro', component: Registro, canActivate: [guestGuard] },

  // 3. Panel Exclusivo del Maestro Artesano (Cargar artesanías, stock, pedidos de su taller)
  { path: 'artesano', component: ArtesanoPanel, canActivate: [artisanGuard] },

  // 4. Panel Exclusivo de Administración (Gestión interna de admins, moderación, devoluciones)
  { path: 'admin', component: AdminPanel, canActivate: [adminGuard] },

  // 5. Módulo del Comprador Cultural (Catálogo, Detalle, Carrito, Pagos y Mis Compras)
  { path: 'catalogo', component: Catalogo, canActivate: [authGuard] },
  { path: 'detalle-producto', component: DetalleProducto, canActivate: [authGuard] },
  { path: 'carrito', component: Carrito, canActivate: [authGuard] },
  { path: 'checkout', component: Checkout, canActivate: [authGuard] },
  { path: 'pedidos', component: Pedidos, canActivate: [authGuard] },

  // Redirección por defecto
  { path: '**', pathMatch: 'full', redirectTo: '/' }
];
