package mg.ando.erpnext.controller.suppliers;

import mg.ando.erpnext.dto.Supplier;
import mg.ando.erpnext.service.SupplierService;
import mg.ando.erpnext.auth.annotation.RequireERPAuth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @RequireERPAuth
    public String listSuppliers(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("title", "Gestion des Fournisseurs");
        return "suppliers/list";
    }
}