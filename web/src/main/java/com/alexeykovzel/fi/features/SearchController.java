package com.alexeykovzel.fi.features;

import com.alexeykovzel.fi.features.stock.StockRepository;
import com.alexeykovzel.fi.features.insider.InsiderRepository;
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
        // TODO: Try to find the closest match.

        // temporary solution, please don't judge
        int abbrIdx = query.indexOf("(");
        String symbol = (abbrIdx != -1)
                ? query.substring(abbrIdx + 1, query.indexOf(")")).toLowerCase()
                : "unidentified";

        return new ModelAndView("redirect:/stocks/" + symbol);
    }

    @GetMapping("/hints")
    public Collection<String> getSearchHints() {
        return stockRepository.findAllNames();
    }
}
