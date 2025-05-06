package mg.ando.erpnext.service;

import java.util.List;

import mg.ando.erpnext.dto.OrderItem;
import mg.ando.erpnext.dto.SupplierOrder;

public interface SupplierOrderService {
    public List<SupplierOrder> getSupplierOrders(String supplierId, String status);   
    public List<OrderItem> getSupplierOrdersItem(String orderId);
}
