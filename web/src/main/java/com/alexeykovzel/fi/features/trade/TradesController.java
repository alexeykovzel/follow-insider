package com.alexeykovzel.fi.features.trade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradesController {
    private final TradeRepository tradeRepository;

    @GetMapping("/recent")
    public Collection<Trade.View> getRecentTrades(@RequestParam(value = "types", required = false) List<String> types,
                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                  @RequestParam(value = "limit", defaultValue = "100") int limit) {
        Pageable paging = PageRequest.of(offset, limit, Sort.by("date").descending());
        return tradeRepository.findPagingViews(paging, TradeCode.ofTypes(types)).getContent();
    }
}
