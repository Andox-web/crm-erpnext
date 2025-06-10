package mg.ando.erpnext.crm.config;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateConverter {

   
    // Liste des formateurs supportés (dans l'ordre de priorité)
    private static final List<DateTimeFormatter> SUPPORTED_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ISO_LOCAL_DATE,                    // yyyy-MM-dd
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),           // Format original
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),           // Tiret comme séparateur
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),           // Format logique (année en premier)
        DateTimeFormatter.ofPattern("dd MMM yyyy"),          // 01 jan 2023 (anglais)
        DateTimeFormatter.ofPattern("dd MMMM yyyy")          // 01 January 2023 (anglais)
    );

    public static String convertToIsoDate(String inputDate) throws DateTimeParseException {
        if (inputDate == null || inputDate.isBlank()) {
            throw new IllegalArgumentException("La date ne peut pas être nulle ou vide");
        }

        String trimmedDate = inputDate.trim();
        DateTimeParseException lastException = null;

        // Essayer chaque formateur dans l'ordre
        for (DateTimeFormatter formatter : SUPPORTED_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmedDate, formatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                lastException = e; // Conserver la dernière exception
            }
        }

        // Aucun format valide trouvé
        String errorMessage = "Format de date invalide. Formats acceptés:"
                + "yyyy-MM-dd "
                + "| dd/MM/yyyy "
                + "| dd-MM-yyyy "
                + "| yyyy/MM/dd "
                + "| dd MMM yyyy (ex: 01 Jan 2023) "
                + "| dd MMMM yyyy (ex: 01 January 2023)";
        
        throw new DateTimeParseException(
            errorMessage, 
            trimmedDate, 
            lastException != null ? lastException.getErrorIndex() : 0
        );
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
    public static String getYear(String inputDate) {
        inputDate = convertToIsoDate(inputDate);
        LocalDate date = LocalDate.parse(inputDate.trim(), DateTimeFormatter.ISO_DATE);
        return String.valueOf(date.getYear());
    }
}