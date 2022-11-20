import {showErrorToast} from '/scripts/ui/popup.js';

export function post(url, success) {
    request(url, success, {method: 'POST'});
}

export function postJson(url, success, data) {
    request(url, success, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    });
}

export function postForm(url, success, data) {
    request(url, success, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        body: new URLSearchParams(data),
    });
}

export function fetchJson(url, success) {
    requestAndConvert(url, success, data => data.json(), {});
}

function request(url, success, props) {
    requestAndConvert(url, success, data => data, props);
}1

function requestAndConvert(url, success, convert, props) {
    fetch(location.origin + url, props)
        .then(response => {
            if (response.ok) return response;
            throw new Error(response.statusText);
        })
        .then(convert)
        .then(success)
        .catch(error => showErrorToast(error));
}