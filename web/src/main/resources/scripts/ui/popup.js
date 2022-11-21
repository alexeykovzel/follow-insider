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


function confirm(title, message, doText, cancelText, $link) {
    let dialog = document.createElement('div');
    dialog.classList.add('dialog-overlay');
    dialog.innerHTML = `
        <div class='dialog'>
        <header>
            <h3>${title}</h3>
            <i class='fa fa-close'></i>
        </header>
        <div class='dialog-msg'>
        <p>${message}</p>
        </div>
            <footer>
                <div class='controls'>
                    <button class='button button-danger'>${doText}</button>
                    <button class='button button-default'>${cancelText}</button>
                </div>
            </footer>
        </div>`;

    document.querySelector('body').prepend(dialog);

    dialog.querySelector('.button-danger').onclick = () => {
        window.open($link, "_blank");
        this.parents('.dialog-overlay').fadeOut(500, function () {
            $(this).remove();
        });
    };

    $('.cancelAction, .fa-close').click(function () {
        $(this).parents('.dialog-overlay').fadeOut(500, function () {
            $(this).remove();
        });
    });

}

$('a').click(function () {
    confirm('Go to Google', 'Are you sure you want to visit Google', 'Yes', 'Cancel', "https://www.google.com.eg"); /*change*/
});

