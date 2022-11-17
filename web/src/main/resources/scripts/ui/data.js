export class Dashboard {
    constructor(blocks) {
        this.blocks = blocks || [];
    }

    init() {
        // initialise charts
        this.charts.forEach(chart => chart.init());

        // resize graphs on side panel transition
        let sidepanel = document.querySelector('.s-panel');
        ['transitionend', 'webkitTransitionEnd', 'otransitionend'].forEach(type => {
            sidepanel.addEventListener(type, (event) => {
                if (event.propertyName !== 'width') return;
                this.charts.forEach(chart => chart.reload());
            });
        });
        sidepanel.addEventListener('transitionstart', (event) => {
            if (event.propertyName !== 'width') return;
            this.charts.forEach(chart => chart.clear());
        });

        // resize info blocks on window resize
        this.alignAll();
        window.onresize = () => {
            this.charts.forEach(chart => chart.reload());
            this.alignAll();
        };
    }

    alignAll() {
        this.blocks.forEach(block => block.align());
    }

    get charts() {
        return this.blocks.filter(block => block.isChart);
    }

    get html() {
        return `
            <div class='dashboard'>
                ${this.blocks.map(block => block.html).join('')}
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
        let block = document.getElementById(this.blockId);
        let spans = Math.round(block.clientHeight / 10) + 3;
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
        return document.getElementById(this.id);
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


export class Table {
    constructor(id, columns, fractions, filters) {
        this.id = id;
        this.columns = columns;
        this.fractions = fractions;
        this.filters = filters;
        this.loader = new Loader(this.id);
    }

    initGrid() {
        // set the same fraction for each column
        if (this.fractions === null) {
            this.fractions = Array(this.columns.length).fill(1);
        }
        // set column fractions for each row
        let template = this.fractions.map(val => val + "fr").join(" ");
        this.ref.querySelectorAll("tr").forEach(row => {
            row.style.gridTemplateColumns = template;
        });
    }

    addAll(rows) {
        this.body.style.minHeight = "0";
        this.loader.hide();
        // append rows to the table
        let table = this.body;
        rows.forEach(row => table.appendChild(row));
    }

    reset() {
        this.initGrid();
        this.body.style.minHeight = "200px";
        this.body.innerHTML = "";
        this.loader.show();
    }

    get html() {
        let headerRow = this.columns.map(col => "<th>" + col + "</th>").join("");
        let tableHtml = `
            <div id="${this.id}" class="table-wrapper scrollbar">
                <table>
                    <thead><tr>${headerRow}</tr></thead>
                    <tbody></tbody>
                </table>
            </div>`;

        let filtersHtml = (this.filters != null) ? this.filters : "";
        return filtersHtml + tableHtml
    }

    get ref() {
        return document.getElementById(this.id);
    }

    get body() {
        return this.ref.querySelector("tbody");
    }
}

export class Loader {
    constructor(parentId) {
        this.parentId = parentId;
        this.id = "l-" + parentId;
    }

    show() {
        this.hide();
        let parent = document.querySelector(`#${this.parentId} tbody`);
        if (parent === null) return;
        parent.innerHTML += `
            <div id="${this.id}" class="center">
                <div class="lds-facebook"><div></div><div></div><div></div></div>
            </div>`;
    }

    hide() {
        let loader = document.getElementById(this.id);
        if (loader !== null) loader.remove();
    }
}