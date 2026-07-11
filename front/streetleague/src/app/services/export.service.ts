import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_BASE_URL } from '../utils/constants';

export type ExportDataset = 'matchs' | 'events' | 'statistiques' | 'classement' | 'feuilles';
export type ExportFormat = 'csv' | 'pdf';

/**
 * Déclenche un téléchargement de fichier réel (CSV/PDF) généré par le backend.
 * Récupère le blob puis force la sauvegarde via un lien temporaire.
 */
@Injectable({ providedIn: 'root' })
export class ExportService {
  private readonly baseUrl = `${API_BASE_URL}/export`;

  constructor(private http: HttpClient) {}

  download(
    dataset: ExportDataset,
    format: ExportFormat,
    filters: Record<string, string | undefined> = {}
  ): void {
    let params = new HttpParams().set('format', format);
    for (const [key, value] of Object.entries(filters)) {
      if (value) params = params.set(key, value);
    }

    this.http.get(`${this.baseUrl}/${dataset}`, { params, responseType: 'blob' }).subscribe({
      next: (blob) => this.saveBlob(blob, `${dataset}.${format}`),
      // Errors surface through the global HTTP interceptor toast.
      error: () => {}
    });
  }

  private saveBlob(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
}
