package mg.ando.erpnext.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mg.ando.erpnext.dto.ErpQuoteResponse;
import mg.ando.erpnext.dto.QuoteItem;
import mg.ando.erpnext.dto.Rfq;
import mg.ando.erpnext.dto.RfqItem;
import mg.ando.erpnext.dto.SupplierQuote;
import mg.ando.erpnext.dto.UpdatePriceRequest;
import mg.ando.erpnext.util.Util;

@Service
public class SupplierQuoteServiceImpl implements SupplierQuoteService{
    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public List<SupplierQuote> getSupplierQuotes(String rfqId, String status) {
        try {
            // 1) Construire dynamiquement la liste de filtres
            List<String> filtersList = new ArrayList<>();
            filtersList.add(
                "[\"Supplier Quotation Item\",\"request_for_quotation\",\"=\",\"" + rfqId + "\"]"
            );
            if (status != null) {
                filtersList.add(
                    "[\"status\",\"=\",\"" + status + "\"]"
                );
            }

            // 2) Joindre et envelopper dans un tableau JSON
            String filtersJson = "[" + String.join(",", filtersList) + "]";

            // 3) Encoder pour l’URL
            String filtersParam = filtersJson;
            String fieldsJson = "[\"name\",\"transaction_date\",\"valid_till\",\"status\",\"grand_total\",\"currency\",\"supplier\"]";
            String fieldsParam  = fieldsJson;

            // 4) Construire l’URL complet
            String url = ERP_BASE_URL
                    + "/api/resource/Supplier Quotation"
                    + "?fields=" + fieldsParam
                    + "&filters=" + filtersParam
                    + "&apply_filters=1";

            // 5) Faire la requête
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Util.createErpRequest(),
                JsonNode.class
            );

            return parseQuoteList(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur d'encodage des filtres", e);
        }
    }

    private List<SupplierQuote> parseQuoteList(JsonNode response) {
        JsonNode data = response.path("data");
        List<SupplierQuote> suppQuote = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                suppQuote.add(SupplierQuote.fromJson(node));
            }
        }
        
        return suppQuote;
    }

    @Override
    public List<QuoteItem> getQuoteItems(String quoteId) {
        // 1. Construire l'URL ERPNext
        String fields = "[\"items\"]";
        String erpUrl = ERP_BASE_URL + "/api/resource/Supplier Quotation/" + quoteId + "?fields=" + fields;

        
        // 3. Exécuter l'appel API
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            erpUrl,
            HttpMethod.GET,
            Util.createErpRequest(),
            JsonNode.class
        );
        if (response.getStatusCode().is2xxSuccessful()&& response.hasBody()) {
            return parseQuoteItems(response.getBody());
        }
        throw new RuntimeException("Une erreur a ete retenue");
    }
    private List<QuoteItem> parseQuoteItems(JsonNode data) {
        JsonNode itemsNode = data.path("items");
        List<QuoteItem> items = new ArrayList<>();

        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                items.add(QuoteItem.fromJson(item));
            }
        }
        
        return items;
    }

    @Override
    public Map<String, Object> updateAndValidatePrice(String quoteId, List<UpdatePriceRequest> requests) {
        Map<String, Object> response = new HashMap<>();
        try {
            String url = ERP_BASE_URL + "/api/resource/Supplier Quotation/" + quoteId;
    
            // 1. Récupérer le document existant
            ResponseEntity<JsonNode> getResponse = restTemplate.exchange(
                url, HttpMethod.GET, Util.createErpRequest(), JsonNode.class);
    
            JsonNode existingDoc = getResponse.getBody().get("data");
    
            // 2. Mettre à jour les taux
            ArrayNode items = (ArrayNode) existingDoc.get("items");
            for (JsonNode item : items) {
                for (UpdatePriceRequest request : requests) {
                    if (item.get("name").asText().equals(request.getName())) {
                        ((ObjectNode) item).put("rate", request.getRate());
                    }
                }
            }
    
            // 3. Envoyer les modifications
            Map<String, Object> updateData = Map.of(
                "items", items,
                "modified_by", "API User"
            );
    
            HttpEntity<Map<String, Object>> httpEntity = Util.createErpRequestWithBody(updateData);
            ResponseEntity<JsonNode> erpResponse = restTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, JsonNode.class);
    
            if (!erpResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de la mise à jour : " + erpResponse.getBody());
            }
    
            // 4. Soumettre le devis (passage au statut "Submitted")
            String submitUrl = ERP_BASE_URL + "/api/resource/Supplier Quotation/" + quoteId + "?run_method=submit";
            ResponseEntity<JsonNode> submitResponse = restTemplate.exchange(
                submitUrl, HttpMethod.POST, Util.createErpRequest(), JsonNode.class);
    
            if (!submitResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de la soumission : " + submitResponse.getBody());
            }
    
            response.put("success", true);
            response.put("message", "Prix mis à jour et devis soumis avec succès");
            return response;
    
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
