package com.example.projectPi.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Réponse paginée générique renvoyée par les endpoints "/paged".
 * Format stable et léger (contrairement à la sérialisation par défaut de Page).
 */
@Schema(name = "PagedResponse", description = "Résultat paginé")
public class PagedResponse<T> {

    @Schema(description = "Éléments de la page courante")
    private List<T> content;

    @Schema(description = "Numéro de page (1-based)", example = "1")
    private int page;

    @Schema(description = "Taille de page", example = "10")
    private int size;

    @Schema(description = "Nombre total d'éléments", example = "197")
    private long totalElements;

    @Schema(description = "Nombre total de pages", example = "20")
    private int totalPages;

    public PagedResponse() {}

    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<T> getContent()          { return content; }
    public void setContent(List<T> c)    { this.content = c; }
    public int getPage()                 { return page; }
    public void setPage(int page)        { this.page = page; }
    public int getSize()                 { return size; }
    public void setSize(int size)        { this.size = size; }
    public long getTotalElements()       { return totalElements; }
    public void setTotalElements(long t) { this.totalElements = t; }
    public int getTotalPages()           { return totalPages; }
    public void setTotalPages(int t)     { this.totalPages = t; }
}
