import {showErrorToast} from '/scripts/ui/popup.js';

export function deleteRaw(url, success) {
    fetchRaw(url, success, {'method': 'DELETE'});
}

export function postRaw(url, success) {
    fetchRaw(url, success, {'method': 'POST'});
}

export function postJson(url, success, data) {
    fetchRaw(url, success, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/json'},
        'body': JSON.stringify(data)
    });
}

export function postForm(url, success, data) {
    fetchRaw(url, success, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        'body': new URLSearchParams(data),
    });
}

export function fetchJson(url, success) {
    fetchByFormat(url, success, {
        'method': 'GET',
        'headers': {'Accept': 'application/json'},
    }, 'json');
}

export function fetchRaw(url, success, props) {
    fetchByFormat(url, success, props, 'raw');
}

function fetchByFormat(url, success, props, format) {
    fetch(location.origin + url, props)
        .then(response => {
            if (response.redirected) throw new Error('Tried to redirect')
            console.log(response)
            if (!response.ok) throw new Error(response.statusText);
            return {
                'raw': (data) => data,
                'json': (data) => data.json(),
            }[format](response);
        })
        .then(success)
        .catch(error => {
            console.log(error);
            showErrorToast(error);
        });
}