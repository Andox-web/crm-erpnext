package mg.ando.erpnext.dto;

import java.util.List;

public class ErpQuoteResponse {
    private List<ErpQuote> data;

    public List<ErpQuote> getData() {
        return data;
    }

    public void setData(List<ErpQuote> data) {
        this.data = data;
    }
    
}

