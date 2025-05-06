package mg.ando.erpnext.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import mg.ando.erpnext.dto.Invoice;
import mg.ando.erpnext.dto.InvoiceItem;

public interface PurchaseInvoiceService {
    public List<Invoice> getInvoices(String status);
    public List<InvoiceItem> getInvoiceItems(String invoiceId);
    public JsonNode createPayment(String invoiceId, Map<String, Object> paymentData);
}
