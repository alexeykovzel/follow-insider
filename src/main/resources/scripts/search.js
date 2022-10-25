import {showError} from "/scripts/ui/popup.js";

class SearchBar {
    constructor(ref, hints) {
        this.ref = ref;
        this.hints = hints;
        this.focus = -1;
        this.matches = 0;
    }

    get inputRef() {
        return this.ref.querySelector("input");
    }

    get hintsRef() {
        return this.ref.querySelectorAll(".autocomplete>*");
    }
}

export function fetchHints(search) {
    fetch("./search/hints")
        .then(data => data.json())
        .then(hints => setHints(search, hints))
        .catch(error => showError(error));
}

export function setHints(search, hints) {
    autocomplete(new SearchBar(search, hints));
}

function autocomplete(search) {
    let ref = search.inputRef;

    ref.oninput = function () {
        let input = this.value;
        input = normalizeInput(input);
        // ignore invalid input
        if (!input) return false;
        // load matching hints
        resetAutocomplete(search);
        showMatchingHints(search, input);
    };
    ref.addEventListener('keydown', function (e) {
        // on "keydown"
        if (e.keyCode === '40') {
            search.focus++;
            updateActiveHint(search)
        }
        // on "keyup"
        if (e.keyCode === '38') {
            search.focus--;
            updateActiveHint(search)
        }
        // on "enter"
        if (e.keyCode === '13') {
            e.preventDefault();
            let hints = search.hintsRef;
            if (search.focus > -1 && hints) {
                hints[search.focus].click();
            }
        }
    });

    // hide autocompletes if clicked somewhere
    window.addEventListener("click", () => {
        resetAutocomplete(search);
    });
}

function resetAutocomplete(search) {
    let autocomplete = search.ref.querySelector(".autocomplete");
    if (autocomplete !== null) autocomplete.remove();
    search.focus = -1;
}

function normalizeInput(input) {
    return input.replace(" ", "").toLowerCase();
}

function updateActiveHint(search) {
    let matches = search.matches;
    if (matches === 0) return false;

    // shift current focus
    let currentFocus = search.focus;
    if (currentFocus < 0) search.focus = (matches - 1);
    search.focus = currentFocus % matches;

    // set autocomplete active
    let ref = search.hintsRef;
    ref.classList.remove("autocomplete-active");
    ref[search.focus].classList.add("autocomplete-active");
}

function showMatchingHints(search, input) {
    let matches = findMatches(search, input);

    // check if any matches
    let matchCount = Object.keys(matches).length;
    if (matchCount > 0) {

        // create autocomplete list
        search.ref.innerHTML += "<div class='autocomplete'></div>";
        let hints = document.querySelector(".autocomplete");

        // add matches to the autocomplete list
        search.matches = matchCount;
        for (const [val, idx] of Object.entries(matches)) {
            let p1 = val.substring(0, idx);
            let match = val.substring(idx, input.length + idx);
            let p2 = val.substring(input.length + idx, val.length);

            hints.innerHTML += `
                <p onclick="location.assign('/search?q=${val}')">${p1}<b>${match}</b>${p2}</p>
            `;
        }
    }
}

function findMatches(search, input) {
    let hints = search.hints;
    let matches = {};
    let i = 0;
    while (Object.keys(matches).length < 5 && hints.length > i) {
        let index = hints[i].toLowerCase().indexOf(input);
        if (index !== -1) {
            matches[hints[i]] = index;
        }
        i++;
    }
    return matches;
}