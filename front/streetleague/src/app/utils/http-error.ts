import { HttpErrorResponse } from '@angular/common/http';

/**
 * Extracts a human-readable message from a backend error response.
 *
 * The API returns `{ statusCode, message, errors[] }` (ApiResponseDTO). We
 * prefer the detailed `errors` list, then `message`, then sensible fallbacks —
 * so the UI shows the real reason instead of a generic "Failed to save".
 */
export function extractErrorMessage(err: unknown, fallback = 'Une erreur est survenue'): string {
  if (err instanceof HttpErrorResponse) {
    const body = err.error;

    if (body) {
      if (Array.isArray(body.errors) && body.errors.length > 0) {
        return body.errors.join('\n');
      }
      if (typeof body.message === 'string' && body.message.trim()) {
        return body.message;
      }
      if (typeof body === 'string' && body.trim()) {
        return body;
      }
    }

    if (err.status === 0) {
      return 'Impossible de joindre le serveur. Vérifiez votre connexion.';
    }
    if (err.message) {
      return err.message;
    }
  }

  if (err instanceof Error && err.message) {
    return err.message;
  }

  return fallback;
}
