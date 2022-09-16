import {Table} from "./elements.js";
import * as Trades from "./trades.js";

/*
                <th>Symbol</th>
                <th>Company</th>
                <th>Insider</th>
                <th>Position</th>
                <th>Type</th>
                <th>Price</th>
                <th>Shares</th>
                <th>Total</th>
                <th>Date</th>
 */

$(document).ready(() =>  {
    handleFilters();
    let tradesTable = new Table("trades",
        ["Symbol", "Company", "Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [0.5, 1.2, 1.2, 1.2, 1, 1, 1, 1, 1]
    );
    $(".content-wrapper").append(tradesTable.html);
    Trades.addTradesToTable(tradesTable, Trades.mockTrades(20), true)
    tradesTable.init();
});

function handleFilters() {
    // reload trades from the server whenever a checkbox is checked
    $('.filters :checkbox').change(() => Trades.fetchTrades($("#trades tbody")));
}

function getCheckedTypes() {
    return $('input[type=checkbox]:checked').map(function () {
        return $(this).attr('name');
    }).get();
}