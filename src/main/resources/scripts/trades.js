import * as Utils from "./utils.js";

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

let typeColors = {
    'Buy': 'var(--buy)',
    'Sell': 'var(--sell)',
    'Grant': 'var(--grant)',
    'Options': 'var(--options)',
    'Taxes': 'var(--taxes)',
    'Other': 'var(--other)'
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
    // send request to retrieve trades from the server
    $.ajax({
        type: "GET",
        url: location.origin + url + "?types=" + types.join(','),
        // if success, add trades to the table
        success: (data) => {
            let trades = data.map(obj => Object.assign(new Trade(), obj));
            addTradesToTable(table, trades, withStock);
            table.initGrid();
        },
        // otherwise, print an error message
        error: (error) => console.log("[ERROR] " + error.responseText),
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
    let defaultCell = '<scan style="color: #bbb">Undefined</scan>';
    table.addAll(trades.map(trade => {
        storedTrades[trade.id] = trade;

        // get 1-st insider + "others" tail
        let others = trade.insiders.length - 1;
        let othersTail = (others > 0) ? `, <p class="insider-tail">and ${others} others</p>` : "";
        let insiderVal = trade.insiders[0].name + othersTail;

        // get unique insider positions
        let positions = Utils.uniqueMerge(trade.insiders.map(insider => insider.positions));
        let positionVal = (positions.length === 0) ? defaultCell : positions.join(", ");

        // get other column values
        let colorStyle = "color: " + typeColors[trade.type];
        let priceVal = (trade.sharePrice !== 0) ? Utils.formatMoney(trade.sharePrice) : "-";
        let sharesVal = Utils.formatNumber(trade.shareCount);
        let totalVal = Utils.formatNumber(trade.leftShares);
        let dateVal = Utils.formatDate(trade.date);

        // reference to the stock page
        let stockRef = '/stocks/' + trade.symbol.toLowerCase();

        // build table row element
        let tradeRow = $(`
            <tr id="trade-${trade.id}"">
                ${withStock ? `<td onclick="location.assign('${stockRef}')" class="link">${trade.symbol}</td>` : ""}
                ${withStock ? `<td> ${trade.company}</td>` : ""}
                <td class="insider-cell">${insiderVal}</td>
                <td>${positionVal}</td>
                <td style="${colorStyle}">${trade.type}</td>
                <td>${priceVal}</td>
                <td>${sharesVal}</td>
                <td>${totalVal}</td>
                <td>${dateVal}</td>
            </tr>
        `);

        // show all insiders if "and ** others" is clicked
        tradeRow.find(".insider-tail").on("click", () => showAllInsiders(trade.id));
        return tradeRow;
    }));
}

function showAllInsiders(id) {
    let insiders = storedTrades[id].insiders;
    let insiderVal = insiders.map(insider => `<p>${insider.name}</p>`).join('');
    let cell = $(`#trade-${id} .insider-cell`);
    cell.css('gap', '15px');
    cell.html(insiderVal);
}