import {Dashboard, InfoBlock, Table, ScatterChart} from './elements.js';
import {fetchStockTrades} from "./trades.js";
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
    document.title = `${stock.name} (${stock.symbol}) - FollowInsider`;
    fillSidePanel(stock);

    let dashboard = new Dashboard();
    let graph0 = new ScatterChart("insider-buying", "Insider Buying", ["Date", "Shares"], () => {
        handleTimeRanges(() => {
            let range = $("#" + graph0.blockId + " :checked").prop("name");
            fetchTradePoints(stock.symbol, range, ["Buy"], (points) => graph0.draw(points));
        });
    });
    dashboard.blocks.push(graph0);

    // for testing
    let graph1 = new ScatterChart("insider-buying-2", "Insider Buying", ["Date", "Shares"], () => {
        graph1.draw(mockTradePoints());
        dashboard.align();
    });

    // add key points (if exist)
    let keyPoints = stock.keyPoints.filter(p => p);
    if (keyPoints.length > 0) {
        let keyPointsVal = keyPoints.map(point => "<p>- " + point + "</p>").join("");
        let keyPointsBlock = new InfoBlock("key-points", "Key Points", keyPointsVal);
        dashboard.blocks.push(keyPointsBlock);
    }
    dashboard.blocks.push(graph1);
    // add description (if exists)
    if (stock.description) {
        let descriptionBlock = new InfoBlock("desc", "Description", "<p>" + stock.description + "</p>");
        dashboard.blocks.push(descriptionBlock);
    }
    // define insider table columns
    let insidersTable = new Table("insiders",
        ["Name", "Position", "Shares Total", "Last Active"],
        [1, 2, 1, 1]
    );
    // define trade columns
    let tradesTable = new Table("trades",
        ["Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [1.2, 1.2, 1, 1, 1, 1, 1],
        "<trade-filters></trade-filters>"
    );

    initTabs([
        new Tab("Dashboard", dashboard.html, () => dashboard.init()),
        new Tab("Insiders", insidersTable.html, () => fetchInsiders(insidersTable, stock.symbol)),
        new Tab("Trades", tradesTable.html, () => fetchStockTrades(tradesTable, stock.symbol, ["Buy"]))
    ]);
}

function handleTimeRanges(loadData) {
    loadData();
    let checkboxes = $("time-ranges :checkbox");
    checkboxes.change(function () {
        if ($("time-ranges :checked").length > 0) {
            checkboxes.not(this).prop("checked", false);
            loadData();
        } else {
            $(this).prop("checked", true);
        }
    });
}

function fetchTradePoints(symbol, range, types, success) {
    $.ajax({
        type: "GET",
        url: `${location.origin}/stocks/${symbol}/trade-points?range=${range}&types=${types.join(",")}`,
        success: (data) => {
            let points = [];
            data.forEach(obj => {
                let point = [new Date(obj["date"]), obj["shareCount"]];
                points.push(point);
            });
            success(points);
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
    let points = [];
    for (let i = 0; i < 30; i++) {
        points.push([new Date(2022, 10, i), Math.pow(i + 2, 2)]);
    }
    return points;
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