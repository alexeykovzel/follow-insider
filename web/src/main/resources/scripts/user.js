import * as REST from '/scripts/common/rest.js';
import {showToast} from '/scripts/ui/popup.js';

class User {
    static isAuthorized = null;
    static profile = null;
}

export function ifAuthorized(then) {
    console.log('is authorized: ' + User.isAuthorized);
    if (User.isAuthorized == null) {
        REST.fetchJson('/user/roles', (roles) => {
            User.isAuthorized = !roles.find(role => role['authority'] === 'ROLE_ANONYMOUS');
            ifAuthorized(then);
        });
    } else if (User.isAuthorized) {
        then();
    }
}

export function register(details) {
    console.log('register user: ' + JSON.stringify(details));
    REST.postForm('/user/register', () => location.replace('/home'), details);
}

export function login(details) {
    console.log('login user: ' + JSON.stringify(details));
    REST.postForm('/user/login', () => location.replace('/home'), details);
}

export function logout() {
    REST.postRaw('/user/logout', () => location.replace('/'));
}

export function fetchProfile(success) {
    if (User.profile != null) return User.profile;
    REST.fetchJson('/user/profile', (profile) => {
        User.profile = profile;
        success(profile);
    });
}

export function updateProfile(profile) {
    console.log('update profile: ' + JSON.stringify(profile));
    REST.postForm('/user/profile/update', () => {
        User.profile = profile;
        document.dispatchEvent(new Event('UpdateProfile'));
        showToast('Profile is updated');
    }, profile)
}

export function deleteUser() {
    REST.deleteRaw('/user/delete', () => location.replace('/'));
}