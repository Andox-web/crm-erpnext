package mg.ando.erpnext.service;

import java.util.List;

import mg.ando.erpnext.dto.Supplier;

public interface SupplierService {
    public List<Supplier> getAllSuppliers();
    public Supplier getSupplierDetails(String supplierId);
}