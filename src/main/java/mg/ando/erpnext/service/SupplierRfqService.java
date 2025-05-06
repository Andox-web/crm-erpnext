package mg.ando.erpnext.service;

import java.util.List;

import mg.ando.erpnext.dto.Rfq;
import mg.ando.erpnext.dto.RfqItem;

public interface SupplierRfqService {
    public List<Rfq> getAllRfqsForSupplier(String supplierId, String status);
    public List<RfqItem> getRfqItems(String rfqId);
}
