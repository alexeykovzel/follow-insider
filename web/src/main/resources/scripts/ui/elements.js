import * as Search from '/scripts/common/search.js';
import * as Account from '/scripts/account.js';

customElements.define('default-header', class extends HTMLElement {
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
        this.querySelector('.nav-btn').onclick = function () {
            let box = document.querySelector('.nav-box');
            document.querySelector('.header-menu').style.maxHeight = box.checked ? '0' : '240px';
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
        Search.fetchHints(document.getElementById('search'));

        // -------- FOR TESTING ----------
        // let testHints = ["New York", "A very very very very long hint", "Neta1", "Neta2"];
        // Search.setHints(header.css.find("#search"), testHints);
        // -------------------------------
    }
});

customElements.define('default-footer', class extends HTMLElement {
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

customElements.define('trade-filters', class extends HTMLElement {
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

customElements.define('time-ranges', class extends HTMLElement {
    constructor() {
        super();
        let ranges = [
            { id: '1m', val: '1M', checked: false },
            { id: '3m', val: '3M', checked: false },
            { id: '6m', val: '6M', checked: false },
            { id: '1y', val: '1Y', checked: false },
            { id: '3y', val: '3Y', checked: false },
            { id: '5y', val: '5Y', checked: false },
            { id: '10y', val: '10Y', checked: false },
            { id: 'max', val: 'MAX', checked: true },
        ];
        this.innerHTML = (ranges.map(range => `
            <div>
                <input type="checkbox" id="${range['id']}" name="${range['val']}" ${range['checked'] ? "checked" : ""}>
                <label for="${range['id']}">${range['val']}</label>
            </div>
        `).join(''));
    }
});

customElements.define('bottom-waves', class extends HTMLElement {
    constructor() {
        super();
        this.innerHTML = `
            <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
                <path d="M0,0V46.29c47.79,22.2,103.59,32.17,158,28,70.36-5.37,136.33-33.31,206.8-37.5C438.64,32.43,512.34,53.67,583,72.05c69.27,18,138.3,24.88,209.4,13.08,36.15-6,69.85-17.84,104.45-29.34C989.49,25,1113-14.29,1200,52.47V0Z" opacity=".25" class="shape-fill"></path>
                <path d="M0,0V15.81C13,36.92,27.64,56.86,47.69,72.05,99.41,111.27,165,111,224.58,91.58c31.15-10.15,60.09-26.07,89.67-39.8,40.92-19,84.73-46,130.83-49.67,36.26-2.85,70.9,9.42,98.6,31.56,31.77,25.39,62.32,62,103.63,73,40.44,10.79,81.35-6.69,119.13-24.28s75.16-39,116.92-43.05c59.73-5.85,113.28,22.88,168.9,38.84,30.2,8.66,59,6.17,87.09-7.5,22.43-10.89,48-26.93,60.65-49.24V0Z" opacity=".5" class="shape-fill"></path>
                <path d="M0,0V5.63C149.93,59,314.09,71.32,475.83,42.57c43-7.64,84.23-20.12,127.61-26.46,59-8.63,112.48,12.24,165.56,35.4C827.93,77.22,886,95.24,951.2,90c86.53-7,172.46-45.71,248.8-84.81V0Z" class="shape-fill"></path>
            </svg>`;
    }
});