import {Dashboard, InfoBlock, Table, LineGraph} from './element.js';
import {mockTrades, fetchTrades} from './trade.js';
import {initScore} from "./rating.js";

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
    constructor(name, positions, lastActive) {
        this.name = name;
        this.positions = positions;
        this.lastActive = lastActive;
    }
}

$(document).ready(() => {

    // define table with insider trades
    let tradesTable = new Table("trades",
        ["Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [2, 2, 1.5, 1.5, 1.5, 1.5, 1.5]
    );

    // define table with insider information
    let insidersTable = new Table("insiders",
        ["Name", "Position", "Last Active"],
        [1, 1, 1]
    );

    // insert stock data
    let stock = mockStock()
    fillSidePanel(stock);

    // configure dashboard info blocks
    let keyPointsVal = stock.keyPoints.map(point => "<p>- " + point + "</p>").join("");
    let keyPoints = new InfoBlock("key-points", "Key Points", keyPointsVal);
    let description = new InfoBlock("desc", "Description", "<p>" + stock.description + "</p>");
    let lineGraph0 = new LineGraph("graph0", "Average Value per Buy");
    let lineGraph1 = new LineGraph("graph1", "Average Value per Buy");
    let lineGraph2 = new LineGraph("graph2", "Average Value per Buy");
    let dashboard = new Dashboard([lineGraph0, keyPoints, description, lineGraph1, lineGraph2]);

    initTabs([
        new Tab("Dashboard", dashboard.html, () => {
            // render graphs with input data
            lineGraph0.draw();
            lineGraph1.draw();
            lineGraph2.draw();
            // set block height in the grid (depends on block content)
            dashboard.blocks.forEach(block => block.align());
        }),
        new Tab("Insiders", insidersTable.html, () => {
            addInsidersToTable(insidersTable, stock.insiders);
            insidersTable.init();
        }),
        new Tab("Trades", tradesTable.html, () => {
            // fetchTrades($("#trades tbody"), ["Buy"]);
            addTradesToTable(tradesTable, mockTrades(20))
            tradesTable.init();
        })
    ]);

    // toggle side panel if arrow is clicked
    $("#panel-arrow").on("click", () => toggleSidePanel());
});

function addTradesToTable(table, trades) {
    table.addAll(trades.map(trade => `<tr>
        <tr id="trade-${trade.id}"">
        <td class="link">${trade.symbol}</td>
        <td> ${trade.company}</td>
        <td class="insider-cell">${insiderVal}</td>
        <td>${positionVal}</td>
        <td style="${colorStyle}">${trade.type}</td>
        <td>${priceVal}</td>
        <td>${sharesVal}</td>
        <td>${totalVal}</td>
        <td>${dateVal}</td>
    </tr>`));
}

function addInsidersToTable(table, insiders) {
    table.addAll(insiders.map(insider => `<tr>
        <td>${insider.name}</td>
        <td>${insider.positions}</td>
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
        new Insider("Steve Jobs0", "CEO", "23 Aug, 2022"),
        new Insider("Steve Jobs1", ["10% Owner", "CTO"], "24 Aug, 2022"),
        new Insider("Steve Jobs2", ["10% Owner", "CCO", "Chief Technical Officer"], "20 Aug, 2022"),
        new Insider("Steve Jobs3", "10% Owner", "01 Jan, 2001"),
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