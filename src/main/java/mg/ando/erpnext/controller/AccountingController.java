package mg.ando.erpnext.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mg.ando.erpnext.util.Util;

@RestController
@RequestMapping("/accounting")
public class AccountingController {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public AccountingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<String>> listAccounts(
            @RequestParam String company,
            @RequestParam(required = false) String accountType // "from" ou "to"
    ) throws JsonProcessingException {

        // 1. Filtre obligatoire sur la compagnie
        List<List<Object>> filters = new ArrayList<>();
        filters.add(List.of("Account", "company", "=", company));

        // 2. Filtre selon le rôle du compte
        if ("from".equalsIgnoreCase(accountType)) {
            // Paid From → Cash ou Bank
            filters.add(List.of("Account", "account_type", "in", List.of("Cash", "Bank")));
        } else if ("to".equalsIgnoreCase(accountType)) {
            // Paid To → Payable
            filters.add(List.of("Account", "account_type", "=", "Payable"));
        }

        // 3. Sérialisation et encodage
        String fields  = "[\"name\"]";
        String filtersJson = mapper.writeValueAsString(filters);
        String filtersParam = filtersJson;

        String url = ERP_BASE_URL
            + "/api/resource/Account?fields=" + fields
            + "&filters=" + filtersParam;

        // 4. Requête ERPNext
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(Util.createErpRequest().getHeaders()),
            JsonNode.class
        );

        // 5. Extraction des noms de compte
        List<String> accounts = new ArrayList<>();
        for (JsonNode row : resp.getBody().get("data")) {
            accounts.add(row.get("name").asText());
        }
        return ResponseEntity.ok(accounts);
    }
}
