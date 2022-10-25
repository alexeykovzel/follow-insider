import {fetchStockTrades} from "/scripts/trades.js";
import {buildDashboard} from "/scripts/ui/dashboard.js";
import {Tab, initTabs} from "/scripts/helpers/tabs.js";
import {initScore} from "/scripts/rating.js";
import {Table} from "/scripts/ui/elements.js";
import * as Utils from "/scripts/helpers/utils.js";

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

function ready(callback) {
    if (document.readyState !== "loading") callback();
    else document.addEventListener("DOMContentLoaded", callback);
}

ready(function () {
    // toggle side panel if its arrow is clicked
    document.querySelector("#panel-arrow").onclick = () => toggleSidePanel();
    // get stock symbol from the url
    let symbol = Utils.getLastUrlSegment();
    // fetch and fill its data into the page
    fetchStock(symbol);
})

function fetchStock(symbol) {
    fetch(`./stocks/${symbol}/info`)
        .then(data => data.json())
        .then(data => {
            let stock = Object.assign(new Stock(), data);
            initStock(stock);
        })
        .catch((error) => {
            console.log("[ERROR] " + error.responseText);
            // TODO: Remove before prod.
            initStock(mockStock());
        });
}

function fetchInsiders(table, symbol) {
    table.reset();
    fetch(`./stocks/${symbol}/insiders`)
        .then(data => data.json())
        .then(data => {
            let insiders = data.map(obj => Object.assign(new Insider(), obj));
            addInsidersToTable(table, insiders);
            table.initGrid();
        })
        .catch((error) => console.log("[ERROR] " + error.responseText))
}

function initStock(stock) {
    document.title = `${stock.name} (${stock.symbol}) - FollowInsider`;
    fillSidePanel(stock);

    let dashboard = buildDashboard();

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

function addInsidersToTable(table, insiders) {
    table.addAll(insiders.map(insider => `<tr>
        <td>${insider.name}</td>
        <td>${insider.positions}</td>
        <td>${Utils.formatNumber(insider.totalShares)}</td>
        <td>${Utils.formatDate(insider.lastActive)}</td>
    </tr>`));
}

function fillSidePanel(stock) {
    if (!document.querySelectorAll(".s-info").length) return;
    document.querySelector("#s-name").innerText = stock.name + " (" + stock.symbol + ")";
    document.querySelector("#last-active").innerText = "Last active: " + (Utils.formatDate(stock.lastActive) || "-");

    // initialize stock rating
    initScore(document.querySelector("#efficiency-score"), stock.efficiency);
    initScore(document.querySelector("#trend-score"), stock.trend);
    initScore(document.querySelector("#overall-score"), stock.overall);
}

function toggleSidePanel() {
    let info = document.querySelector(".s-info");
    let closed = info.style.opacity === "0";
    let width = closed ? "420px" : "80px";
    document.querySelector(".s-panel").style.width = width;
    document.querySelector(".main").style.marginLeft = width;
    document.querySelector("#panel-arrow").style.transform = closed ? "rotate(0deg)" : "rotate(180deg)";
    Object.assign(info.style, {opacity: closed ? "1" : "0", visibility: closed ? "visible" : "hidden"});
}

function mockStock() {
    let description = "Intel is best known for developing the microprocessors found in most of the world's " +
        "personal computers. The multinational technology company is also the world's largest manufacturer by " +
        "revenue of semiconductor chips, a product used in most of the world's electronic devices.";

    let keyPoints = ["lowest activity in 5 years",
        "2 days ago, Steven Jobs bought shares for $2.0M",
        "Average insider return: 25% per year"];

    return new Stock("Intel Corporation", "INTC", description, keyPoints,
        "3 Aug, 2022", 4, 6, 9);
}

function mockInsiders() {
    return [
        new Insider("Steve Jobs0", ["CEO"], 2000, "23 Aug, 2022"),
        new Insider("Steve Jobs1", ["10% Owner", "CTO"], 2312322, "24 Aug, 2022"),
        new Insider("Steve Jobs2", ["10% Owner", "CFO"], 3298326382, "20 Aug, 2022"),
        new Insider("Steve Jobs3", ["10% Owner"], 2, "01 Jan, 2001"),
    ];
}