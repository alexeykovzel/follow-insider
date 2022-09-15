export class Table {
    /**
     * @param {*} id            id attribute of a html element
     * @param {*} columns       column names of the table
     * @param {*} fractions     column fractions of the table
     */
    constructor(id, columns, fractions) {
        this.id = id;
        this.columns = columns;
        this.fractions = fractions;
    }

    init() {
        // set column fractions
        if (this.fractions === null) {
            this.fractions = Array(this.columns.length).fill(1);
        }
        let table = $("#" + this.id);
        let template = this.fractions.map(val => val + "fr").join(" ");
        table.find("tr").css("grid-template-columns", template);
    }

    get html() {
        return `
            <div id="${this.id}" class="table-wrapper scrollbar">
                <table>
                    <thead>
                        <tr>
                            ${this.columns.map(col => "<th>" + col + "</th>").join("")}
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        `;
    }
}

export function addAll(table, rows) {
    $("#loader").remove();
    rows.forEach(row => table.append(row));
}

export function reset(table) {
    table.empty();
    $("#loader").remove();
    addLoader(table)
}

export function addLoader(table) {
    table.append(`
        <div id="loader" class="center">
            <div class="lds-facebook"><div></div><div></div><div></div></div>
        </div>
    `);
}