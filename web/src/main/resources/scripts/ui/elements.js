import * as Search from "/scripts/search.js";

customElements.define("default-header", class extends HTMLElement {
    constructor() {
        super();
        // set header.css in place of a custom element
        this.innerHTML = `
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
            </ul>`;

        // configure the navigation menu for mobile
        this.querySelector(".nav-btn").onclick = function () {
            let box = document.querySelector(".nav-box");
            document.querySelector(".header-menu").style.maxHeight = box.checked ? "0" : "240px";
            box.checked = !box.checked;
        };

        // show search hints while typing
        Search.fetchHints(document.getElementById("search"));

        // -------- FOR TESTING ----------
        // let testHints = ["New York", "A very very very very long hint", "Neta1", "Neta2"];
        // Search.setHints(header.css.find("#search"), testHints);
        // -------------------------------
    }
});

customElements.define("default-footer", class extends HTMLElement {
    constructor() {
        super();
        // set footer in place of a custom element 
        this.innerHTML = `
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
            <p class="copyright">@ Copyright 2022 - FollowInsider</p>`;
    }
});

customElements.define("trade-filters", class extends HTMLElement {
    constructor() {
        super();
        this.innerHTML = `
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
            </div>`;
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
        this.innerHTML = (ranges.map(range => `
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