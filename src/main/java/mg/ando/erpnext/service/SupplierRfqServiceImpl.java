package mg.ando.erpnext.service;

import com.fasterxml.jackson.databind.JsonNode;

import groovy.transform.AutoFinal;
import mg.ando.erpnext.dto.Rfq;
import mg.ando.erpnext.dto.RfqItem;
import mg.ando.erpnext.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SupplierRfqServiceImpl implements SupplierRfqService {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;


    public List<Rfq> getAllRfqsForSupplier(String supplierId, String status) {
        try {
            String url = buildRfqUrl(supplierId, status);
            JsonNode response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                Util.createErpRequest(), 
                JsonNode.class
            ).getBody();

            return parseRfqList(response);
        } catch (Exception e) {
            throw new RuntimeException("Erreur RFQ pour fournisseur " + supplierId, e);
        }
    }

    public List<RfqItem> getRfqItems(String rfqId) {
        try {
            String url = ERP_BASE_URL + "/api/resource/Request for Quotation/" 
                       + rfqId;
            
            JsonNode response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                Util.createErpRequest(), 
                JsonNode.class
            ).getBody();

            return parseRfqItems(response.path("data"));
        } catch (Exception e) {
            throw new RuntimeException("Erreur items pour RFQ " + rfqId, e);
        }
    }

    private String buildRfqUrl(String supplierId, String status) {
        List<String> filters = new ArrayList<>();
        filters.add("[\"Request for Quotation Supplier\",\"supplier\",\"=\",\"" + supplierId + "\"]");
        filters.add("[\"docstatus\",\"=\",1]");
        
        if (status != null) {
            filters.add("[\"status\",\"=\",\"" + status + "\"]");
        }
        String url =ERP_BASE_URL + "/api/resource/Request for Quotation?fields=[\"name\",\"transaction_date\",\"status\",\"company\"]&filters="+filters.toString()+"&apply_filters=1";
        return url;
    }

    private List<Rfq> parseRfqList(JsonNode response) {
        JsonNode data = response.path("data");
        List<Rfq> rfqs = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                rfqs.add(Rfq.fromJson(node));
            }
        }
        
        return rfqs;
    }

    private List<RfqItem> parseRfqItems(JsonNode data) {
        JsonNode itemsNode = data.path("items");
        List<RfqItem> items = new ArrayList<>();

        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                items.add(RfqItem.fromJson(item));
            }
        }
        
        return items;
    }
}