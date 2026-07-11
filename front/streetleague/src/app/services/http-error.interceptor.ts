import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastService } from './toast.service';
import { extractErrorMessage } from '../utils/http-error';

/**
 * HTTP Error Interceptor.
 *
 * Surfaces a readable toast (from the backend's `message`/`errors[]`) on any
 * failed request, then rethrows the ORIGINAL response so components can extract
 * the detailed message themselves via `extractErrorMessage`.
 *
 * Registered in app.config.ts via HTTP_INTERCEPTORS.
 */
@Injectable({ providedIn: 'root' })
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private toastService: ToastService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const message = extractErrorMessage(error);

        this.toastService.error(
          error.status === 401 ? 'Session expirée. Veuillez vous reconnecter.' : message
        );

        if (error.status >= 500) {
          console.error('Server error:', error);
        }

        return throwError(() => error);
      })
    );
  }
}

/**
 * ApiResponse Interface - matches the backend ApiResponseDTO shape.
 */
export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  data: T | null;
  errors: string[] | null;
  timestamp: string;
}
