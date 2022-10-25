import * as Utils from "/scripts/helpers/utils.js";

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
    Utils.fetchJson(location.origin + "/search/hints", (hints) => {
        setHints(search, hints);
    });
}

function setHints(search, hints) {
    autocomplete(new SearchBar(search, hints));
}

function autocomplete(search) {
    let ref = search.inputRef;

    ref.addEventListener("input", function () {
        let input = this.value;
        input = normalizeInput(input);
        // ignore invalid input
        if (!input) return false;
        // load matching hints
        resetAutocomplete(search);
        showMatchingHints(search, input);
    });

    ref.addEventListener("keydown", function (e) {
        switch (e.key) {
            case "Down":
            case "ArrowDown":
                search.focus++;
                updateActiveHint(search);
                break;

            case "Up":
            case "ArrowUp":
                search.focus--;
                updateActiveHint(search);
                break;

            case "Enter":
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
    if (search.focus < 0) search.focus = (matches - 1);
    search.focus = search.focus % matches;

    // set autocomplete active
    let hints = search.hintsRef;
    for (let i = 0; i < hints.length; i++) {
        if (i === search.focus) {
            hints[i].classList.add("autocomplete-active");
        } else {
            hints[i].classList.remove("autocomplete-active");
        }
    }
}

function showMatchingHints(search, input) {
    let matches = findMatches(search, input);

    // check if any matches
    let matchCount = Object.keys(matches).length;
    if (matchCount > 0) {

        // create autocomplete list
        let hints = document.createElement("div");
        hints.classList.add("autocomplete");
        search.ref.appendChild(hints);

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