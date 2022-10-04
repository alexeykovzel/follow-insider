package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.view.TradeView;
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
    private final TradeService tradeService;

    @GetMapping("/recent")
    public Collection<TradeView> getRecentTrades(@RequestParam(value = "types", required = false) List<String> types) {
        Pageable paging = PageRequest.of(0, 100, Sort.by("date").descending());
        if (types == null || types.isEmpty()) return tradeRepository.findRecent(paging).getContent();
        return tradeRepository.findRecent(tradeService.getCodesByTypes(types), paging).getContent();
    }
}
