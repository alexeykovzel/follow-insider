window.tabs = [];

class Tab {
    constructor(name, html, init) {
        this.name = name;
        this.html = html;
        this.init = init;
    }
}

function initTabs(tabs) {
    window.tabs = tabs;
    let tabsEl = $("#tabs")
    for (let i = 0; i < tabs.length; i++) {
        tabsEl.append(`<a onclick="loadTab(${i})">${tabs[i].name}</a>`);
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