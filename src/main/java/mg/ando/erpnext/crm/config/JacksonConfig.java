package mg.ando.erpnext.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Configuration de base
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Module pour les types Java 8 Date/Time
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // Configuration de YearMonth
        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        javaTimeModule.addSerializer(YearMonth.class, new YearMonthSerializer(yearMonthFormatter));
        javaTimeModule.addDeserializer(YearMonth.class, new YearMonthDeserializer(yearMonthFormatter));
        
        objectMapper.registerModule(javaTimeModule);
        
        // Désactiver la conversion WRITE_DATES_AS_TIMESTAMPS
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
}