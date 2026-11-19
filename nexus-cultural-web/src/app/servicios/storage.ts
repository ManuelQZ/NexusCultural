import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { PresignedUrlResponse } from '../modelos/nexus';

@Injectable({ providedIn: 'root' })
export class StorageService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/v1/storage/presigned-url`;

  obtenerPresignedUrl(extension: string, contentType: string) {
    const params = new HttpParams()
      .set('extension', extension.toLowerCase().replace('.', ''))
      .set('contentType', contentType);
    return this.http.get<PresignedUrlResponse>(this.url, { params });
  }

  subirArchivoS3(uploadUrl: string, file: File) {
    const headers = new HttpHeaders({
      'Content-Type': file.type
    });
    return this.http.put(uploadUrl, file, { headers });
  }
}

