import {Dashboard, InfoBlock, Table, LineGraph} from './elements.js';
import {fetchStockTrades} from './trades.js';
import {Tab, initTabs} from "./tabs.js";
import {initScore} from "./rating.js";
import * as Utils from "./utils.js";

class Stock {
    constructor(name, symbol, description, keyPoints, lastActive, efficiency, trend, overall) {
        this.name = name;
        this.symbol = symbol;
        this.description = description;
        this.keyPoints = keyPoints;
        this.lastActive = lastActive;
        this.efficiency = efficiency;
        this.trend = trend;
        this.overall = overall;
    }
}

class Insider {
    constructor(name, positions, totalShares, lastActive) {
        this.name = name;
        this.positions = positions;
        this.totalShares = totalShares;
        this.lastActive = lastActive;
    }
}

$(document).ready(() => {
    // toggle side panel if its arrow is clicked
    $("#panel-arrow").on("click", () => toggleSidePanel());
    // get stock symbol from the url
    let symbol = Utils.getLastUrlSegment();
    // fetch and fill its data into the page
    fetchStock(symbol);
});

function fetchStock(symbol) {
    $.ajax({
        type: "GET",
        url: `${location.origin}/stocks/${symbol}/info`,
        success: (data) => {
            let stock = Object.assign(new Stock(), data);
            initStock(stock);
        },
        error: (error) => {
            console.log("[ERROR] " + error.responseText);
            // TODO: Remove before prod.
            initStock(mockStock());
        },
    });
}

function fetchInsiders(table, symbol) {
    table.reset();
    $.ajax({
        type: "GET",
        url: `${location.origin}/stocks/${symbol}/insiders`,
        success: (data) => {
            let insiders = data.map(obj => Object.assign(new Insider(), obj));
            addInsidersToTable(table, insiders);
            table.initGrid();
        },
        error: (error) => console.log("[ERROR] " + error.responseText),
    });
}

function initStock(stock) {
    fillSidePanel(stock);
    let dashboard = new Dashboard();

    // add key points (if exist)
    let keyPoints = stock.keyPoints.filter(p => p);
    if (keyPoints.length > 0) {
        let keyPointsVal = keyPoints.map(point => "<p>- " + point + "</p>").join("");
        let keyPointsBlock = new InfoBlock("key-points", "Key Points", keyPointsVal);
        dashboard.blocks.push(keyPointsBlock);
    }
    // add description (if exists)
    if (stock.description) {
        let descriptionBlock = new InfoBlock("desc", "Description", "<p>" + stock.description + "</p>");
        dashboard.blocks.push(descriptionBlock);
    }
    // define table with insider information
    let insidersTable = new Table("insiders",
        ["Name", "Position", "Shares Total", "Last Active"],
        [1, 2, 1, 1]
    );
    // define table with insider trades
    let tradesTable = new Table("trades",
        ["Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [1.2, 1.2, 1, 1, 1, 1, 1],
        "<trade-filters></trade-filters>"
    );
    // add line graph
    let lineGraph = new LineGraph("linegraph0", "Shares per Transaction", ["Date", "Shares"]);
    dashboard.blocks.push(lineGraph);

    initTabs([
        new Tab("Dashboard", dashboard.html, () => {
            // render graphs with input data
            lineGraph.init(() => {
                fetchTradePoints(stock.symbol, "5M", (points) => {
                    lineGraph.draw(points)
                    dashboard.align();
                });
                // for testing
                // lineGraph.draw(mockTradePoints());
                // dashboard.align();
            });
            // align dashboard blocks
            $(window).resize(() => {
                lineGraph.resize();
                dashboard.align();
            });
            dashboard.align();
        }),
        new Tab("Insiders", insidersTable.html, () => {
            fetchInsiders(insidersTable, stock.symbol)
        }),
        new Tab("Trades", tradesTable.html, () => {
            // addTradesToTable(tradesTable, mockTrades(20), false)
            // tradesTable.initGrid();
            fetchStockTrades(tradesTable, stock.symbol, ["Buy"]);
        })
    ]);
}

function fetchTradePoints(symbol, range, draw) {
    $.ajax({
        type: "GET",
        url: `${location.origin}/stocks/${symbol}/trade-points?range=${range}`,
        success: (data) => {
            console.log(JSON.stringify(data));
            let points = [];
            data.forEach(obj => {
                let point = [Utils.formatDate(obj["date"]), obj["shareCount"]];
                points.push(point);
            });
            draw(points);
        },
        error: (error) => console.log("[ERROR] " + error.responseText),
    })
}

function addInsidersToTable(table, insiders) {
    table.addAll(insiders.map(insider => `<tr>
        <td>${insider.name}</td>
        <td>${insider.positions}</td>
        <td>${Utils.formatNumber(insider.totalShares)}</td>
        <td>${Utils.formatDate(insider.lastActive)}</td>
    </tr>`));
}

function fillSidePanel(stock) {
    if (!$(".s-info").length) return;
    $("#s-name").text(stock.name + " (" + stock.symbol + ")");
    $("#last-active").text("Last active: " + (Utils.formatDate(stock.lastActive) || "-"));

    // init stock rating
    initScore($("#efficiency-score"), stock.efficiency);
    initScore($("#trend-score"), stock.trend);
    initScore($("#overall-score"), stock.overall);
}

function mockStock() {
    let description = "Intel is best known for developing the microprocessors found in most of the world's " +
        "personal computers. The multinational technology company is also the world's largest manufacturer by " +
        "revenue of semiconductor chips, a product used in most of the world's electronic devices.";

    let keyPoints = [
        "lowest activity in 5 years",
        "2 days ago, Steven Jobs bought shares for $2.0M",
        "Average insider return: 25% per year"
    ];

    return new Stock("Intel Corporation", "INTC", description, keyPoints,
        "3 Aug, 2022", 4, 6, 9);
}

function mockInsiders() {
    return [
        new Insider("Steve Jobs0", ["CEO"], 2000, "23 Aug, 2022"),
        new Insider("Steve Jobs1", ["10% Owner", "CTO"], 2312322, "24 Aug, 2022"),
        new Insider("Steve Jobs2", ["10% Owner", "Chief Technical Officer"], 3298326382, "20 Aug, 2022"),
        new Insider("Steve Jobs3", ["10% Owner"], 2, "01 Jan, 2001"),
    ];
}

function mockTradePoints() {
    return [[10, 20], [15, 25], [20, 20], [25, 50], [30, 20], [32, 20], [35, 25], [50, 5]];
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