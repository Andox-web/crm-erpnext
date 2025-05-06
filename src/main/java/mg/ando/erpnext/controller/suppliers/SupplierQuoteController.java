package mg.ando.erpnext.controller.suppliers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.ando.erpnext.auth.annotation.RequireERPAuth;
import mg.ando.erpnext.dto.Supplier;
import mg.ando.erpnext.dto.SupplierQuote;
import mg.ando.erpnext.dto.UpdatePriceRequest;
import mg.ando.erpnext.service.SupplierQuoteService;
import mg.ando.erpnext.service.SupplierService;

@Controller
@RequestMapping("/suppliers/{supplierId}/quotes")
public class SupplierQuoteController {

    
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierQuoteService supplierQuoteService;

    @GetMapping("/{rfqId}/list")
    @RequireERPAuth
    public String listSupplierQuotes(
            @PathVariable String rfqId,
            @PathVariable String supplierId,
            @RequestParam(required = false) String status,
            Model model) {
        
        try {
            // 1. Récupérer les infos du fournisseur
            Supplier supplier =  supplierService.getSupplierDetails(supplierId);
            
            // 2. Récupérer les devis
            List<SupplierQuote> quotes = supplierQuoteService.getSupplierQuotes(rfqId, status);
            
            // 3. Ajouter au modèle
            model.addAttribute("supplier", supplier);
            model.addAttribute("quotes", quotes);
            model.addAttribute("statusFilter", status);
            model.addAttribute("title", "Devis fournisseur: " + supplier.getSupplierName());
            
            return "suppliers/quote";
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des devis", e);
        }
    }

    @PostMapping("/{quoteId}/update-price")
    @ResponseBody
    @RequireERPAuth
    public ResponseEntity<Map<String, Object>> updateQuotePrice(
            @PathVariable String supplierId,
            @PathVariable String quoteId,
            @RequestBody List<UpdatePriceRequest> requests) {
    
        Map<String, Object> response = new HashMap<>();
        try {
            response = supplierQuoteService.updateAndValidatePrice(quoteId, requests); 
            if ((Boolean)response.get("success")) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{quoteId}/items")
    public ResponseEntity<?> getQuoteItems(
        @PathVariable String supplierId,
        @PathVariable String quoteId) {

        try {
           return ResponseEntity.ok(supplierQuoteService.getQuoteItems(quoteId));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des items pour le devis {}"+ quoteId);
            return ResponseEntity.internalServerError().body("Erreur serveur"+e.getMessage());
        }
    }
}