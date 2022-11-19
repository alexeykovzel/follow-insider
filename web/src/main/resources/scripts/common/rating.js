let ratings = [
    {value: 'Very good', box: 'green-box', color: 'var(--very-good)', from: 9, to: 10},
    {value: 'Very bad', box: 'red-box', color: 'var(--very-bad)', from: 0, to: 1},
    {value: 'So-so', box: 'yellow-box', color: 'var(--so-so)', from: 4, to: 6},
    {value: 'Good', box: 'green-box', color: 'var(--good)', from: 7, to: 8},
    {value: 'Bad', box: 'red-box', color: 'var(--bad)', from: 2, to: 3}
];

export function initScore(boxes, score) {
    let defaultBoxPath = getDefaultBoxPath();
    let boxPath = getBoxPathByScore(score);

    for (let i = 0; i < 10; i++) {
        let finalPath = (score > i) ? boxPath : defaultBoxPath;
        boxes.innerHTML += `<img src="${finalPath}" alt="Box Image">`;
    }
}

function getBoxPathByScore(score) {
    let rating = ratings.find(r => (score >= r.from) && (score <= r.to));
    if (rating == null) return getDefaultBoxPath();
    return getBoxPathByName(rating.box);
}

function getDefaultBoxPath() {
    return getBoxPathByName('grey-box');
}

function getBoxPathByName(name) {
    return `/images/boxes/${name}.jpg`;
}