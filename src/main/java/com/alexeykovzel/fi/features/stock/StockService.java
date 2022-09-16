package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.company.CompanyRepository;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final AlphaVantageAPI alphaVantageAPI;
    private final CompanyRepository companyRepository;
    private final StockRecordRepository stockRecordRepository;

    @Transactional
    public void updateStockRecords() {
        stockRecordRepository.deleteAll();
        ProgressBar.execute("Updating stock records...", companyRepository.findAll(), company ->
                stockRecordRepository.saveAll(alphaVantageAPI.getStockRecords(company))
        );
    }
}
