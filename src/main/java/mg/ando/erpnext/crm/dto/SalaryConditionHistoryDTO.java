package mg.ando.erpnext.crm.dto;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import mg.ando.erpnext.crm.config.ConditionListConverter;
import mg.ando.erpnext.crm.config.StringListConverter;

@Data
@Entity
@Table(name = "salary_condition_history")
public class SalaryConditionHistoryDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;

    @Column(nullable = false)
    private Double base;

    @Column(nullable = false)
    private Integer sign;

    @Column(columnDefinition = "JSON", nullable = false)
    @Convert(converter = ConditionListConverter.class)
    private List<ConditionDTO> conditions;

    @Column(name = "affected_salary_slips",columnDefinition = "JSON", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> affectedSalarySlips;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "error_message",columnDefinition = "TEXT")
    private String errorMessage;

    @Data
    @Embeddable
    public static class ConditionDTO {
        private String component;
        private String operator;
        private Double value;
    }
}