import * as Search from "/scripts/helpers/search.js";
import * as Account from "/scripts/account.js";

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
                    <img class="ctr" src="/images/icons/search.png" alt="Search Icon">
                </div>
                <div class="user-menu blue-btn">Login</div>
                <button class="nav-btn">
                    <input id="nav-box" class="nav-box" type="checkbox"/>
                    <label for="nav-box"><span class="nav-icon"></span></label>
                </button>
            </div>
            <ul class="header-menu nav-links">
                <li onclick="location.assign('/home')"><p>Home</p></li>
                <li onclick="location.assign('/faq')"><p>FAQ</p></li>
                <li onclick="location.assign('/contact')"><p>Contact</p></li>
            </ul>`;

        // configure the navigation menu for mobile
        this.querySelector(".nav-btn").onclick = function () {
            let box = document.querySelector(".nav-box");
            document.querySelector(".header-menu").style.maxHeight = box.checked ? "0" : "240px";
            box.checked = !box.checked;
        };

        // try to login user silently 
        Account.fetch((user) => {
            let userMenu = document.querySelector('.user-menu');
            userMenu.classList.remove('blue-btn');
            userMenu.innerHTML = `
                <div class="avatar"><img src="/images/icons/profile.svg" alt="Avatar Icon"></div>
                <p></p>`;
            userMenu.querySelector('p').innerText = user.name;
            if ('avatar' in user) {
                userMenu.querySelector('img').src = user.avatar;
            }
        });

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
                <li onclick="location.assign('/home')"><p>Home</p></li>
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

customElements.define("time-ranges", class extends HTMLElement {
    constructor() {
        super();
        let ranges = [
            {id: '1m', val: '1M', checked: false},
            {id: '3m', val: '3M', checked: false},
            {id: '6m', val: '6M', checked: false},
            {id: '1y', val: '1Y', checked: false},
            {id: '3y', val: '3Y', checked: false},
            {id: '5y', val: '5Y', checked: false},
            {id: '10y', val: '10Y', checked: false},
            {id: 'max', val: 'MAX', checked: true},
        ];
        this.innerHTML = (ranges.map(range => `
            <div>
                <input type="checkbox" id="${range['id']}" name="${range['val']}" ${range['checked'] ? "checked" : ""}>
                <label for="${range['id']}">${range['val']}</label>
            </div>
        `).join(""));
    }
});