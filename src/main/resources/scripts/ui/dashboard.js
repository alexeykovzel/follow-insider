import {Loader} from "/scripts/ui/elements.js";
import {showError} from "/scripts/ui/popup.js";

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
        .catch((error) => showError(error));
}

function mockTradePoints() {
    let points = [];
    for (let i = 0; i < 30; i++) {
        let date = new Date(2022, 10, i);
        points.push([date, Math.pow(i + 2, 2)]);
    }
    return points;
}

export class Dashboard {
    constructor(blocks) {
        this.blocks = blocks || [];
    }

    init() {
        // initialise charts
        this.charts.forEach(chart => chart.init());

        // resize graphs on side panel transition
        let sidepanel = document.querySelector('.s-panel');
        sidepanel.addEventListener('transitionend webkitTransitionEnd otransitionend', (event) => {
            if (event.originalEvent.propertyName !== 'width') return;
            this.charts.forEach(chart => chart.reload());
        });
        sidepanel.addEventListener('transitionstart', (event) => {
            if (event.originalEvent.propertyName !== 'width') return;
            this.charts.forEach(chart => chart.clear());
        });

        // resize info blocks on window resize
        this.align();
        window.onresize = () => {
            this.charts.forEach(chart => chart.reload());
            this.align();
        };
    }

    align() {
        this.blocks.forEach(block => block.align());
    }

    get charts() {
        return this.blocks.filter(block => block.isChart);
    }

    get html() {
        return `
            <div class='dashboard'>
                ${this.blocks.map(block => block.html).join('')}
            </div>`;
    }
}

export class InfoBlock {
    constructor(id, name, content, isChart) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.isChart = isChart || false;
    }

    align() {
        // default span is 10
        let block = document.querySelector('#' + this.blockId);
        let spans = Math.round(block.height() / 10) + 9;
        block.style.gridRowEnd = 'span ' + spans;
    }

    get html() {
        return `
            <div id='${this.blockId}' class='info-block'>
               <h3>${this.name}</h3>
               ${this.content}
            </div>`;
    }

    get blockId() {
        return 'b-' + this.id;
    }

    get ref() {
        return document.querySelector('#' + this.id);
    }
}

export class ScatterChart extends InfoBlock {
    constructor(id, name, labels, load) {
        super(id, name, `
            <time-ranges class='no-select'></time-ranges>
            <div id='${id}' class='chart'></div>
        `, true);
        this.labels = labels;
        this.load = load;
        this.loader = new Loader(this.id);
    }

    init() {
        this.loader.show();
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(() => this.load());
    }

    draw(points) {
        if (!points) return;
        // sort data points by date (ascending)
        points = points.sort((a, b) => a[0] > b[0] ? 1 : -1);

        // calculate time range for label formatting
        let monthDiff = 0;
        if (points.length > 1) {
            let timeDiff = points[points.length - 1][0].getTime() - points[0][0].getTime();
            monthDiff = timeDiff / 1000 / 3600 / 24 / 30;
        }
        // dots' and crosshair colorsX
        let lightColor = '#CBC3E3'
        let darkColor = '#5D3FD3';

        // insert invisible point if no data
        let empty = points.length === 0;
        if (empty) points.push([new Date(), 0]);

        this.points = points;
        let data = google.visualization.arrayToDataTable([this.labels, ...points]);
        this.chart = new google.visualization.LineChart(document.getElementById(this.id));

        this.chart.draw(data, {
            legend: 'none',
            curveType: 'function',
            focusTarget: 'category',
            colors: [darkColor],
            pointSize: empty ? 0 : 5,
            lineWidth: 0,
            vAxis: {
                format: 'short',
                gridlines: {color: '#eee', minSpacing: 40},
                minorGridlines: {count: 0}
            },
            series: {
                // show y-axis labels on the right
                // 0: {targetAxisIndex: 1},
            },
            hAxis: {
                format: (monthDiff > 3) ? 'MMM d, y' : 'MMM d',
                gridlines: {color: '#eee',}
            },
            crosshair: {
                color: lightColor,
                trigger: 'focus',
                orientation: 'vertical'
            },
            chartArea: {
                top: 15,
                width: '85%',
                height: '80%',
            },
        });
        this.loader.hide();
    }

    reload() {
        this.clear();
        this.draw(this.points);
    }

    clear() {
        this.chart.clearChart();
        this.loader.show();
    }
}