package mg.ando.erpnext.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import mg.ando.erpnext.dto.ErpOrderResponse;
import mg.ando.erpnext.dto.OrderItem;
import mg.ando.erpnext.dto.QuoteItem;
import mg.ando.erpnext.dto.SupplierOrder;
import mg.ando.erpnext.dto.SupplierQuote;
import mg.ando.erpnext.util.Util;

@Service
public class SupplierOrderServiceImpl implements SupplierOrderService{

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<SupplierOrder> getSupplierOrders(String supplierId, String status) {
        String filters = "[[\"supplier\",\"=\",\"" + supplierId + "\"]]";
        if (status != null) {
            filters = "[[\"supplier\",\"=\",\"" + supplierId + "\"],[\"status\",\"=\",\"" + status + "\"]]";
        }
        
        String fields = "[\"name\",\"transaction_date\",\"status\",\"grand_total\",\"currency\"]";
        
        String url = ERP_BASE_URL + "/api/resource/Purchase Order?fields=" + fields 
                   + "&filters=" + filters;
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            Util.createErpRequest(),
            JsonNode.class
        );
        
        return   parseOrderList(response.getBody());   
    }
    private List<SupplierOrder> parseOrderList(JsonNode response) {
        JsonNode data = response.path("data");
        List<SupplierOrder> suppOrder = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                suppOrder.add(SupplierOrder.fromJson(node));
            }
        }
        
        return suppOrder;
    }

    @Override
    public List<OrderItem> getSupplierOrdersItem(String orderId) {
        String fields = "[\"items\"]";
        String erpUrl = ERP_BASE_URL + "/api/resource/Purchase Order/" + orderId + "?fields=" + fields;

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            erpUrl,
            HttpMethod.GET,
            Util.createErpRequest(),
            JsonNode.class
        );
        if (response.getStatusCode().is2xxSuccessful()&& response.hasBody()) {
            return parseOrderItem(response.getBody());
        }
        throw new RuntimeException("Une erreur a ete retenue");
    }
    private List<OrderItem> parseOrderItem(JsonNode data) {
        JsonNode itemsNode = data.path("items");
        List<OrderItem> items = new ArrayList<>();

        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                items.add(OrderItem.fromJson(item));
            }
        }
        
        return items;
    }
    
}
