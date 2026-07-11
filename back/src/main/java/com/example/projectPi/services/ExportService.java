package com.example.projectPi.services;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;

/**
 * Génère des exports réels (CSV et PDF) à partir d'un tableau générique
 * (titre + entêtes + lignes). Chaque contrôleur construit ses lignes et
 * délègue la sérialisation ici, afin de partager la logique d'export.
 */
@Service
public class ExportService {

    private static final Color HEADER_BG = new Color(15, 23, 42);
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color ROW_ALT = new Color(243, 246, 250);

    // ==================== CSV ====================

    /**
     * Génère un CSV (UTF-8 avec BOM pour Excel) à partir des entêtes et lignes.
     */
    public byte[] toCsv(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(csvLine(headers));
        for (List<String> row : rows) {
            sb.append(csvLine(row));
        }
        // BOM UTF-8 pour qu'Excel reconnaisse les accents.
        byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(body, 0, out, bom.length, body.length);
        return out;
    }

    private String csvLine(List<String> cells) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) line.append(',');
            line.append(csvEscape(cells.get(i)));
        }
        line.append("\r\n");
        return line.toString();
    }

    private String csvEscape(String value) {
        String v = value == null ? "" : value;
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    // ==================== PDF ====================

    /**
     * Génère un PDF paysage avec un titre et un tableau (entêtes + lignes).
     */
    public byte[] toPdf(String title, List<String> headers, List<List<String>> rows) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 42, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(15, 23, 42));
            Paragraph heading = new Paragraph(title, titleFont);
            heading.setSpacingAfter(4f);
            document.add(heading);

            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(120, 130, 150));
            Paragraph meta = new Paragraph("StreetLeague — " + rows.size() + " ligne(s)", metaFont);
            meta.setSpacingAfter(12f);
            document.add(meta);

            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, HEADER_FG);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(HEADER_BG);
                cell.setPadding(6f);
                cell.setBorderColor(new Color(220, 225, 235));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(30, 40, 60));
            boolean alt = false;
            for (List<String> row : rows) {
                for (String value : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(value == null ? "" : value, cellFont));
                    cell.setPadding(5f);
                    cell.setBorderColor(new Color(230, 234, 242));
                    if (alt) cell.setBackgroundColor(ROW_ALT);
                    table.addCell(cell);
                }
                alt = !alt;
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            document.close();
            throw new RuntimeException("Échec de la génération du PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}
