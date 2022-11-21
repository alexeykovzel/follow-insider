import * as REST from '/scripts/common/rest.js';
import {showToast} from '/scripts/ui/popup.js';

class User {
    static isAuthorized = null;
    static profile = null;
}

export function register(details) {
    REST.postForm('/user/register', () => location.replace('/home'), details);
}

export function login(details) {
    REST.postForm('/user/login', () => location.replace('/home'), details);
}

export function logout() {
    REST.postRaw('/user/logout', () => location.replace('/'));
}

export function deleteUser() {
    REST.deleteRaw('/user/delete', () => location.replace('/'));
}

export function updateProfile(profile) {
    REST.postForm('/user/profile/update', () => {
        User.profile = profile;
        document.dispatchEvent(new Event('update-profile'));
        showToast('Profile is updated');
    }, profile)
}

export function loadProfile(load) {
    if (User.profile == null) {
        REST.fetchJson('/user/profile', (profile) => {
            console.log('load profile: ' + JSON.stringify(profile));
            User.profile = profile;
            load(profile);
        });
    } else {
        load(User.profile);
    }
}

export function isAuthorized(then) {
    if (User.isAuthorized == null) {
        REST.fetchJson('/user/roles', (roles) => {
            User.isAuthorized = !roles.find(role => role['authority'] === 'ROLE_ANONYMOUS');
            isAuthorized(then);
        });
    } else if (User.isAuthorized) {
        then();
    }
}