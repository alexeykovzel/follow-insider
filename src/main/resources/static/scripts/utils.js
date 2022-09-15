export function formatNumber(val) {
    val = Math.abs(Number(val));
    if (val >= 1.0e+9) return (val / 1.0e+9).toFixed(1) + 'B';
    if (val >= 1.0e+6) return (val / 1.0e+6).toFixed(1) + 'M';
    if (val >= 1.0e+3) return (val / 1.0e+3).toFixed(1) + 'K';
    return val.toFixed(0);
}

export function uniqueMerge(arr) {
    return unique(merge(arr));
}

export function unique(arr) {
    return arr.filter((value, index, self) => {
        return self.indexOf(value) === index
    });
}

export function merge(arr) {
    return [].concat.apply([], arr);
}

export function formatDate(val) {
    let options = {month: 'short', day: 'numeric', year: 'numeric'};
    return new Date(val).toLocaleDateString('en-US', options);
}

export function formatMoney(val) {
    return val.toFixed(1) + "$";
}