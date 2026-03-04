function filterTable() {
        const input = document.getElementById("nameFilter");
        const filter = input.value.toUpperCase();
        const table = document.getElementById("chatTable");
        const tr = table.getElementsByTagName("tr");
        for (let i = 1; i < tr.length; i++) {
            let td = tr[i].getElementsByTagName("td")[1]; // Filename column
            if (td) {
                let txtValue = td.textContent || td.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }

    let currentSort = { column: 3, order: 'desc' }; // Default sort by Last Modified Desc

    function sortTable(columnIndex, type, button) {
        const table = document.getElementById("chatTable");
        const tbody = table.tBodies[0];
        const rows = Array.from(tbody.getElementsByTagName("tr"));

        let order = 'asc';
        if (currentSort.column === columnIndex && currentSort.order === 'asc') {
            order = 'desc';
        }
        currentSort = { column: columnIndex, order: order };
        // Update active button style
        document.querySelectorAll('.filter-controls button').forEach(btn => btn.classList.remove('active-sort'));
        button.classList.add('active-sort');
        rows.sort((a, b) => {
            const cellA = a.getElementsByTagName("td")[columnIndex].innerText;
            const cellB = b.getElementsByTagName("td")[columnIndex].innerText;

            let valA, valB;
            if (type === 'date') {
                valA = new Date(cellA);
                valB = new Date(cellB);
            } else { // string
                valA = cellA.toLowerCase();
                valB = cellB.toLowerCase();
            }
            
            if (valA < valB) {
                return order === 'asc' ? -1 : 1;
            }
            if (valA > valB) {
                return order === 'asc' ? 1 : -1;
            }
            return 0;
        });

        rows.forEach(row => tbody.appendChild(row));
    }