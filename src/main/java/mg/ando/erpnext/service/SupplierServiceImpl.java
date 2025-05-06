package mg.ando.erpnext.service;

import com.fasterxml.jackson.databind.JsonNode;
import mg.ando.erpnext.dto.Supplier;
import mg.ando.erpnext.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;
    
    @Autowired
    private RestTemplate restTemplate;

    public List<Supplier> getAllSuppliers() {
        try {
            String fields = "[\"name\",\"supplier_name\",\"supplier_type\",\"email_id\",\"mobile_no\",\"modified\"]";
            String url = ERP_BASE_URL + "/api/resource/Supplier?fields=" + fields;
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Util.createErpRequest(),
                JsonNode.class
            );

            return processJsonResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des fournisseurs", e);
        }
    }

    private List<Supplier> processJsonResponse(JsonNode rootNode) {
        List<Supplier> suppliers = new ArrayList<>();
        JsonNode dataNode = rootNode.path("data");

        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                suppliers.add(mapJsonToSupplier(node));
            }
        }
        
        return suppliers;
    }

    private Supplier mapJsonToSupplier(JsonNode node) {
        Supplier supplier = new Supplier();
        supplier.setName(getTextValue(node, "name"));
        supplier.setSupplierName(getTextValue(node, "supplier_name"));
        supplier.setSupplierType(getTextValue(node, "supplier_type"));
        supplier.setEmailId(getTextValue(node, "email_id"));
        supplier.setMobileNo(getTextValue(node, "mobile_no"));
        supplier.setModified(getTextValue(node, "modified"));
        return supplier;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : "";
    }
    public Supplier getSupplierDetails(String supplierId) {
        String url = ERP_BASE_URL + "/api/resource/Supplier/" + supplierId;
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            Util.createErpRequest(),
            JsonNode.class
        );
        return Supplier.convertFromJson(response.getBody().get("data"));
    }
}