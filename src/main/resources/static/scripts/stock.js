import {initScore} from "./rating.js";

window.insertTestStock = insertTestStock;
window.fetchStock = fetchStock;

class Stock {
    constructor(company, symbol, lastActive, description, efficiency, liquidity) {
        this.company = company;
        this.symbol = symbol;
        this.lastActive = lastActive;
        this.description = description;
        this.efficiency = efficiency;
        this.liquidity = liquidity;
    }
}

function fetchStock(symbol) {
    $.ajax({
        type: "GET",
        url: location.origin + "stocks/" + symbol,
        success: (data) => {
            let stock = Object.assign(data, new Stock());
            insertStock(stock);
        },
        error: (error) => console.log('[ERROR] ' + error.responseText),
    });
}

function insertStock(stock) {
    if (!$(".s-info").length) return;
    $("#s-desc").text(stock.description);
    $("#s-name").text(stock.company + " (" + stock.symbol + ")");
    $("#last-active").text("Last active: " + stock.lastActive);
    initScore($("#efficiency-score"), stock.efficiency);
    initScore($("#liquidity-score"), stock.liquidity);
}

function insertTestStock() {
    insertStock(new Stock("Intel Corporation", "INTC", "3 Aug, 2022",
        "Intel is best known for developing the microprocessors found "
        + "in most of the world's personal computers. The multinational "
        + "technology company is also the world's largest manufacturer "
        + "by revenue of semiconductor chips, a product used in most of "
        + "the world's electronic devices.", 4, 6));
}