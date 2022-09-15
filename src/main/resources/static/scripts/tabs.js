window.tabs = [];

export class Tab {
    constructor(name, html, init) {
        this.name = name;
        this.html = html;
        this.init = init;
    }
}

export function initTabs(tabs) {
    window.tabs = tabs;
    let htmlTabs = $("#tabs")
    for (let i = 0; i < tabs.length; i++) {
        let tab = $(`<a>${tabs[i].name}</a>`);
        tab.on("click", () => loadTab(i));
        htmlTabs.append(tab);
    }
    loadTab(0);
}

function loadTab(index) {
    // throw an error if such tab doesn't exist
    if (tabs.length <= index) {
        throw "invalid tab index: " + index;
    }

    // highlight the chosen tab
    let links = $("#tabs a");
    links.each(function () {
        $(this).removeClass("chosen");
    });
    links.eq(index).addClass("chosen");

    // load the tab content
    $("#tab-content").html(tabs[index].html);
    tabs[index].init();
}