import {Dashboard, InfoBlock, ScatterChart} from "/scripts/ui/charts.js";

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
        dashboard.align();
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
        if (!this.checked) {
            this.checked = true;
            return;
        }
        // change active checkbox and load data
        checkboxes.forEach(checkbox => checkbox.checked = false);
        this.checked = true;
        loadData();
    });
}

function fetchTradePoints(symbol, range, types, load) {
    fetch(`./stocks/${symbol}/trade-points?range=${range}&types=${types.join(",")}`)
        .then(data => data.json())
        .then(data => {
            let points = [];
            data.forEach(obj => {
                let date = new Date(obj["date"]);
                let point = [date, obj["shareCount"]];
                points.push(point);
            });
            load(points);
        })
        .catch((error) => console.log("[ERROR] " + error.responseText));
}

function mockTradePoints() {
    let points = [];
    for (let i = 0; i < 30; i++) {
        let date = new Date(2022, 10, i);
        points.push([date, Math.pow(i + 2, 2)]);
    }
    return points;
}