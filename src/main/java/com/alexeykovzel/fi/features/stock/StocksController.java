package com.alexeykovzel.fi.features.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {

    @GetMapping("/{stock}")
    public StockView getStock(@PathVariable String stock) {
        // TODO: Return stock view.
        return new StockView();
    }
}
