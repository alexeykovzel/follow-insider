$(document).ready(() => {
    addHeader();
    addFooter();
});

function addHeader() {
    // prepend the header building block to the body
    $('body').prepend(`
        <header>
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
        </header>
    `);

    // configure the navigation menu for mobile
    $('.nav-btn').on('click', function () {
        let box = $('.nav-box');
        let checked = box.is(':checked');
        $('.header-menu').css('max-height', checked ? 0 : 240);
        box.prop('checked', !checked);
    })

    // show search hints while typing
    addTestHints($("#search"));
    // addSearchHints($("#search"));
}

function addFooter() {
    $('body').append(`
        <footer>
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
        </footer>
    `);
}