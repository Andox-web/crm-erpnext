package mg.ando.erpnext.controller.suppliers;

import mg.ando.erpnext.dto.Rfq;
import mg.ando.erpnext.dto.RfqItem;
import mg.ando.erpnext.service.SupplierRfqService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/suppliers")
public class SupplierRfqController {

    private final SupplierRfqService rfqService;

    public SupplierRfqController(SupplierRfqService rfqService) {
        this.rfqService = rfqService;
    }

    @GetMapping("/{supplierId}/request-quotes")
    public String listRfqs(
        @PathVariable String supplierId,
        @RequestParam(required = false) String status,
        Model model
    ) {
        List<Rfq> rfqs = rfqService.getAllRfqsForSupplier(supplierId, status);
        model.addAttribute("rfqs", rfqs);
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("statusFilter", status);
        return "suppliers/request-quotes";
    }

    @GetMapping("/rfqs/{rfqId}/items")
    @ResponseBody
    public ResponseEntity<?> getRfqItems(@PathVariable String rfqId) {
        try {
            List<RfqItem> items = rfqService.getRfqItems(rfqId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "error", "Erreur technique",
                    "details", e.getMessage()
                ));
        }
    }
}