import {TRADE_COLORS} from "/scripts/helpers/constants.js";
import * as Utils from "/scripts/helpers/utils.js";

let storedTrades = {};

class Trade {
    constructor(id, symbol, company, insiders, type, sharePrice, shareCount, leftShares, date) {
        this.id = id;
        this.symbol = symbol;
        this.company = company;
        this.insiders = insiders;
        this.type = type;
        this.sharePrice = sharePrice;
        this.shareCount = shareCount;
        this.leftShares = leftShares;
        this.date = date;
    }
}

class Insider {
    constructor(name, positions) {
        this.name = name;
        this.positions = positions;
    }
}

export function fetchTestStockTrades(table) {
    addTradesToTable(table, mockTrades(20), false)
    table.initGrid();
}

export function fetchStockTrades(table, symbol, types) {
    return fetchTrades(table, `/stocks/${symbol}/trades`, types, false)
}

export function fetchAllTrades(table, types) {
    return fetchTrades(table, "/trades/recent", types, true)
}

function fetchTrades(table, url, types, withStock) {
    table.reset();
    let typesParam = types ? types.join(",") : "";
    Utils.fetchJson(location.origin + `${url}?types=${typesParam}`, (data) => {
        let trades = data.map(obj => Object.assign(new Trade(), obj));
        if (trades.length === 0) {
            Utils.showErrorToast("No trades found");
        }
        addTradesToTable(table, trades, withStock);
        table.initGrid();
    });
}

function mockTrades(number) {
    let trades = [];
    for (let i = 0; i < number; i++) {
        let insider = new Insider("Mega Super Fond", ["CEO", "Director"]);
        let insiders = Array(5).fill(insider);
        let trade = new Trade(i, "INTC", "Intel Corporation", insiders, "Buy",
            20, 100_000, 200_000, "2022/01/01");
        trades.push(trade);
    }
    return trades;
}

function addTradesToTable(table, trades, withStock) {
    let defaultCell = "<scan style='color: #bbb'>Undefined</scan>";
    table.addAll(trades.map(trade => {
        storedTrades[trade.id] = trade;

        // get 1-st insider + "others" tail
        let others = trade.insiders.length - 1;
        let insiderVal = trade.insiders[0].name + ((others > 0) ? `<p class="insider-tail" 
                onclick="showOthers(${trade.id})">, and ${others} others</p>` : "");

        // get unique insider positions
        let positions = Utils.uniqueMerge(trade.insiders.map(insider => insider.positions));
        let positionVal = (positions.length === 0) ? defaultCell : positions.join(", ");

        // get other column values
        let colorStyle = "color: " + TRADE_COLORS[trade.type];
        let priceVal = (trade.sharePrice !== 0) ? Utils.formatMoney(trade.sharePrice) : "-";
        let sharesVal = Utils.formatNumber(trade.shareCount);
        let totalVal = Utils.formatNumber(trade.leftShares);
        let dateVal = Utils.formatDate(trade.date);

        // reference to the stock page
        let stockRef = "/stocks/" + (trade.symbol || "").toLowerCase();

        return `
            <tr id="trade-${trade.id}">
                ${withStock ? `<td onclick="location.assign("${stockRef}")" class="link">${trade.symbol}</td>` : ""}
                ${withStock ? `<td> ${trade.company}</td>` : ""}
                <td class="insider-cell">${insiderVal}</td>
                <td>${positionVal}</td>
                <td style="${colorStyle}">${trade.type}</td>
                <td>${priceVal}</td>
                <td>${sharesVal}</td>
                <td>${totalVal}</td>
                <td>${dateVal}</td>
            </tr>`;
    }));
}

function showOthers(id) {
    let insiders = storedTrades[id].insiders;
    let insiderVal = insiders.map(insider => `<p>${insider.name}</p>`).join("");
    let cell = document.querySelector(`#trade-${id} .insider-cell`);
    cell.style.gap = "15px";
    cell.innerHTML = insiderVal;
}