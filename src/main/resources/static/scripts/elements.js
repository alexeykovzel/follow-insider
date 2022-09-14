import * as search from "./search.js";

customElements.define('default-header', class extends HTMLElement {
    constructor() {
        super();

        // set header in place of a custom element 
        jQuery(this).html(`
            <div>
                <h2 onclick="location.assign('/')">FI</h2>
                <div class="search autocomplete">
                    <label for="search"></label>
                    <input id="search" type="text" placeholder="Type a company or insider">
                    <object class="center" type="image/svg+xml" data="../static/images/search.svg"></object>
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
        jQuery(this).html(`
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

    /**
     * @param {*} id            id attribute of an html element
     * @param {*} columns       column names of the table
     * @param {*} fractions     column fractions of the table
     */
    constructor(id, columns, fractions) {
        this.id = id;
        this.columns = columns;
        this.fractions = fractions;
    }

    setFractions() {
        if (this.fractions === null) {
            this.fractions = Array(this.columns.length).fill(1);
        }
        let template = this.fractions.map(val => val + "fr").join(" ");
        $(`#${this.id} *`).css("grid-template-columns", template);
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