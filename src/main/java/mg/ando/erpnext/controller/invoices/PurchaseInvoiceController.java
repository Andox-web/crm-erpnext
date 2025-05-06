package mg.ando.erpnext.controller.invoices;

import mg.ando.erpnext.dto.Invoice;
import mg.ando.erpnext.dto.InvoiceItem;
import mg.ando.erpnext.dto.PaymentData;
import mg.ando.erpnext.service.PurchaseInvoiceService;
import mg.ando.erpnext.auth.annotation.RequireERPAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/purchase-invoices")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService invoiceService;

    public PurchaseInvoiceController(PurchaseInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @RequireERPAuth
    public String listPurchaseInvoices(
        @RequestParam(required = false) String status,
        Model model
    ) {
        model.addAttribute("invoices", invoiceService.getInvoices(status));
        model.addAttribute("statusFilter", status);
        return "invoices/list";
    }

    @GetMapping("/{invoiceId}/items")
    @ResponseBody
    public ResponseEntity<?> getInvoiceItems(@PathVariable String invoiceId) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceItems(invoiceId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{invoiceId}/pay")
    @ResponseBody
    public ResponseEntity<?> createPayment(
        @PathVariable String invoiceId,
        @RequestBody PaymentData paymentData
    ) {
        try {
            validatePaymentData(paymentData);
            invoiceService.createPayment(invoiceId, paymentData);
            Map<String, Object> paymentResponse = Map.of(
                "success", true
            );
            return ResponseEntity.ok(paymentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur de traitement: " + e.getMessage()));
        }
    }

    private void validatePaymentData(PaymentData paymentData) {
        if (paymentData.getModeOfPayment()== null) {
            throw new IllegalArgumentException("Le mode de paiement est requis");
        }
    }
}