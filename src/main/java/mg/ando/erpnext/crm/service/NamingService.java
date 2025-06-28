package mg.ando.erpnext.crm.service;

import mg.ando.erpnext.crm.dto.SeriesDTO;
import mg.ando.erpnext.crm.repository.SeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
public class NamingService {
    
    private final SeriesRepository seriesRepository;
    
    // Cache pour les séries fréquemment utilisées
    private final Map<String, Integer> seriesCache = new HashMap<>();
    
    public NamingService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }
    
    @Transactional
    public String getNext(String seriesPattern) {
        // Nettoyer le pattern (ERPNext utilise des points comme marqueurs)
        String baseSeries = seriesPattern.replaceAll("\\.\\w+\\.", "");
        
        // Vérifier le cache
        if (!seriesCache.containsKey(baseSeries)) {
            initializeSeries(baseSeries);
        }
        
        // Incrémenter la valeur
        int nextValue = seriesCache.get(baseSeries) + 1;
        seriesCache.put(baseSeries, nextValue);
        
        // Mettre à jour la base (verrouillage optimiste)
        seriesRepository.incrementSeries(baseSeries);
        
        // Formater le résultat
        return formatSeries(seriesPattern, nextValue);
    }
    
    private void initializeSeries(String series) {
        SeriesDTO seriesDTO = seriesRepository.findById(series)
            .orElseGet(() -> {
                SeriesDTO newSeries = new SeriesDTO();
                newSeries.setName(series);
                newSeries.setCurrent(0);
                return seriesRepository.save(newSeries);
            });
        
        seriesCache.put(series, seriesDTO.getCurrent());
    }
    
    private String formatSeries(String pattern, int value) {
        return pattern
            .replace(".FY.", String.valueOf(Year.now().getValue()))
            .replace(".MM.", String.format("%02d", LocalDate.now().getMonthValue()))
            + String.format("%05d", value);
    }
}