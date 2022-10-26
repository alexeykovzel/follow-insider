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

function addTradesToTable(table, trades, withStock) {
    let defaultCell = "<scan style='color: #bbb'>Undefined</scan>";
    table.addAll(trades.map(trade => {
        storedTrades[trade.id] = trade;
        let row = document.createElement("tr");
        row.id = "trade-" + trade.id;

        // append stock information
        if (withStock) {
            let stockRef = "/stocks/" + (trade.symbol || "").toLowerCase();
            let symbolCell = document.createElement("td");
            let companyCell = document.createElement("td");
            companyCell.innerText = trade.company;
            symbolCell.innerText = trade.symbol;
            symbolCell.classList.add("link");
            symbolCell.onclick = () => location.assign(stockRef);
            row.append(symbolCell, companyCell);
        }

        // append the 1-st insider
        let insiderCell = document.createElement("td");
        insiderCell.classList.add("col");
        insiderCell.innerText += trade.insiders[0].name;
        row.appendChild(insiderCell);

        // append others
        let othersCount = trade.insiders.length - 1;
        if (othersCount > 0) {
            let others = document.createElement("p");
            insiderCell.appendChild(others);
            others.classList.add("others")
            others.innerText = `and ${othersCount} others`;
            others.onclick = () => {
                others.remove();
                insiderCell.style.gap = "15px";
                for (let i = 1; i < trade.insiders.length; i++) {
                    let p = document.createElement("p");
                    p.innerText = trade.insiders[i].name;
                    insiderCell.appendChild(p);
                }
            };
        }

        // add other column values
        let positions = Utils.uniqueMerge(trade.insiders.map(insider => insider.positions));
        let positionVal = (positions.length === 0) ? defaultCell : positions.join(", ");
        let colorStyle = "color: " + TRADE_COLORS[trade.type];
        let priceVal = (trade.sharePrice !== 0) ? Utils.formatMoney(trade.sharePrice) : "-";
        let sharesVal = Utils.formatNumber(trade.shareCount);
        let totalVal = Utils.formatNumber(trade.leftShares);
        let dateVal = Utils.formatDate(trade.date);

        row.insertAdjacentHTML('beforeend', `
            <td>${positionVal}</td>
            <td style="${colorStyle}">${trade.type}</td>
            <td>${priceVal}</td>
            <td>${sharesVal}</td>
            <td>${totalVal}</td>
            <td>${dateVal}</td>`);

        return row;
    }));
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