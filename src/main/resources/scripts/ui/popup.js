export function showError(error) {
    let params = JSON.parse(error.responseText);
    showTextError(params["message"]);
}

export function showTextError(text) {
    let toast = document.querySelector("#toast");
    toast.style.background = "#DC143C";
    showToast(toast, text);
}

export function showToast(toast, text) {
    toast.innerText = text;
    toast.classList.add("show");
    setTimeout(() => {
        toast.classList.remove("show");
    }, 3000);
}