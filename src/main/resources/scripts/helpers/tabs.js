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
    let htmlTabs = document.getElementById("tabs");
    for (let i = 0; i < tabs.length; i++) {
        htmlTabs.innerHTML += `<a onclick="loadTab(${i})">${tabs[i].name}</a>`;
    }
    // load the 1-st tab by default
    loadTab(0);
}

function loadTab(index) {
    // throw an error if such tab doesn't exist
    if (tabs.length <= index) throw "invalid tab index: " + index;

    // highlight the chosen tab
    let links = document.querySelectorAll("#tabs a");
    links.forEach(link => link.classList.remove("chosen"));
    links[index].classList.add("chosen");

    // load the tab content
    document.querySelector("#tab-content").innerHTML = tabs[index].html;
    tabs[index].init();
}