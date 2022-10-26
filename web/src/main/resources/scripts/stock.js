import {Dashboard, InfoBlock, ScatterChart} from "/scripts/ui/charts.js";
import {fetchStockTrades} from "/scripts/trades.js";
import {initTabs, Tab} from "/scripts/helpers/tabs.js";
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
    initStock(mockStock());
    // TODO: Fetch data from the server.
    // Utils.fetchJson(location.origin + `/stocks/${symbol}/info`, (data) => {
    //     let stock = Object.assign(new Stock(), data);
    //     initStock(stock);
    // });
}

function fetchInsiders(table, symbol) {
    table.reset();
    Utils.fetchJson(location.origin + `/stocks/${symbol}/insiders`, (data) => {
        let insiders = data.map(obj => Object.assign(new Insider(), obj));
        if (insiders.length === 0) {
            Utils.showErrorToast("No insiders found");
        }
        addInsidersToTable(table, insiders);
        table.initGrid();
    });
}

function fetchTradePoints(symbol, range, types, load) {
    let params = `range=${range}&types=${types.join(",")}`;
    Utils.fetchJson(location.origin + `/stocks/${symbol}/trade-points?${params}`, (data) => {
        let points = [];
        data.forEach(obj => {
            let date = new Date(obj["date"]);
            let point = [date, obj["shareCount"]];
            points.push(point);
        });
        load(points);
    });
}

function initStock(stock) {
    document.title = `${stock.name} (${stock.symbol}) | FollowInsider`;
    fillSidePanel(stock);

    let dashboard = buildDashboard(stock);

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

export function buildDashboard(stock) {
    let dashboard = new Dashboard();

    let graph0 = new ScatterChart("insider-buying", "Insider Buying", ["Date", "Shares"], () => {
        handleTimeRanges(() => {
            let range = document.querySelector(`#${graph0.blockId} :checked`).getAttribute("name");
            fetchTradePoints(stock.symbol, range, ["Buy"], (points) => graph0.draw(points));
        });
    });
    dashboard.blocks.push(graph0);

    // add key points (if exist)
    let keyPoints = stock.keyPoints.filter(p => p);
    if (keyPoints.length > 0) {
        let keyPointsVal = keyPoints.map(point => "<p>- " + point + "</p>").join("");
        let keyPointsBlock = new InfoBlock("key-points", "Key Points", keyPointsVal);
        dashboard.blocks.push(keyPointsBlock);
    }

    // for testing
    let graph1 = new ScatterChart("insider-buying-2", "Insider Buying", ["Date", "Shares"], () => {
        graph1.draw(mockTradePoints());
        dashboard.alignAll();
    });
    dashboard.blocks.push(graph1);

    // add description (if exists)
    if (stock.description) {
        let descriptionBlock = new InfoBlock("desc", "Description", "<p>" + stock.description + "</p>");
        dashboard.blocks.push(descriptionBlock);
    }
    return dashboard;
}

function handleTimeRanges(loadData) {
    loadData();
    let checkboxes = document.querySelectorAll("time-ranges input[type=checkbox]");
    checkboxes.forEach(checkbox => checkbox.onchange = () => {
        // disable manual unchecking
        if (!checkbox.checked) {
            checkbox.checked = true;
            return;
        }
        // change active checkbox and load data
        checkboxes.forEach(checkbox => checkbox.checked = false);
        checkbox.checked = true;
        loadData();
    });
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
    document.getElementById("s-name").innerText = stock.name + " (" + stock.symbol + ")";
    document.getElementById("last-active").innerText = "Last active: " + (Utils.formatDate(stock.lastActive) || "-");

    // initialize stock rating
    initScore(document.getElementById("efficiency-score"), stock.efficiency);
    initScore(document.getElementById("trend-score"), stock.trend);
    initScore(document.getElementById("overall-score"), stock.overall);
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

function mockTradePoints() {
    let points = [];
    for (let i = 0; i < 30; i++) {
        let date = new Date(2022, 10, i);
        points.push([date, Math.pow(i + 2, 2)]);
    }
    return points;
}