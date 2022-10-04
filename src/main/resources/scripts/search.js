class SearchBar {
    constructor(ref, hints) {
        this.ref = ref;
        this.hints = hints;
        this.focus = -1;
        this.matches = 0;
    }

    get inputRef() {
        return this.ref.find("input");
    }

    get hintsRef() {
        return this.ref.find(".autocomplete>*");
    }
}

export function fetchHints(search) {
    $.ajax({
        type: "GET",
        url: location.origin + "/search/hints",
        success: (hints) => setHints(search, hints),
        error: (error) => console.log("[ERROR] " + error.responseText),
    });
}

export function setHints(search, hints) {
    autocomplete(new SearchBar(search, hints));
}

function autocomplete(search) {
    let ref = search.inputRef;

    ref.on("input", function () {
        let input = $(this).val();
        input = normalizeInput(input);
        // ignore invalid input
        if (!input) return false;
        // load matching hints
        resetAutocomplete(search);
        showMatchingHints(search, input);
    });

    ref.keydown(function (e) {
        // on 'keydown'
        if (e.keyCode === 40) {
            search.focus++;
            updateActiveHint(search)
        }
        // on 'keyup'
        if (e.keyCode === 38) {
            search.focus--;
            updateActiveHint(search)
        }
        // on 'enter'
        if (e.keyCode === 13) {
            e.preventDefault();
            let hints = search.hintsRef;
            if (search.focus > -1 && hints) {
                hints[search.focus].click();
            }
        }
    });

    // hide autocompletes if clicked somewhere
    $(document).on("click", function () {
        resetAutocomplete(search);
    });
}

function resetAutocomplete(search) {
    search.ref.find(".autocomplete").remove();
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
    ref.removeClass("autocomplete-active");
    ref.eq(search.focus).addClass("autocomplete-active");
}

function showMatchingHints(search, input) {
    let matches = findMatches(search, input);

    // check if any matches
    let matchCount = Object.keys(matches).length;
    if (matchCount > 0) {

        // create autocomplete list
        let hints = $(`<div class="autocomplete"></div>`);
        hints.appendTo(search.ref);

        // add matches to the autocomplete list
        search.matches = matchCount;
        for (const [hint, idx] of Object.entries(matches)) {
            let p1 = hint.substring(0, idx);
            let match = hint.substring(idx, input.length + idx);
            let p2 = hint.substring(input.length + idx, hint.length);
            $(`<p onclick="location.assign('/search?q=${hint}')">${p1}<b>${match}</b>${p2}</p>`)
                .appendTo(hints);
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