import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match, MatchRequest, MatchStatus } from '../models/match.model';
import { PagedResponse } from '../models/paged.model';
import { API_BASE_URL, ENDPOINTS } from '../utils/constants';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private readonly apiUrl = `${API_BASE_URL}${ENDPOINTS.MATCHES}`;

  constructor(private http: HttpClient) {}

  getMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(this.apiUrl);
  }

  getMatchesPaged(
    page: number,
    size: number,
    filters: { sportId?: string; status?: MatchStatus; date?: string } = {}
  ): Observable<PagedResponse<Match>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filters.sportId) params = params.set('sportId', filters.sportId);
    if (filters.status) params = params.set('status', filters.status);
    if (filters.date) params = params.set('date', filters.date);
    return this.http.get<PagedResponse<Match>>(`${this.apiUrl}/paged`, { params });
  }

  getMatch(id: string): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/${id}`);
  }

  createMatch(match: MatchRequest): Observable<Match> {
    return this.http.post<Match>(this.apiUrl, match);
  }

  updateMatch(id: string, match: Partial<MatchRequest>): Observable<Match> {
    return this.http.put<Match>(`${this.apiUrl}/${id}`, match);
  }

  deleteMatch(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
