import {Dashboard, InfoBlock, Table, LineGraph} from './elements.js';
import {mockTrades, addTradesToTable} from './trades.js';
import {Tab, initTabs} from "./tabs.js";
import {initScore} from "./rating.js";
import * as Utils from "./utils.js";

class Stock {
    constructor(company, symbol, insiders, lastActive, description, keyPoints, efficiency, liquidity, overall) {
        this.company = company;
        this.symbol = symbol;
        this.insiders = insiders;
        this.lastActive = lastActive;
        this.description = description;
        this.keyPoints = keyPoints;
        this.efficiency = efficiency;
        this.liquidity = liquidity;
        this.overall = overall;
    }
}

class Insider {
    constructor(name, positions, sharesTotal, lastActive) {
        this.name = name;
        this.positions = positions;
        this.sharesTotal = sharesTotal;
        this.lastActive = lastActive;
    }
}

$(document).ready(() => {

    // define table with insider trades
    let tradesTable = new Table("trades",
        ["Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [1.2, 1.2, 1, 1, 1, 1, 1]
    );

    // define table with insider information
    let insidersTable = new Table("insiders",
        ["Name", "Position", "Shares Total", "Last Active"],
        [1, 2, 1, 1]
    );

    // TODO: Fetch data from the server.

    // insert stock data
    let symbol = Utils.getLastUrlSegment();
    // let stock = mockStock()
    let stock = fetchStock(symbol);
    fillSidePanel(stock);

    // configure dashboard info blocks
    let keyPointsVal = stock.keyPoints.map(point => "<p>- " + point + "</p>").join("");
    let keyPoints = new InfoBlock("key-points", "Key Points", keyPointsVal);
    let description = new InfoBlock("desc", "Description", "<p>" + stock.description + "</p>");
    let lineGraph0 = new LineGraph("graph0", "Average Value per Buy");
    let dashboard = new Dashboard([lineGraph0, keyPoints, description]);

    initTabs([
        new Tab("Dashboard", dashboard.html, () => {
            // render graphs with input data
            lineGraph0.draw(["1 Jan", "1 Feb", "1 Mar", "1 Apr", "1 May"], [7, 8, 8, 9, 16], 1);
            // set block height in the grid (depends on block content)
            $(window).resize(() => dashboard.update());
            dashboard.update();
        }),
        new Tab("Insiders", insidersTable.html, () => {
            addInsidersToTable(insidersTable, stock.insiders);
            insidersTable.init();
        }),
        new Tab("Trades", tradesTable.html, () => {
            // fetchTrades($("#trades tbody"), ["Buy"]);
            addTradesToTable(tradesTable, mockTrades(20), false)
            tradesTable.init();
        })
    ]);

    // toggle side panel if arrow is clicked
    $("#panel-arrow").on("click", () => toggleSidePanel());
});

function addInsidersToTable(table, insiders) {
    table.addAll(insiders.map(insider => `<tr>
        <td>${insider.name}</td>
        <td>${insider.positions.join(", ")}</td>
        <td>${Utils.formatNumber(insider.sharesTotal)}</td>
        <td>${insider.lastActive}</td>
    </tr>`));
}

function fetchStock(symbol) {
    $.ajax({
        type: "GET",
        url: location.origin + "stocks/" + symbol,
        success: (data) => {
            let stock = Object.assign(data, new Stock());
            fillSidePanel(stock);
        },
        error: (error) => console.log('[ERROR] ' + error.responseText),
    });
}

function fillSidePanel(stock) {
    if (!$(".s-info").length) return;
    $("#s-name").text(stock.company + " (" + stock.symbol + ")");
    $("#last-active").text("Last active: " + stock.lastActive);

    // init stock rating
    initScore($("#efficiency-score"), stock.efficiency);
    initScore($("#liquidity-score"), stock.liquidity);
    initScore($("#overall-score"), stock.overall);
}

function mockStock() {
    let description = "Intel is best known for developing the microprocessors found in most of the world's " +
        "personal computers. The multinational technology company is also the world's largest manufacturer by " +
        "revenue of semiconductor chips, a product used in most of the world's electronic devices.";
    let insiders = [
        new Insider("Steve Jobs0", ["CEO"], 2000, "23 Aug, 2022"),
        new Insider("Steve Jobs1", ["10% Owner", "CTO"], 2312322, "24 Aug, 2022"),
        new Insider("Steve Jobs2", ["10% Owner", "Chief Technical Officer"], 3298326382, "20 Aug, 2022"),
        new Insider("Steve Jobs3", ["10% Owner"], 2, "01 Jan, 2001"),
    ];
    let keyPoints = [
        "lowest activity in 5 years",
        "Steven Jobs bought shares for $2.0M 2 days go",
        "Average insider return: 25% per year"
    ];
    return new Stock("Intel Corporation", "INTC", insiders,
        "3 Aug, 2022", description, keyPoints, 4, 6, 9);
}

function toggleSidePanel() {
    let info = $(".s-info");
    let closed = info.css("opacity") === "0";
    let width = closed ? "420px" : "80px";
    $(".s-panel").css("width", width);
    $(".main").css("margin-left", width)
    $("#panel-arrow").css("transform", closed ? "rotate(0deg)" : "rotate(180deg)");
    info.css({"opacity": closed ? "1" : "0", "visibility": closed ? "visible" : "hidden"});
}