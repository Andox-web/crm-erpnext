package mg.ando.erpnext.crm.service.salary;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import mg.ando.erpnext.crm.config.DateConverter;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;

@Service
public class PdfServiceImpl implements PdfService {
    public byte[] generatePdf(SalarySlipDTO salarySlip, EmployeDTO employee) throws DocumentException {
        // Configuration du document
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Couleurs harmonieuses
        Color primaryColor = new Color(59, 89, 152);
        Color secondaryColor = new Color(75, 119, 190);
        Color textColor = new Color(44, 62, 80);
        Color borderColor = new Color(189, 195, 199);
        Color totalBgColor = new Color(245, 249, 252);

        // Polices
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, primaryColor);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, textColor);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, textColor);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, textColor);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);
        Font summaryTotalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryColor);
        Font signatureFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new Color(100, 100, 100));

        // En-tête
        Paragraph header = new Paragraph("BULLETIN DE SALAIRE", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(15f);
        document.add(header);

        // Informations employé
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setSpacingAfter(15f);

        addInfoCell(infoTable, "Nom:", employee.getEmployeeName(), boldFont, normalFont, borderColor);
        addInfoCell(infoTable, "Matricule:", employee.getEmployeeNumber(), boldFont, normalFont, borderColor);
        addInfoCell(infoTable, "Date d'embauche:", DateConverter.setMonthDateInWords(employee.getDateOfJoining()), boldFont, normalFont, borderColor);
        addInfoCell(infoTable, "Période:", DateConverter.setMonthDateInWords(salarySlip.getStartDate()) + " - " + DateConverter.setMonthDateInWords(salarySlip.getEndDate()), boldFont, normalFont, borderColor);
        addInfoCell(infoTable, "Structure salariale:", salarySlip.getSalaryStructure(), boldFont, normalFont, borderColor);
        addInfoCell(infoTable, "Statut:", employee.getStatus(), boldFont, normalFont, borderColor);

        document.add(infoTable);

        // Section des gains
        addSectionTitle(document, "GAINS", titleFont);
        PdfPTable earningsTable = createDataTable(
            salarySlip.getEarnings(),
            salarySlip.getCurrency(),
            "Total Gains",
            salarySlip.getGrossPay(),
            secondaryColor,
            borderColor,
            totalBgColor,
            boldFont,
            totalFont,
            normalFont
        );
        document.add(earningsTable);

        // Section des déductions
        addSectionTitle(document, "DÉDUCTIONS", titleFont);
        PdfPTable deductionsTable = createDataTable(
            salarySlip.getDeductions(),
            salarySlip.getCurrency(),
            "Total Déductions",
            salarySlip.getTotalDeduction(),
            secondaryColor,
            borderColor,
            totalBgColor,
            boldFont,
            totalFont,
            normalFont
        );
        document.add(deductionsTable);

        // Récapitulatif
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setSpacingBefore(20f);
        summaryTable.setSpacingAfter(10f);

        addSummaryCell(summaryTable, "Salaire Brut:", salarySlip.getGrossPay(), salarySlip.getCurrency(), boldFont, borderColor);
        addSummaryCell(summaryTable, "Total Déductions:", salarySlip.getTotalDeduction(), salarySlip.getCurrency(), boldFont, borderColor);
        addSummaryCell(summaryTable, "Net à Payer:", salarySlip.getNetPay(), salarySlip.getCurrency(), summaryTotalFont, borderColor);
        addSummaryCell(summaryTable, "Arrondi:", salarySlip.getRoundedTotal(), salarySlip.getCurrency(), boldFont, borderColor);
        
        document.add(summaryTable);

        // Montant en lettres
        Paragraph inWords = new Paragraph("Montant en lettres: " + salarySlip.getTotalInWords(), normalFont);
        inWords.setAlignment(Element.ALIGN_CENTER);
        inWords.setSpacingBefore(10f);
        document.add(inWords);

        // Section de signatures
        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(30f);

        // Création d'une police pour le label "Signature :"
        Font signatureLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, textColor);

        // Signature employé
        PdfPCell employeeSignatureCell = new PdfPCell();
        employeeSignatureCell.setBorder(Rectangle.NO_BORDER);
        employeeSignatureCell.setPaddingTop(15f);

        // Création d'une table pour contenir le label et la ligne
        PdfPTable employeeSignatureContent = new PdfPTable(2);
        employeeSignatureContent.setWidthPercentage(100);
        employeeSignatureContent.setWidths(new float[]{30, 70});

        // Partie texte "Signature :"
        PdfPCell employeeLabelCell = new PdfPCell(new Phrase("Signature de l'employee:", signatureLabelFont));
        employeeLabelCell.setBorder(Rectangle.NO_BORDER);
        employeeLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        employeeSignatureContent.addCell(employeeLabelCell);

        // Partie ligne noire
        PdfPCell employeeLineCell = new PdfPCell();
        employeeLineCell.setBorder(Rectangle.BOTTOM);
        employeeLineCell.setBorderColorBottom(Color.BLACK);
        employeeLineCell.setBorderWidthBottom(1.5f); // Épaisseur de la ligne
        employeeLineCell.setFixedHeight(20f); // Hauteur de la cellule pour la ligne
        employeeSignatureContent.addCell(employeeLineCell);

        employeeSignatureCell.addElement(employeeSignatureContent);
        employeeSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Signature employeur (même structure)
        PdfPCell employerSignatureCell = new PdfPCell();
        employerSignatureCell.setBorder(Rectangle.NO_BORDER);
        employerSignatureCell.setPaddingTop(15f);

        PdfPTable employerSignatureContent = new PdfPTable(2);
        employerSignatureContent.setWidthPercentage(100);
        employerSignatureContent.setWidths(new float[]{30, 70});

        PdfPCell employerLabelCell = new PdfPCell(new Phrase("Signature de l'employeur:", signatureLabelFont));
        employerLabelCell.setBorder(Rectangle.NO_BORDER);
        employerLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        employerSignatureContent.addCell(employerLabelCell);

        PdfPCell employerLineCell = new PdfPCell();
        employerLineCell.setBorder(Rectangle.BOTTOM);
        employerLineCell.setBorderColorBottom(Color.BLACK);
        employerLineCell.setBorderWidthBottom(1.5f);
        employerLineCell.setFixedHeight(20f);
        employerSignatureContent.addCell(employerLineCell);

        employerSignatureCell.addElement(employerSignatureContent);
        employerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        signatureTable.addCell(employeeSignatureCell);
        signatureTable.addCell(employerSignatureCell);

        document.add(signatureTable);

        // Pied de page
        Paragraph footer = new Paragraph("Document généré le " + new java.util.Date(), normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(15f);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    // Méthodes utilitaires
    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont, Color borderColor) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorderColor(borderColor);
        labelCell.setBackgroundColor(new Color(245, 245, 245));
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
        valueCell.setBorderColor(borderColor);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addSectionTitle(Document document, String title, Font font) throws DocumentException {
        Paragraph p = new Paragraph(title, font);
        p.setSpacingBefore(15f);
        p.setSpacingAfter(8f);
        document.add(p);
    }

    private PdfPTable createDataTable(
        List<SalaryDetailDTO> items, 
        String currency, 
        String totalLabel,
        Double totalValue,
        Color headerColor, 
        Color borderColor,
        Color totalBgColor,
        Font headerFont, 
        Font totalFont,
        Font contentFont
    ) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15f);
        
        // En-tête du tableau
        PdfPCell h1 = new PdfPCell(new Phrase("Libellé", headerFont));
        PdfPCell h2 = new PdfPCell(new Phrase("Montant", headerFont));
        h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        for (PdfPCell headerCell : new PdfPCell[]{h1, h2}) {
            headerCell.setBackgroundColor(headerColor);
            headerCell.setBorderColor(borderColor);
            headerCell.setPadding(5);
        }
        
        table.addCell(h1);
        table.addCell(h2);
        
        // Données
        for (SalaryDetailDTO item : items) {
            PdfPCell labelCell = createCell(item.getSalaryComponent(), contentFont, borderColor);
            PdfPCell valueCell = createCell(formatCurrency(item.getAmount(), currency), contentFont, borderColor);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(labelCell);
            table.addCell(valueCell);
        }
        
        // Ligne de total
        PdfPCell totalLabelCell = new PdfPCell(new Phrase(totalLabel, totalFont));
        totalLabelCell.setBorderColor(borderColor);
        totalLabelCell.setBackgroundColor(totalBgColor);
        totalLabelCell.setPadding(5);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase(formatCurrency(totalValue, currency), totalFont));
        totalValueCell.setBorderColor(borderColor);
        totalValueCell.setBackgroundColor(totalBgColor);
        totalValueCell.setPadding(5);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(totalLabelCell);
        table.addCell(totalValueCell);
        
        return table;
    }

    private PdfPCell createCell(String text, Font font, Color borderColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(borderColor);
        cell.setPadding(5);
        return cell;
    }

    private void addSummaryCell(
        PdfPTable table, 
        String label, 
        Double value, 
        String currency,
        Font font, 
        Color borderColor
    ) {
        PdfPCell labelCell = createCell(label, font, borderColor);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);
        
        PdfPCell valueCell = createCell(formatCurrency(value, currency), font, borderColor);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private String formatCurrency(Double amount, String currency) {
        return String.format("%,.2f", amount) + " " + currency;
    }
}
