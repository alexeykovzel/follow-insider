export function showErrorToast(error) {
    showToast(error, '#DC143C');
}

export function showToast(text, background) {
    // ignore if there are any active toasts
    if (document.querySelector('.toast') !== null) return;

    // otherwise append a toast to a body
    let toast = document.createElement('div');
    toast.style.background = background ?? '#0A142F';
    toast.classList.add('toast', 'show');
    toast.innerText = text;
    document.body.appendChild(toast);

    // remove a toast after 3 seconds
    setTimeout(() => toast.remove(), 3000);
}