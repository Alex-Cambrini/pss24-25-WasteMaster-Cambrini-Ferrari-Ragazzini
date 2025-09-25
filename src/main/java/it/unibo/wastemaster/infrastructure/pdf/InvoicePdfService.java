package it.unibo.wastemaster.infrastructure.pdf;


import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Invoice;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class InvoicePdfService {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL);

    public void generateInvoicePdf(Invoice invoice, OutputStream out) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        // Titolo
        Paragraph title = new Paragraph("FATTURA", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE);

        // Dati fattura
        Paragraph invoiceData = new Paragraph(
                "ID Fattura: " + invoice.getInvoiceId() + "\n" +
                        "Data: " + invoice.getIssueDate().format(DateTimeFormatter.ISO_DATE) + "\n" +
                        "Stato pagamento: " + invoice.getPaymentStatus().name(),
                NORMAL_FONT);
        document.add(invoiceData);

        document.add(Chunk.NEWLINE);

        // Dati cliente
        Paragraph customerData = new Paragraph(
                "Cliente:\n" +
                        invoice.getCustomer().getName() + "\n" +
                        invoice.getCustomer().getSurname() + "\n" +
                        invoice.getCustomer().getLocation() + "\n" +
                        invoice.getCustomer().getEmail() + "\n" +
                NORMAL_FONT);
        document.add(customerData);

        document.add(Chunk.NEWLINE);

        // Tabella dettagli collection
        PdfPTable table = new PdfPTable(4); // 4 colonne
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 3, 3, 2}); // proporzioni colonne

        // Intestazioni
        addTableHeader(table, "ID");
        addTableHeader(table, "Data");
        addTableHeader(table, "Tipo");
        addTableHeader(table, "Prezzo (€)");

        // Riga dati
        for (Collection c : invoice.getCollections()) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(c.getCollectionId()), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(c.getCollectionDate().format(DateTimeFormatter.ISO_DATE), NORMAL_FONT)));
            String type = c.getSchedule() instanceof it.unibo.wastemaster.domain.model.RecurringSchedule
                    ? "Ricorrente"
                    : "Una tantum";
            table.addCell(new PdfPCell(new Phrase(type, NORMAL_FONT)));
            double price = c.getSchedule() instanceof it.unibo.wastemaster.domain.model.RecurringSchedule
                    ? 0.25
                    : 0.40;
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", price), NORMAL_FONT)));
        }

        document.add(table);

        document.add(Chunk.NEWLINE);

        // Totali
        Paragraph totals = new Paragraph(
                "Totale Ricorrenti: € " + String.format("%.2f", invoice.getTotalRecurring()) + "\n" +
                        "Totale Una Tantum: € " + String.format("%.2f", invoice.getTotalOnetime()) + "\n" +
                        "Totale Fattura: € " + String.format("%.2f", invoice.getAmount()),
                HEADER_FONT);
        totals.setAlignment(Element.ALIGN_RIGHT);
        document.add(totals);

        document.close();
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle, HEADER_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
