package mg.ando.erpnext.service;

import com.fasterxml.jackson.databind.JsonNode;
import mg.ando.erpnext.dto.Invoice;
import mg.ando.erpnext.dto.InvoiceItem;
import mg.ando.erpnext.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseInvoiceServiceimpl implements PurchaseInvoiceService {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public List<Invoice> getInvoices(String status) {
        String url = buildInvoiceUrl(status);
        JsonNode response = restTemplate.exchange(url, HttpMethod.GET, Util.createErpRequest(), JsonNode.class).getBody();
        return parseInvoiceList(response);
    }

    public List<InvoiceItem> getInvoiceItems(String invoiceId) {
        String url = ERP_BASE_URL + "/api/resource/Purchase Invoice/" + invoiceId;
        JsonNode response = restTemplate.exchange(url, HttpMethod.GET, Util.createErpRequest(), JsonNode.class).getBody();
        return parseInvoiceItems(response.path("data"));
    }

    public JsonNode createPayment(String invoiceId, Map<String, Object> paymentData) {
        JsonNode invoice = fetchInvoiceDetails(invoiceId);
        Map<String, Object> paymentEntry = buildPaymentEntry(invoiceId, paymentData, invoice);
        
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            ERP_BASE_URL + "/api/resource/Payment Entry",
            Util.createErpRequest(),
            JsonNode.class
        );
        
        submitPaymentEntry(response.getBody().path("data").path("name").asText());
        return response.getBody();
    }

    private String buildInvoiceUrl(String status) {
        List<String> filters = new ArrayList<>();
        filters.add("[\"docstatus\",\"=\",1]");
        
        if (status != null) {
            filters.add("[\"status\",\"=\",\"" + status + "\"]");
        }

        return UriComponentsBuilder.fromHttpUrl(ERP_BASE_URL + "/api/resource/Purchase Invoice")
            .queryParam("fields", "[\"name\",\"posting_date\",\"due_date\",\"status\",\"grand_total\",\"currency\",\"supplier\",\"supplier_name\",\"company\"]")
            .queryParam("filters", filters.toString())
            .encode()
            .toUriString();
    }

    private List<Invoice> parseInvoiceList(JsonNode response) {
        List<Invoice> invoices = new ArrayList<>();
        JsonNode data = response.path("data");
        
        if (data.isArray()) {
            data.forEach(node -> invoices.add(Invoice.fromJson(node)));
        }
        
        return invoices;
    }

    private List<InvoiceItem> parseInvoiceItems(JsonNode data) {
        List<InvoiceItem> items = new ArrayList<>();
        JsonNode itemsNode = data.path("items");
        
        if (itemsNode.isArray()) {
            itemsNode.forEach(item -> items.add(parseInvoiceItem(item)));
        }
        
        return items;
    }

    private InvoiceItem parseInvoiceItem(JsonNode item) {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setItemCode(getTextValue(item, "item_code"));
        invoiceItem.setDescription(getTextValue(item, "description"));
        invoiceItem.setQuantity(getBigDecimalValue(item, "qty"));
        invoiceItem.setRate(getBigDecimalValue(item, "rate"));
        invoiceItem.setAmount(getBigDecimalValue(item, "amount"));
        return invoiceItem;
    }

    private Map<String, Object> buildPaymentEntry(String invoiceId, Map<String, Object> paymentData, JsonNode invoice) {
        Map<String, Object> entry = new java.util.HashMap<>();
        
        entry.put("doctype", "Payment Entry");
        entry.put("payment_type", "Pay");
        entry.put("company", invoice.path("company").asText());
        entry.put("posting_date", java.time.LocalDate.now().toString());
        
        entry.put("party_type", "Supplier");
        entry.put("party", paymentData.getOrDefault("supplier", invoice.path("supplier").asText()));
        
        entry.put("paid_from", paymentData.getOrDefault("paidFrom", "Cash - H"));
        entry.put("paid_to", paymentData.getOrDefault("paidTo", "Creditors - H"));
        
        BigDecimal amount = getPaymentAmount(paymentData, invoice);
        entry.put("paid_amount", amount);
        
        Map<String, Object> reference = Map.of(
            "reference_doctype", "Purchase Invoice",
            "reference_name", invoiceId,
            "allocated_amount", amount
        );
        
        entry.put("references", Collections.singletonList(reference));
        
        if (paymentData.containsKey("exchangeRate")) {
            entry.put("source_exchange_rate", paymentData.get("exchangeRate"));
            entry.put("payment_currency", paymentData.getOrDefault("currency", invoice.path("currency").asText()));
        }
        
        return entry;
    }

    private BigDecimal getPaymentAmount(Map<String, Object> paymentData, JsonNode invoice) {
        return paymentData.containsKey("amount") 
            ? (BigDecimal) paymentData.get("amount") 
            : new BigDecimal(invoice.path("outstanding_amount").asText());
    }

    private void submitPaymentEntry(String entryName) {
        restTemplate.postForEntity(
            ERP_BASE_URL + "/api/resource/Payment Entry/" + entryName + "?run_method=submit",
            Util.createErpRequest(),
            JsonNode.class
        );
    }

    private JsonNode fetchInvoiceDetails(String invoiceId) {
        String url = ERP_BASE_URL + "/api/resource/Purchase Invoice/" + invoiceId;
        return restTemplate.exchange(url, HttpMethod.GET, Util.createErpRequest(), JsonNode.class)
            .getBody().path("data");
    }

    private String getTextValue(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    private BigDecimal getBigDecimalValue(JsonNode node, String field) {
        return node.has(field) ? new BigDecimal(node.get(field).asText()) : BigDecimal.ZERO;
    }
}