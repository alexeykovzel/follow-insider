import {showErrorToast} from '/scripts/ui/popup.js';

export function deleteRaw(url, success) {
    console.log('delete raw: ' + url);
    fetchRaw(url, success, {'method': 'DELETE'});
}

export function postRaw(url, success) {
    console.log('post raw: ' + url);
    fetchRaw(url, success, {'method': 'POST'});
}

export function postJson(url, success, data) {
    console.log('post json: ' + url);
    fetchRaw(url, success, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/json'},
        'body': JSON.stringify(data)
    });
}

export function postForm(url, success, data) {
    console.log('post form: ' + url);
    fetchRaw(url, success, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        'body': new URLSearchParams(data),
    });
}

export function fetchJson(url, success) {
    console.log('fetch json: ' + url);
    fetchByFormat(url, success, {
        'method': 'GET',
        'headers': {'Accept': 'application/json'},
    }, 'json');
}

export function fetchRaw(url, success, props) {
    console.log('fetch raw: ' + url);
    fetchByFormat(url, success, props, 'raw');
}

function fetchByFormat(url, success, props, format) {
    fetch(location.origin + url, props)
        .then(response => {
            if (response.redirected) throw new Error('Tried to redirect')
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