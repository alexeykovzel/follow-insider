$(document).ready(() => {
    addHeader();
    addFooter();
    addSearchHints();
    handleNavigation();
});

function addHeader() {
    $('body').prepend(`
        <header>
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
                <li><a href="/">Dashboard</a></li>
                <li><a href="/faq">FAQ</a></li>
            </ul>
        </header>
    `);
}

function addFooter() {
    $('body').append(`
        <footer>
            <ul class="nav-links">
                <li><a href="/">Dashboard</a></li>
                <li><a href="/faq">FAQ</a></li>
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

function addSearchHints() {
    $.ajax({
        type: "GET",
        url: location.origin + "/search/hints",
        success: (hints) => autocomplete(document.getElementById("search"), hints),
        error: (error) => console.log("[ERROR] " + error.responseText),
    });
}

function handleNavigation() {
    $('.nav-btn').on('click', function () {
        let box = $('.nav-box');
        let checked = box.is(':checked');
        $('.header-menu').css('max-height', checked ? 0 : 240);
        box.prop('checked', !checked);
    })
}

function autocomplete(inputField, options) {
    let focus;

    // show available options on input
    inputField.addEventListener("input", function (e) {
        closeAllLists();
        const input = this.value;
        if (!input) return false;
        focus = -1;

        // calculate matches
        let matches = [];
        for (let i = 0; i < options.length; i++) {
            if (options[i].substring(0, input.length).toUpperCase() === input.toUpperCase()) {
                matches.push(options[i]);
                if (matches.length > 5) break;
            }
        }

        // check if any matches
        if (matches.length > 0) {

            // create autocomplete list
            let list = document.createElement("DIV");
            list.setAttribute("id", this.id + "autocomplete-list");
            list.setAttribute("class", "autocomplete-items");
            this.parentNode.appendChild(list);

            // add matches to the list
            matches.forEach(val => {
                let option = document.createElement("DIV");
                option.innerHTML = "<strong>" + val.substring(0, input.length) + "</strong>";
                option.innerHTML += val.substring(input.length);
                option.innerHTML += "<input type='hidden' value='" + val + "'>";
                option.addEventListener("click", function (e) {
                    inputField.value = this.getElementsByTagName("input")[0].value;
                    closeAllLists();
                });
                list.appendChild(option);
            });
        }
    });

    // focus on one of the options using keys
    inputField.addEventListener("keydown", function (e) {
        let options = document.getElementById(this.id + "autocomplete-list");
        if (options) options = options.getElementsByTagName("div");

        // on 'keydown'
        if (e.keyCode === 40) {
            focus++;
            addActive(options);
        }
        // on 'keyup'
        if (e.keyCode === 38) {
            focus--;
            addActive(options);
        }
        // on 'enter'
        if (e.keyCode === 13) {
            e.preventDefault();
            if (focus > -1 && options) {
                options[focus].click();
            }
        }
    });

    // hide options if clicked elsewhere
    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });

    // make options on focus active
    function addActive(options) {
        if (!options) return false;
        removeActive(options);
        if (focus >= options.length) focus = 0;
        if (focus < 0) focus = (options.length - 1);
        options[focus].classList.add("autocomplete-active");
    }

    // remove active options
    function removeActive(options) {
        for (let i = 0; i < options.length; i++) {
            options[i].classList.remove("autocomplete-active");
        }
    }

    // hide all option lists
    function closeAllLists(el) {
        const options = document.getElementsByClassName("autocomplete-items");
        for (let i = 0; i < options.length; i++) {
            if (el !== options[i] && el !== inputField) {
                options[i].parentNode.removeChild(options[i]);
            }
        }
    }
}