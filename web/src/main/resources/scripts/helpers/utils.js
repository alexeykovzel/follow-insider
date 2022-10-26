export function formatNumber(val) {
    val = Math.abs(Number(val));
    if (val >= 1.0e+9) return (val / 1.0e+9).toFixed(1) + "B";
    if (val >= 1.0e+6) return (val / 1.0e+6).toFixed(1) + "M";
    if (val >= 1.0e+3) return (val / 1.0e+3).toFixed(1) + "K";
    return val.toFixed(0);
}

export function uniqueMerge(arr) {
    return unique(merge(arr));
}

export function unique(arr) {
    return arr.filter((value, index, self) => {
        return self.indexOf(value) === index
    });
}

export function merge(arr) {
    return [].concat.apply([], arr);
}

export function formatDate(val, options) {
    options = options || {month: "short", day: "numeric", year: "numeric"};
    return new Date(val).toLocaleDateString("en-US", options);
}

export function formatMoney(val) {
    return val.toFixed(1) + "$";
}

export function getLastUrlSegment() {
    let segments = location.href.split("?")[0].split("/");
    return segments.pop() || segments.pop();
}

export function fetchJson(url, success) {
    fetch(url)
        .then(response => {
            if (response.ok) return response.json();
            return response.statusText;
        })
        .then(success)
        .catch(error => showErrorToast(error));
}

export function showErrorToast(error) {
    showToast(error, "#DC143C");
}

export function showToast(text, background) {
    // ignore if there are any active toasts
    if (document.querySelector(".toast") !== null) return;

    // otherwise append a toast to a body
    let toast = document.createElement("div");
    toast.style.background = background ?? "#0A142F";
    toast.classList.add("toast", "show");
    toast.innerText = text;
    document.body.appendChild(toast);

    // remove a toast after 3 seconds
    setTimeout(() => toast.remove(), 3000);
}