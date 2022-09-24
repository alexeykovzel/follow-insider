package com.alexeykovzel.fi.core;

import com.alexeykovzel.fi.core.stock.StockRepository;
import com.alexeykovzel.fi.core.insider.InsiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final StockRepository stockRepository;
    private final InsiderRepository insiderRepository;

    @GetMapping
    public ModelAndView sendQuery(@RequestParam("q") String query) {
        String symbol = stockRepository.findSymbolByName(query);
        if (symbol == null) {
            // TODO: Try to find the closest match.
        }
        return new ModelAndView("redirect:/stocks/" + symbol);
    }

    @GetMapping("/hints")
    public Collection<String> getSearchHints() {
        return stockRepository.findAllNames();
    }
}
