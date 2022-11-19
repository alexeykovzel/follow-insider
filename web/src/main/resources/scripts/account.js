import * as Utils from '/scripts/common/utils.js';

class InputField {
    constructor(name, type) {
        this.name = name;
        this.type = type;
    }

    get node() {
        let field = document.createElement('div');
        field.classList.add('col', 'input-field');
        field.innerHTML = `
            <label for="${this.name}">${Utils.capitalize(this.name)}:</label>
            <input id="${this.name}" type="${this.type}" placeholder="Enter ${this.name}">`;
        return field;
    }
}

export function openRegister() {
    let loginLink = `Already have an account? <span class="link">Sign in<span>`;
    setAuthPage('Sign up', register, loginLink, openLogin, [
        new InputField('password', 'password'),
        new InputField('e-mail', 'email'),
        new InputField('fullname', 'text'),
    ]);
}

export function openLogin() {
    let registerLink = `Don't have an account yet? <span class="link">Sign up<span>`;
    setAuthPage('Sign in', login, registerLink, openRegister, [
        new InputField('password', 'password'),
        new InputField('e-mail', 'email'),
    ]);
}

function setAuthPage(type, callback, link, redirect, fields) {
    let panel = document.querySelector('.auth');
    let btn = panel.querySelector('#auth-btn');
    panel.querySelectorAll('.input-field').forEach(field => field.remove());
    panel.querySelector('.toggle-link').innerHTML = link;
    panel.querySelector('.link').onclick = () => redirect();
    fields.forEach(field => panel.prepend(field.node))
    btn.onclick = () => callback();
    btn.innerText = type;
}

function login() {
    Utils.postForm('/account/login', () => location.replace('/home'), {
        'email': document.getElementById('e-mail').value,
        'password': document.getElementById('password').value
    });
}

function register() {
    Utils.postForm('/account/register', () => location.replace('/home'), {
        'email': document.getElementById('e-mail').value,
        'password': document.getElementById('password').value,
        'fullname': document.getElementById('fullname').value
    });
}

export function fetch(callback) {
    callback({name: 'Aliaksei'});
}