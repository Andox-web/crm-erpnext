package mg.ando.erpnext.crm.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

public class Util {
    public static Map<Integer, CSVRecord> parseCsv(MultipartFile csvFile) throws IOException {
        if (csvFile == null || csvFile.isEmpty()) {
            throw new IllegalArgumentException("CSV file is null or empty");
        }

        try (Reader reader = new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                 .builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .build()
                 .parse(reader)) {

            Map<Integer, CSVRecord> recordMap = new HashMap<>();
            for (CSVRecord record : parser) {
                // Use physical line number as key
                recordMap.put((int) record.getRecordNumber()+1, record);
            }
            return recordMap;
        }
    }
}