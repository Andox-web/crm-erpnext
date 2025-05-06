package mg.ando.erpnext.service;

import com.fasterxml.jackson.databind.JsonNode;
import mg.ando.erpnext.dto.Invoice;
import mg.ando.erpnext.dto.InvoiceItem;
import mg.ando.erpnext.dto.PaymentData;
import mg.ando.erpnext.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    public JsonNode createPayment(String invoiceId, PaymentData paymentData) {
        JsonNode invoice = fetchInvoiceDetails(invoiceId);
        Map<String, Object> paymentEntry = buildPaymentEntry(invoiceId, paymentData, invoice);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            ERP_BASE_URL + "/api/resource/Payment Entry",
            HttpMethod.POST,
            Util.createErpRequestWithBody(paymentEntry),
            JsonNode.class
        );
        
        submitPaymentEntry(response.getBody().path("data").path("name").asText());
        return response.getBody();
    }

    private String buildInvoiceUrl(String status) {
        List<String> filters = new ArrayList<>();
        
        if (status != null) {
            filters.add("[\"status\",\"=\",\"" + status + "\"]");
        }

        return ERP_BASE_URL + "/api/resource/Purchase Invoice?fields=[\"name\",\"posting_date\",\"due_date\",\"status\",\"grand_total\",\"currency\",\"supplier\",\"supplier_name\",\"company\",\"outstanding_amount\"]&filters="+filters.toString();
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

    private Map<String, Object> buildPaymentEntry(String invoiceId, PaymentData data,JsonNode invoice) {
        Map<String, Object> entry = new HashMap<>();
        
        // Récupérer les valeurs depuis la facture si elles ne sont pas fournies dans le DTO
        String supplier = data.getSupplier() != null ? data.getSupplier() : invoice.get("supplier").asText();
        BigDecimal amount = data.getAmount() != null ? data.getAmount() : new BigDecimal(invoice.get("outstanding_amount").asText());
        String currency = data.getCurrency() != null ? data.getCurrency() : invoice.get("currency").asText();
    
        entry.put("doctype", "Payment Entry");
        entry.put("payment_type", "Pay");
        entry.put("company", invoice.get("company").asText()); // Récupéré depuis la facture
        entry.put("posting_date", LocalDate.now().toString());
    
        entry.put("party_type", "Supplier");
        entry.put("party", supplier);
    
        // Comptes (tu peux aussi les récupérer dynamiquement si tu veux plus de souplesse)
        entry.put("paid_from", data.getPaidFrom() != null ? data.getPaidFrom() : "Cash - H");
        entry.put("paid_to", data.getPaidTo() != null ? data.getPaidTo() : "Creditors - H");
    
        entry.put("paid_amount", amount);
        entry.put("received_amount", data.getExchangeRate() != null ? amount.multiply(data.getExchangeRate()) : amount);
    
        // Références
        Map<String, Object> reference = new HashMap<>();
        reference.put("reference_doctype", "Purchase Invoice");
        reference.put("reference_name", invoiceId);
        reference.put("allocated_amount", amount);
        entry.put("references", List.of(reference));
    
        if (data.getExchangeRate() != null) {
            entry.put("source_exchange_rate", data.getExchangeRate());
            entry.put("payment_currency", currency);
        }
    
        return entry;
    }

    private BigDecimal getPaymentAmount(Map<String, Object> paymentData, JsonNode invoice) {
        return paymentData.containsKey("amount") 
            ? new BigDecimal(paymentData.get("amount").toString()) 
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