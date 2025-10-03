package it.unibo.wastemaster.infrastructure.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
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
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Service to generate invoice PDFs.
 */
public class InvoicePdfService {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL);
    private static final float PAGE_MARGIN = 36f;

    /**
     * Generates a PDF for the given invoice and writes it to the provided OutputStream.
     *
     * @param invoice the invoice to generate PDF for
     * @param out the output stream to write the PDF
     * @throws Exception if an error occurs while writing the PDF
     */
    public void generateInvoicePdf(final Invoice invoice, final OutputStream out)
            throws Exception {
        Document document =
                new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN,
                        PAGE_MARGIN);
        PdfWriter.getInstance(document, out);
        document.open();

        addInvoiceHeader(document, invoice);
        addCollectionsTable(document, invoice);
        addTotals(document, invoice);

        document.close();
    }

    /**
     * Adds the invoice header section including title, invoice details, and customer
     * information.
     *
     * @param document the PDF document
     * @param invoice the invoice to display
     * @throws DocumentException if an error occurs while adding elements to the document
     */
    private void addInvoiceHeader(final Document document, final Invoice invoice)
            throws DocumentException {
        Paragraph title = new Paragraph("INVOICE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        Paragraph invoiceData = new Paragraph(
                "Invoice ID: " + invoice.getInvoiceId() + "\n"
                        + "Date: " + invoice.getIssueDate()
                        .format(DateTimeFormatter.ISO_DATE) + "\n"
                        + "Payment status: " + invoice.getPaymentStatus().name()
                        + (invoice.getPaymentStatus() == Invoice.PaymentStatus.PAID
                        && invoice.getPaymentDate() != null
                        ? "\nPayment date: " + invoice.getPaymentDate()
                        .format(DateTimeFormatter.ISO_DATE)
                        : ""),
                NORMAL_FONT);
        document.add(invoiceData);
        document.add(Chunk.NEWLINE);

        String customerInfo = "Customer Name: " + invoice.getCustomer().getName() + " "
                + invoice.getCustomer().getSurname() + "\n"
                + "Address: " + invoice.getCustomer().getLocation() + "\n"
                + "Email: " + invoice.getCustomer().getEmail();
        Paragraph customerData = new Paragraph(customerInfo, NORMAL_FONT);
        document.add(customerData);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds a table listing all collections in the invoice.
     *
     * @param document the PDF document
     * @param invoice the invoice containing collections
     * @throws DocumentException if an error occurs while adding the table
     */
    private void addCollectionsTable(final Document document, final Invoice invoice)
            throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {2, 3, 3, 2});

        addTableHeader(table, "ID");
        addTableHeader(table, "Date");
        addTableHeader(table, "Type");
        addTableHeader(table, "Price (€)");

        double recurringFee = InvoiceManager.getRecurringFee();
        double oneTimeFee = InvoiceManager.getOneTimeFee();

        for (Collection c : invoice.getCollections()) {
            table.addCell(new PdfPCell(
                    new Phrase(String.valueOf(c.getCollectionId()), NORMAL_FONT)));
            table.addCell(new PdfPCell(
                    new Phrase(c.getCollectionDate().format(DateTimeFormatter.ISO_DATE),
                            NORMAL_FONT)));
            String type = c.getSchedule() instanceof RecurringSchedule ? "Recurring"
                    : "One-time";
            table.addCell(new PdfPCell(new Phrase(type, NORMAL_FONT)));
            double price = c.getSchedule() instanceof RecurringSchedule ? recurringFee
                    : oneTimeFee;
            table.addCell(
                    new PdfPCell(new Phrase(String.format("%.2f", price), NORMAL_FONT)));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the total amounts section at the bottom of the invoice.
     *
     * @param document the PDF document
     * @param invoice the invoice containing totals
     * @throws DocumentException if an error occurs while adding totals
     */
    private void addTotals(final Document document, final Invoice invoice)
            throws DocumentException {
        Paragraph totals = new Paragraph(
                "Total Recurring: € " + String.format("%.2f", invoice.getTotalRecurring())
                        + "\n"
                        + "Total One-time: € " + String.format("%.2f",
                        invoice.getTotalOnetime()) + "\n"
                        + "Total Amount: € " + String.format("%.2f", invoice.getAmount()),
                HEADER_FONT);
        totals.setAlignment(Element.ALIGN_RIGHT);
        document.add(totals);
    }

    /**
     * Adds a header cell to a PDF table with gray background and centered text.
     *
     * @param table the table to add the header to
     * @param headerTitle the text of the header
     */
    private void addTableHeader(final PdfPTable table, final String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle, HEADER_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
