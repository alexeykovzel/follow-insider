let storedTrades = {};

let testTrade = {
    company: "Intel Corporation",
    symbol: "INTC",
    sharePrice: 20,
    shareCount: 100000,
    leftShares: 200000,
    type: "Buy",
    date: "2022/01/01",
    insiders: Array(5).fill({
        name: 'Mega Super Fond',
        positions: ['CEO', 'Director']
    })
};

$(document).ready(() => {
    handleFilters();
    // fetchTrades();
    // addTradesToTable(Array(20).fill(testTrade));
});

function handleFilters() {
    // reload trades from the server if any checkbox is checked 
    $('.filters :checkbox').change(() => fetchTrades());
}

function fetchTrades() {
    resetTable($("#trades"))
    // send GET request to the server
    $.ajax({
        type: 'GET',
        url: `${location.origin}/trades?type=${getCheckedTypes().join(',')}`,
        // if success, add trades to the table
        success: (trades) => addTradesToTable(trades),
        // otherwise, print an error message
        error: (error) => console.log('[ERROR] ' + error.responseText),
    });
}

function addTradesToTable(trades) {
    let defaultCell = '<scan style="color: #bbb">Undefined</scan>';
    $("#loader").remove();
    let table = $("#trades");
    trades.forEach(trade => {
        storedTrades[trade['id']] = trade;
        let typeVal = trade['type'];

        // set insider value
        let othersNum = trade['insiders'].length - 1;
        let insiderVal = trade['insiders'][0]['name'] + ((othersNum > 0)
            ? `, <p class="insider-tail" onclick="showAllInsiders(${trade['id']})">and ${othersNum} others</p>`
            : '');

        // set position value
        let positions = [];
        trade['insiders'].forEach(insider => insider['positions'].forEach(position => positions.push(position)));
        let positionVal = (positions.length === 0) ? defaultCell : positions.filter(unique).join(', ');

        // set other values
        let priceVal = (trade['sharePrice'] !== 0) ? trade['sharePrice'].toFixed(1) + '$' : '-';
        let sharesVal = formatNumber(trade['shareCount']);
        let totalVal = formatNumber(trade['leftShares']);
        let dateVal = new Date(trade['date']).toLocaleDateString('en-US',
            { month: 'short', day: 'numeric', year: 'numeric' });

        let typeColor = {
            'Buy': 'var(--buy)',
            'Sell': 'var(--sell)',
            'Grant': 'var(--grant)',
            'Options': 'var(--options)',
            'Taxes': 'var(--taxes)',
            'Other': 'var(--other)'
        }[typeVal];

        table.append(`
                <tr class="trade-row" id="trade-${trade['id']}"">
                    <td class="link">${trade['symbol']}</td>
                    <td> ${trade['company']}</td>
                    <td class="insider">${insiderVal}</td>
                    <td>${positionVal}</td>
                    <td style="color: ${typeColor}">${typeVal}</td>
                    <td>${priceVal}</td>
                    <td>${sharesVal}</td>
                    <td>${totalVal}</td>
                    <td>${dateVal}</td>
                </tr>
            `);
    })
}

function resetTable(table) {
    // clear table data
    table.empty();
    // remove old loading animation (if exists)
    $("#loader").remove();
    // add new loading animation to the center
    table.after(`<div id="loader" class="center"><div class="lds-facebook"><div></div><div></div><div></div></div></div>`);
}

function getCheckedTypes() {
    return $('input[type=checkbox]:checked').map(function () {
        // define types by their "name" attribute
        return $(this).attr('name');
    }).get();
}

function showTradeInsiders(id) {
    let insiders = storedTrades[id]['insiders'];
    let insiderVal = insiders.map(insider => `<p>${insider['name']}</p>`).join('');
    let cell = $(`#trade-${id} .insider`);
    cell.css('gap', '15px');
    cell.html(insiderVal);
}

function formatNumber(num) {
    num = Math.abs(Number(num));
    if (num >= 1.0e+9) return (num / 1.0e+9).toFixed(1) + 'B';
    if (num >= 1.0e+6) return (num / 1.0e+6).toFixed(1) + 'M';
    if (num >= 1.0e+3) return (num / 1.0e+3).toFixed(1) + 'K';
    return num.toFixed(0);
}

const unique = (value, index, self) => {
    return self.indexOf(value) === index
}