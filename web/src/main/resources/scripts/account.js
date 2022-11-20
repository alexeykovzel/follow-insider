import {postForm} from '/scripts/common/rest.js';

export function login(details) {
    postForm('/account/login', () => location.replace('/home'), details);
}

export function register(details) {
    postForm('/account/register', () => location.replace('/home'), details);
}

export function fetch(callback) {
    callback({name: 'Aliaksei'});
}