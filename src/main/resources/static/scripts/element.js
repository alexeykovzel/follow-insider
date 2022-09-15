import * as search from "./search.js";

customElements.define('default-header', class extends HTMLElement {
    constructor() {
        super();

        // set header in place of a custom element 
        $(this).html(`
            <div>
                <h2 onclick="location.assign('/')">FI</h2>
                <div class="search autocomplete">
                    <label for="search"></label>
                    <input id="search" type="text" placeholder="Type a company or insider">
                    <object class="center" type="image/svg+xml" data="/images/search.svg"></object>
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
        $('.nav-btn').on('click', function () {
            let box = $('.nav-box');
            let checked = box.is(':checked');
            $('.header-menu').css('max-height', checked ? 0 : 240);
            box.prop('checked', !checked);
        })

        // show search hints while typing
        search.addTestHints($("#search"));
    }
});

customElements.define('default-footer', class extends HTMLElement {
    constructor() {
        super();

        // set footer in place of a custom element 
        $(this).html(`
            <ul class="nav-links">
                <li><a href="/">Dashboard</a></li>
                <li><a href="/faq">FAQ</a></li>
                <li><a href="/faq">Contact</a></li>
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

export class Table {
    constructor(id, columns, fractions) {
        this.id = id;
        this.columns = columns;
        this.fractions = fractions;
    }

    init() {
        if (this.fractions === null) {
            // set the same fraction for each column
            this.fractions = Array(this.columns.length).fill(1);
        }
        // set column fractions for each row
        let template = this.fractions.map(val => val + "fr").join(" ");
        this.ref.find("tr").css("grid-template-columns", template);
    }

    addAll(rows) {
        // remove loading animation
        $("#loader").remove();
        // append rows to the table
        let table = this.ref.find("tbody");
        rows.forEach(row => table.append(row));
    }

    reset() {
        // delete all rows
        this.ref.find("tbody").empty();
        // add loading animation (if not yet)
        if (!$("#loader").length) {
            this.loading();
        }
    }

    loading() {
        // show loading animation in the center
        this.ref.find("tbody").append(`
            <div id="loader" class="center">
                <div class="lds-facebook"><div></div><div></div><div></div></div>
            </div>
        `);
    }

    get ref() {
        // find table by id
        return $("#" + this.id);
    }

    get html() {
        return `
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
    }
}

export class Dashboard {
    constructor(blocks) {
        this.blocks = blocks;
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
    constructor(id, name, content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    align() {
        // default span is 10
        let block = $("#b-" + this.id);
        let spans = Math.round(block.height() / 10) + 9;
        block.css("grid-row-end", "span " + spans);
    }

    get html() {
        return `
            <div id="b-${this.id}" class="info-block">
               <h3>${this.name}</h3>
               ${this.content}
            </div>
        `;
    }
}

export class LineGraph extends InfoBlock {
    constructor(id, name) {
        super(id, name, `<canvas id="${id}" class="line-chart"></canvas>`);
        this.id = id;
        this.name = name;
    }

    draw() {
        let x = ["1 Jan", "1 Feb", "1 Mar", "1 Apr", "1 May"];
        let y = [7, 8, 8, 9, 16];

        new Chart(this.id, {
            type: "line",
            data: {
                labels: x,
                datasets: [{
                    fill: false,
                    lineTension: 0.3,
                    backgroundColor: "red",
                    borderColor: "rgba(0,0,255,0.1)",
                    data: y
                }]
            },
            options: {
                legend: {display: false},
                scales: {
                    yAxes: [{ticks: {min: 6, max: 16}}],
                },
                hover: {
                    intersect: false
                },
                tooltips: {
                    intersect: false,
                }
            }
        });
    }
}