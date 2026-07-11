/** Server-side pagination envelope returned by the `/paged` endpoints. */
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
