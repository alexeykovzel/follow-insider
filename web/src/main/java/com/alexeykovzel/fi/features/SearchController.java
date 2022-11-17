package com.alexeykovzel.fi.features;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final StockRepository stockRepository;

    @GetMapping
    public ModelAndView sendQuery(@RequestParam("q") String query) {
        query = query.trim().replaceAll("/[()]/g", "").toLowerCase();

        if (query.equals(""))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query");

        for (Stock stock : stockRepository.findAll()) {
            if (stock.getFullName().toLowerCase().contains(query))
                return new ModelAndView("redirect:/stocks/" + stock.getSymbol().toLowerCase());
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find a stock");
    }

    @GetMapping("/hints")
    public Collection<String> getSearchHints() {
        return stockRepository.findFullNames();
    }
}
