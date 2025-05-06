package mg.ando.erpnext.service;

import java.util.List;
import java.util.Map;

import mg.ando.erpnext.dto.QuoteItem;
import mg.ando.erpnext.dto.SupplierQuote;
import mg.ando.erpnext.dto.UpdatePriceRequest;

public interface SupplierQuoteService {
    List<SupplierQuote> getSupplierQuotes(String rfqId, String status);
    List<QuoteItem> getQuoteItems(String quoteId);
    Map<String, Object> updateAndValidatePrice(String quoteId,List<UpdatePriceRequest> requests);
}
