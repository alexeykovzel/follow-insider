import {Table} from "./elements.js";
import {fetchAllTrades} from "./trades.js";

$(document).ready(() => {
    let tradesTable = new Table("trades",
        ["Symbol", "Company", "Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [0.5, 1.2, 1.2, 1.2, 1, 1, 1, 1, 1],
        "<trade-filters></trade-filters>"
    );
    $(".content-wrapper").append(tradesTable.html);
    // reload trades from the server whenever a checkbox is checked
    $(".filters :checkbox").change(function () {
        fetchAllTrades(tradesTable, getCheckedTypes());
    });
    // fetch all recent buy trades
    fetchAllTrades(tradesTable, ["Buy"]);
});

function getCheckedTypes() {
    return $('input[type=checkbox]:checked').map(function () {
        return $(this).attr('name');
    }).get();
}