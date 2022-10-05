import * as Search from "./search.js";

class Loader {
    constructor(parentId) {
        this.parentId = parentId;
        this.id = "l-" + parentId;
    }

    show() {
        this.hide();
        $("#" + this.parentId).append(`
            <div id="${this.id}" class="center">
                <div class="lds-facebook"><div></div><div></div><div></div></div>
            </div>
        `);
    }

    hide() {
        $("#" + this.parentId).find("#" + this.id).remove();
    }
}

customElements.define('default-header', class extends HTMLElement {
    constructor() {
        super();

        // set header in place of a custom element
        let header = $(this);
        header.html(`
            <div>
                <h2 onclick="location.assign('/')">FI</h2>
                <div id="search" class="search">
                    <label for="search-input"></label>
                    <input id="search-input" type="text" placeholder="Search a company or insider">
                    <img class="center" src="/images/icons/search.png" alt="Search Icon">
                </div>
                <button class="nav-btn">
                    <input id="nav-box" class="nav-box" type="checkbox"/>
                    <label for="nav-box"><span class="nav-icon"></span></label>
                </button>
            </div>
            <ul class="header-menu nav-links">
                <li onclick="location.assign('/')"><p>Dashboard</p></li>
                <li onclick="location.assign('/faq')"><p>FAQ</p></li>
                <li onclick="location.assign('/contact')"><p>Contact</p></li>
            </ul>
        `);

        // configure the navigation menu for mobile
        header.find(".nav-btn").on("click", function () {
            let box = $('.nav-box');
            let checked = box.is(":checked");
            $('.header-menu').css("max-height", checked ? 0 : 240);
            box.prop("checked", !checked);
        })

        // show search hints while typing
        Search.fetchHints(header.find("#search"));

        // -------- FOR TESTING ----------
        // let testHints = ["New York", "A very very very very long hint", "Neta1", "Neta2"];
        // Search.setHints(header.find("#search"), testHints);
        // -------------------------------
    }
});

customElements.define("default-footer", class extends HTMLElement {
    constructor() {
        super();

        // set footer in place of a custom element 
        $(this).html(`
            <ul class="nav-links">
                <li onclick="location.assign('/')"><p>Dashboard</p></li>
                <li onclick="location.assign('/faq')"><p>FAQ</p></li>
                <li onclick="location.assign('/contact')"><p>Contact</p></li>
            </ul>
            <div class="social">
                <a href="https://facebook.com/" class="fa fa-facebook"></a>
                <a href="https://twitter.com/" class="fa fa-twitter"></a>
                <a href="https://www.youtube.com/" class="fa fa-youtube"></a>
            </div>
            <p class="copyright">@ Copyright 2022 - FollowInsider</p>
        `);
    }
});

customElements.define("trade-filters", class extends HTMLElement {
    constructor() {
        super();

        $(this).html(`
            <div class="filters">
                <div class="checkboxes">
                    <div>
                        <input type="checkbox" id="buy" name="Buy" checked>
                        <label style="color: var(--buy)" for="buy">Buy</label>
                    </div>
                    <div>
                        <input type="checkbox" id="sell" name="Sell">
                        <label style="color: var(--sell)" for="sell">Sell</label>
                    </div>
                    <div>
                        <input type="checkbox" id="grant" name="Grant">
                        <label style="color: var(--grant)" for="grant">Grant</label>
                    </div>
                    <div>
                        <input type="checkbox" id="options" name="Options">
                        <label style="color: var(--options)" for="options">Options</label>
                    </div>
                    <div>
                        <input type="checkbox" id="taxes" name="Taxes">
                        <label style="color: var(--taxes)" for="taxes">Taxes</label>
                    </div>
                    <div>
                        <input type="checkbox" id="other" name="Other">
                        <label style="color: var(--other)" for="other">Other</label>
                    </div>
                </div>
            </div>
        `);
    }
});

class TimeRange {
    constructor(id, val, checked) {
        this.id = id;
        this.val = val;
        this.checked = checked || false;
    }
}

customElements.define("time-ranges", class extends HTMLElement {
    constructor() {
        super();
        let ranges = [
            new TimeRange("1m", "1M"),
            new TimeRange("3m", "3M"),
            new TimeRange("6m", "6M"),
            new TimeRange("1y", "1Y"),
            new TimeRange("3y", "3Y"),
            new TimeRange("5y", "5Y"),
            new TimeRange("10y", "10Y"),
            new TimeRange("max", "MAX", true)
        ];

        $(this).html(ranges.map(range => `
            <div>
                <input type="checkbox" id="${range.id}" name="${range.val}" ${range.checked ? "checked" : ""}>
                <label for="${range.id}">${range.val}</label>
            </div>
        `).join(""));
    }
});

export class Table {
    constructor(id, columns, fractions, filters) {
        this.id = id;
        this.columns = columns;
        this.fractions = fractions;
        this.filters = filters;
        this.loader = new Loader(this.id + " tbody");
    }

    initGrid() {
        if (this.fractions === null) {
            // set the same fraction for each column
            this.fractions = Array(this.columns.length).fill(1);
        }
        // set column fractions for each row
        let template = this.fractions.map(val => val + "fr").join(" ");
        this.ref.find("tr").css("grid-template-columns", template);
    }

    addAll(rows) {
        this.loader.hide();
        // append rows to the table
        let table = this.ref.find("tbody");
        rows.forEach(row => table.append(row));
    }

    reset() {
        this.initGrid();
        this.ref.find("tbody").empty();
        this.loader.show();
    }

    get html() {
        let tableHtml = `
            <div id="${this.id}" class="table-wrapper scrollbar">
                <table>
                    <thead>
                        <tr>
                            ${this.columns.map(col => "<th>" + col + "</th>").join("")}
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        `;
        let filtersHtml = (this.filters != null) ? this.filters : "";
        return filtersHtml + tableHtml
    }

    get ref() {
        return $("#" + this.id);
    }
}

export class Dashboard {
    constructor(blocks) {
        this.blocks = blocks || [];
    }

    init() {
        // initialise charts
        this.charts.forEach(chart => chart.init());
        // resize graphs on side panel transition
        let sidepanel = $(".s-panel");
        sidepanel.on("transitionend webkitTransitionEnd otransitionend", (event) => {
            if (event.originalEvent.propertyName !== "width") return;
            this.charts.forEach(chart => chart.reload());
        });
        sidepanel.on("transitionstart", (event) => {
            if (event.originalEvent.propertyName !== "width") return;
            this.charts.forEach(chart => chart.clear());
        });
        // resize info blocks on window resize
        this.align();
        $(window).resize(() => {
            this.charts.forEach(chart => chart.reload());
            this.align();
        });
    }

    align() {
        this.blocks.forEach(block => block.align());
    }

    get charts() {
        return this.blocks.filter(block => block.isChart);
    }

    get html() {
        return `
            <div class="dashboard">
                ${this.blocks.map(block => block.html).join("")}
            </div>
        `;
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
        let block = $("#" + this.blockId);
        let spans = Math.round(block.height() / 10) + 9;
        block.css("grid-row-end", "span " + spans);
    }

    get html() {
        return `
            <div id="${this.blockId}" class="info-block">
               <h3>${this.name}</h3>
               ${this.content}
            </div>
        `;
    }

    get blockId() {
        return "b-" + this.id;
    }

    get ref() {
        return $("#" + this.id);
    }
}

export class ScatterChart extends InfoBlock {
    constructor(id, name, labels, load) {
        super(id, name, `
            <time-ranges class="no-select"></time-ranges>
            <div id="${id}" class="chart"></div>
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
        let lightColor = "#CBC3E3"
        let darkColor = "#5D3FD3";

        // insert invisible point if no data
        let empty = points.length === 0;
        if (empty) points.push([new Date(), 0]);

        this.points = points;
        let data = google.visualization.arrayToDataTable([this.labels, ...points]);
        this.chart = new google.visualization.LineChart(document.getElementById(this.id));

        this.chart.draw(data, {
            legend: "none",
            curveType: "function",
            colors: [darkColor],
            pointSize: empty ? 0 : 5,
            lineWidth: 0,
            vAxis: {
                format: "short",
                gridlines: {
                    color: "#eee",
                    minSpacing: 40,
                },
                minorGridlines: {
                    count: 0
                }
            },
            series: {
                // show y-axis labels on the right
                // 0: {targetAxisIndex: 1},
            },
            hAxis: {
                format: (monthDiff > 3) ? "MMM d, y" : "MMM d",
                gridlines: {
                    color: "#eee",
                }
            },
            crosshair: {
                color: lightColor,
                trigger: "focus",
                orientation: "vertical"
            },
            focusTarget: "category",
            chartArea: {
                top: 15,
                width: "85%",
                height: "80%",
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