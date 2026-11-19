export interface Precio {
  monto: number;
  moneda: string;
}

export interface ArticuloDetalleResponse {
  id: string;
  artesanoId: string;
  titulo: string;
  descripcion: string;
  montoPrecio: number;
  moneda: string;
  stock: number;
  estado: string; // 'ACTIVO' | 'INACTIVO'
  grupo: string;  // 'ESTATUAS' | 'CERAMICA' | 'TEJIDOS' | 'JOYERIA' | 'BISUTERIA' | 'MADERA'
  imagenes: string[];
  fechaPublicacion: string;
  precioConDescuento?: number | null;
  porcentajeDescuento?: number | null;
  departamento?: string | null;
  municipio?: string | null;
}

export interface CrearArticuloRequest {
  artesanoId: string;
  titulo: string;
  descripcion: string;
  stock: number;
  grupo: string;
  imagenes: string[];
  precio: Precio;
}

export interface RegistrarArtesanoRequest {
  nombreCompleto: string;
  email: string;
  anosExperiencia: number;
  reconocimientos: string[];
  departamento: string;
  municipio: string;
  direccion: string;
}

export interface ArtesanoResponse {
  id: string;
  nombreCompleto: string;
  email: string;
  anosExperiencia: number;
  reconocimientos: string[];
  departamento: string;
  municipio: string;
  direccion: string;
  activo: boolean;
}

export interface RegistrarCompradorRequest {
  nombreCompleto: string;
  email: string;
  departamento: string;
  municipio: string;
  direccion: string;
}

export interface CompradorResponse {
  id: string;
  nombreCompleto: string;
  email: string;
  departamento: string;
  municipio: string;
  direccion: string;
}

export interface CrearAdministradorRequest {
  nombreCompleto: string;
  email: string;
  nivelAcceso: string; // 'SUPER_ADMIN' | 'GESTOR_CULTURAL'
}

export interface AdministradorResponse {
  id: string;
  nombreCompleto: string;
  email: string;
  nivelAcceso: string;
  activo: boolean;
}

export interface PedidoResponse {
  id: string;
  compradorId: string;
  articuloId: string;
  cantidad: number;
  total: number;
  moneda: string;
  estado: string; // 'PENDIENTE' | 'CONFIRMADO' | 'EN_CAMINO' | 'ENTREGADO' | 'CANCELADO' | 'DEVUELTO'
  fechaCreacion?: string;
  fechaEntrega?: string;
  metodoPago?: string;
  referenciaTransaccion?: string;
  motivoDevolucion?: string;
  estadoDevolucion?: string;
}

export interface DevolucionDetalleResponse {
  id: string;
  pedidoId: string;
  compradorId: string;
  motivo: string;
  estado: string; // 'PENDIENTE' | 'APROBADA' | 'RECHAZADA'
  fechaSolicitud?: string;
}

export interface PresignedUrlResponse {
  uploadUrl: string;
  publicUrl: string;
}

