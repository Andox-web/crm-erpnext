package mg.ando.erpnext.crm.config;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class Filter {
    private String field;
    private String operator;
    private String value;

    public Filter(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }
    
    public String toJson() {
        return "[\"" + field + "\", \"" + operator + "\", \"" + value + "\"]";
    }
}