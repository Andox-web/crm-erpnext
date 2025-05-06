package mg.ando.erpnext.controller.suppliers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import mg.ando.erpnext.auth.annotation.RequireERPAuth;
import mg.ando.erpnext.dto.ErpOrderResponse;
import mg.ando.erpnext.dto.ErpSupplier;
import mg.ando.erpnext.dto.OrderItem;
import mg.ando.erpnext.dto.Supplier;
import mg.ando.erpnext.dto.SupplierOrder;
import mg.ando.erpnext.service.SupplierOrderService;
import mg.ando.erpnext.service.SupplierService;
import mg.ando.erpnext.util.Util;

@Controller
@RequestMapping("/suppliers/{supplierId}/orders")
public class SupplierOrderController {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SupplierOrderService supplierOrderService;
    
    @GetMapping
    @RequireERPAuth
    public String listSupplierOrders(
            @PathVariable String supplierId,
            @RequestParam(required = false) String status,
            Model model) {

        try {
            Supplier supplier = supplierService.getSupplierDetails(supplierId);
            List<SupplierOrder> orders = supplierOrderService.getSupplierOrders(supplierId, status);

            model.addAttribute("supplier", supplier);
            model.addAttribute("orders", orders);
            model.addAttribute("statusFilter", status);
            model.addAttribute("title", "Commandes fournisseur: " + supplier.getSupplierName());

            return "suppliers/order";
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes", e);
        }
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> getOrderItems(
        @PathVariable String supplierId,
        @PathVariable String orderId) {

        try {
            String fields = "[\"items\"]";
            String erpUrl = ERP_BASE_URL + "/api/resource/Purchase Order/" + orderId + "?fields=" + fields;

            ResponseEntity<ErpOrderItemResponse> response = restTemplate.exchange(
                erpUrl,
                HttpMethod.GET,
                Util.createErpRequest(),
                ErpOrderItemResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<OrderItem> items = response.getBody().getData().getItems();
                return ResponseEntity.ok(items);
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun item trouvé");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }
    
    public static class ErpOrderItemResponse {
        private DataWrapper data;
        // getters/setters
        
        public static class DataWrapper {
            private List<OrderItem> items;
            // getters/setters

            public List<OrderItem> getItems() {
                return items;
            }

            public void setItems(List<OrderItem> items) {
                this.items = items;
            }
            
        }

        public DataWrapper getData() {
            return data;
        }

        public void setData(DataWrapper data) {
            this.data = data;
        }
        
    }
}