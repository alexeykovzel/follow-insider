class SearchBar {
    constructor(ref, hints) {
        this.ref = ref;
        this.hints = hints;
        this.focus = -1;
    }

    get inputRef() {
        return this.ref.find("input");
    }

    get hintsRef() {
        return this.ref.find(".autocomplete>*");
    }
}

$(document).ready(() => {
    // hide autocompletes if clicked somewhere
    $(document).on("click", function () {
        $(".autocomplete").remove();
    });
})

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
        // else show matching hints
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
}

function normalizeInput(input) {
    return input.replace(" ", "").toLowerCase();
}

function updateActiveHint(search) {
    let hints = search.hints;
    let ref = search.hintsRef;
    if (!search.hints) return false;
    if (search.focus >= hints.length) search.focus = 0;
    if (search.focus < 0) search.focus = (hints.length - 1);
    ref.removeClass("autocomplete-active");
    ref.eq(search.focus).addClass("autocomplete-active");
}

function showMatchingHints(search, input) {
    search.ref.find(".autocomplete").remove();
    let matches = findMatches(search, input);

    // check if any matches
    if (Object.keys(matches).length > 0) {

        // create autocomplete list
        let hints = $(`<div class="autocomplete"></div>`);
        hints.appendTo(search.ref);

        // add matches to the autocomplete list
        for (const [hint, index] of Object.entries(matches)) {
            let p1 = hint.substring(0, index);
            let match = hint.substring(index, input.length + index);
            let p2 = hint.substring(input.length + index, hint.length);

            // highlight the matched part in bold
            $(`<p>${p1}<b>${match}</b>${p2}</p>`).click(function () {
                location.assign("/search?q=" + hint);
            }).appendTo(hints);
        }
    }
}

function findMatches(search, input) {
    let hints = search.hints;
    let matches = {};
    for (let i = 0; i < Math.min(5, hints.length); i++) {
        let index = hints[i].toLowerCase().indexOf(input);
        if (index !== -1) {
            matches[hints[i]] = index;
        }
    }
    return matches;
}