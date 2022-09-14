import { mockTableTrades, addTradesToTable } from "./trades.js";
import { initScore } from "./rating.js";
import { Table } from './elements.js';

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

class Tab {
    constructor(html, init) {
        this.html = html;
        this.init = init;
    }
}

window.loadTab = loadTab;
window.togglePanel = togglePanel;

let tabs = [];

$(document).ready(() => {
    let tradesTable = new Table("trades",
        ["Symbol", "Company", "Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [1, 2, 2, 2, 1.5, 1.5, 1.5, 1.5, 1.5]
    );
    tabs = [
        new Tab(tradesTable.html,
            () => {
                mockTableTrades(20);
                tradesTable.setFractions();
            }
        ),
        new Tab(`
            <p>Nothing yet..</p>
            `, () => { }
        )
    ];
    loadTab(0);
    initTestStock();
});

function loadTab(idx) {
    console.log("Loading tab: " + idx);

    // highlight chosen tab
    let links = $("#tabs a");
    links.each(function () {
        $(this).removeClass("chosen");
    });
    links.eq(idx).addClass("chosen");

    // load tab content
    $("#tab-content").html(tabs[idx].html);
    tabs[idx].init();
}

function fetchStockInfo(symbol, consumer) {
    $.ajax({
        type: "GET",
        url: location.origin + "stocks/" + symbol,
        success: (data) => {
            let stock = Object.assign(data, new Stock());
            consumer(stock);
        },
        error: (error) => console.log('[ERROR] ' + error.responseText),
    });
}

function initStock(stock) {
    $("#s-desc").text(stock.description);
    $("#s-name").text(stock.company + " (" + stock.symbol + ")");
    $("#last-active").text("Last active: " + stock.lastActive);
    initScore($("#efficiency-score"), stock.efficiency);
    initScore($("#liquidity-score"), stock.liquidity);
}

function initTestStock() {
    initStock(new Stock("Intel Corporation", "INTC", "3 Aug, 2022",
        "Intel is best known for developing the microprocessors found "
        + "in most of the world's personal computers. The multinational "
        + "technology company is also the world's largest manufacturer "
        + "by revenue of semiconductor chips, a product used in most of "
        + "the world's electronic devices.", 4, 6));
}

function togglePanel() {
    let info = $(".s-info");
    let closed = info.css("opacity") === "0";
    let width = closed ? "420px" : "80px";
    info.css({ "opacity": closed ? "1" : "0", "visibility": closed ? "visible" : "hidden" });
    $(".s-panel").css("width", width);
    $(".main").css("margin-left", width)
    $("#panel-arrow").css("transform", closed ? "rotate(0deg)" : "rotate(180deg)");
}