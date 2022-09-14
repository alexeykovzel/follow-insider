export function addHints(searchbar) {
    $.ajax({
        type: "GET",
        url: location.origin + "/search/hints",
        success: (hints) => autocomplete(searchbar, hints),
        error: (error) => console.log("[ERROR] " + error.responseText),
    });
}

export function addTestHints(searchbar) {
    let hints = ["New York", "A reeeeeeeeally loooooong hint", "Neta1", "Neta2"];
    autocomplete(searchbar, hints);
}

function autocomplete(inputField, options) {
    let focus;

    // show available options on input
    inputField.on('input', function () {
        $(".autocomplete-items").remove();
        const input = $(this).val().toLowerCase();
        if (!input) return false;
        focus = -1;

        // calculate matches
        let matches = [];
        options.forEach(option => {
            let index = option.toLowerCase().indexOf(input);
            if (index !== -1) {
                matches.push([option, index]);
                if (matches.length > 5) return;
            }
        });

        // check if any matches
        if (matches.length > 0) {

            // create autocomplete list
            let id = $(this).id + "autocomplete-list";
            let items = $(`<div id="${id}" class="autocomplete-items"></div>`);
            items.appendTo($(this).parent());

            // add matches to the list
            matches.forEach((val) => $(`
                <div>
                    ${val[0].substring(0, val[1])}
                    <strong>${val[0].substring(val[1], val[1] + input.length)}</strong>
                    ${val[0].substring(val[1] + input.length)}
                    <input type='hidden' value='${val}'
                </div>
            `)).click(function () {
                location.assign("/search?q=" + match);
            }).appendTo(items);
        }
    });

    // focus on one of the options using keys
    inputField.keydown(function (e) {
        let items = $(`#${$(this).id}autocomplete-list div`);

        // on 'keydown'
        if (e.keyCode === 40) {
            focus++;
            addActive(items);
        }
        // on 'keyup'
        if (e.keyCode === 38) {
            focus--;
            addActive(items);
        }
        // on 'enter'
        if (e.keyCode === 13) {
            e.preventDefault();
            if (focus > -1 && items) {
                items[focus].click();
            }
        }
    });

    // hide options if clicked elsewhere
    $(document).on('click', function (e) {
        $(".autocomplete-items").remove();
    });

    // make options on focus active
    function addActive(items) {
        if (!items) return false;
        items.removeClass("autocomplete-active");
        if (focus >= items.length) focus = 0;
        if (focus < 0) focus = (items.length - 1);
        items.eq(focus).addClass("autocomplete-active");
    }
}