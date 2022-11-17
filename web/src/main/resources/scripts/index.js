import {Table} from "/scripts/ui/data.js";
import {fetchAllTrades} from "/scripts/trades.js";

function ready(callback) {
    if (document.readyState !== "loading") callback();
    else document.addEventListener("DOMContentLoaded", callback);
}

ready(function () {
    // add table with insider trades
    let tradesTable = new Table("trades",
        ["Symbol", "Company", "Insider", "Position", "Type", "Price", "Shares", "Total", "Date"],
        [0.5, 1.2, 1.2, 1.2, 1, 1, 1, 1, 1],
        "<trade-filters></trade-filters>");

    let content = document.querySelector(".content-wrapper");
    content.innerHTML += tradesTable.html;

    // reload trades whenever a checkbox is checked
    document.querySelectorAll(".filters input[type=checkbox]").forEach(checkbox => {
        checkbox.addEventListener("change", () => {
            fetchAllTrades(tradesTable, getCheckedTypes());
        })
    });
    // fetch recent purchases
    fetchAllTrades(tradesTable, ["Buy"]);
});

function getCheckedTypes() {
    let checkboxes = document.querySelectorAll("input[type=checkbox]:checked");
    return [...checkboxes].map(checkbox => checkbox.getAttribute("name"));
}