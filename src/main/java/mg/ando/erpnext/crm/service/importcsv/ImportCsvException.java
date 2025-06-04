package mg.ando.erpnext.crm.service.importcsv;

public class ImportCsvException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImportCsvException(String message) {
        super(message);
    }

    public ImportCsvException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportCsvException(Throwable cause) {
        super(cause);
    }
    
}
