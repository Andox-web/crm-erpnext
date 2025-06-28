package mg.ando.erpnext.crm.config;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mg.ando.erpnext.crm.dto.SalaryConditionHistoryDTO;

@Converter
public class ConditionListConverter implements AttributeConverter<List<SalaryConditionHistoryDTO.ConditionDTO>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SalaryConditionHistoryDTO.ConditionDTO> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Erreur conversion Conditions en JSON", e);
        }
    }

    @Override
    public List<SalaryConditionHistoryDTO.ConditionDTO> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erreur conversion JSON en Conditions", e);
        }
    }
}
