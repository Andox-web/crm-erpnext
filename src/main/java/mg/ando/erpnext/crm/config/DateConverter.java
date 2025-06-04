package mg.ando.erpnext.crm.config;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateConverter {

    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String convertToIsoDate(String inputDate) throws DateTimeParseException {
        if (inputDate == null || inputDate.isBlank()) {
            throw new IllegalArgumentException("La date ne peut pas être nulle ou vide");
        }

        try {
            // Étape 1: Valider et parser la date
            LocalDate date = LocalDate.parse(inputDate.trim(), dateFormatter);
            
            // Étape 2: Convertir en format ISO (yyyy-MM-dd)
            return date.format(DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                "Format de date invalide. Format attendu: dd/MM/yyyy", 
                inputDate, 
                e.getErrorIndex()
            );
        }
    }
    public static String getStartDateOfMonth(String inputDate) throws DateTimeParseException {
        inputDate = convertToIsoDate(inputDate);
        LocalDate date = LocalDate.parse(inputDate.trim(), DateTimeFormatter.ISO_DATE);
        return date.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE);
    }
    public static String getEndDateOfMonth(String inputDate) throws DateTimeParseException {
        inputDate = convertToIsoDate(inputDate);
        LocalDate date = LocalDate.parse(inputDate.trim(), DateTimeFormatter.ISO_DATE);
        return YearMonth.from(date).atEndOfMonth().format(DateTimeFormatter.ISO_DATE);
    }

    public static String getEndDateOfPreviousMonth(String inputDate) throws DateTimeParseException {
        inputDate = convertToIsoDate(inputDate);
        LocalDate date = LocalDate.parse(inputDate.trim(), DateTimeFormatter.ISO_DATE).minusMonths(1);
        return YearMonth.from(date).atEndOfMonth().format(DateTimeFormatter.ISO_DATE);
    }
    public static String setMonthDateInWords(String inputDate){
        LocalDate date = LocalDate.parse(inputDate.trim(), DateTimeFormatter.ISO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }
}