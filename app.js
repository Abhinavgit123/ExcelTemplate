
document.addEventListener('DOMContentLoaded', () => {
    const dataList = document.getElementById('data-list');
    const orgIdSection = document.getElementById('orgId-section');
    const actionButtons = document.getElementById('action-buttons');
    const dataForm = document.getElementById('data-form');

    const orgIdInput = document.getElementById('orgId');
    const submitButton = document.getElementById('submit-button');
    const displayDataButton = document.getElementById('display-data');
    const downloadExcelButton = document.getElementById('download-excel');
    // const fieldNameInput = document.getElementById('field-name');
    // const fieldValueInput = document.getElementById('field-value');

    const dataEntryButton = document.getElementById('data-entry');
    const addDataButton = document.getElementById('add-data');
    const jsonForm = document.getElementById('jsonForm');
    const jsonInput = document.getElementById('json-input');
    const submitJsonButton = document.getElementById('submit-json');
    const jsonDisplay = document.getElementById('json-display');


// Display data functionality
    const fetchData = (orgId) => {
        return fetch(`http://localhost:8080/organizations/${orgId}/retrieve-data`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text(); // Always read as text
            })
            .then(data => {
                try {
                    const jsonData = JSON.parse(data);
                    // Process and display JSON data in your frontend
                    dataList.innerHTML = ''; // Clear existing data
                    if (jsonData && typeof jsonData === 'object') {
                        const columnNames = Object.keys(jsonData.data);
    
                        const tableContainer = document.createElement('div');
                        tableContainer.classList.add('table-container'); // Add a class for styling
    
                        const table = document.createElement('table');
                        table.classList.add('styled-table'); // Add a class for styling
    
                        // Create a header row with column names
                        const headerRow = document.createElement('tr');
                        columnNames.forEach(columnName => {
                            const headerCell = document.createElement('th');
                            headerCell.textContent = columnName;
                            headerRow.appendChild(headerCell);
                        });
                        table.appendChild(headerRow);
    
                        // Create rows with column values
                        const rowCount = Math.max(...columnNames.map(columnName => jsonData.data[columnName].length));
                        for (let rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                            const row = document.createElement('tr');
                            columnNames.forEach(columnName => {
                                const cell = document.createElement('td');
                                cell.textContent = jsonData.data[columnName][rowIndex] || ''; // Handle missing values
                                row.appendChild(cell);
                            });
                            table.appendChild(row);
                        }
    
                        tableContainer.appendChild(table);
                        dataList.appendChild(tableContainer);
                    } else {
                        console.error('Data format is not as expected:', jsonData);
                    }
                } catch (jsonParseError) {
                   document.getElementById('json-result-404').style.display='flex';
                }
            })
            .catch(error => console.error('Error fetching data from the backend:', error));
    };
    
    
    
    const styles = `
    .table-container {
        max-height: 400px;
        overflow-y: auto;
    }

    .styled-table {
        width: 100%;
        border-collapse: collapse;
        margin: 20px 0; /* Add margin for space */
    }

    .styled-table th, .styled-table td {
        padding: 8px;
        border: 1px solid #ddd; /* Add border for cells */
        text-align: center;
    }
`;

// Create a style element and append it to the document head
const styleElement = document.createElement('style');
styleElement.innerHTML = styles;
document.head.appendChild(styleElement);


    // To hide table 
    const hideTable = () => {

    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
        tableContainer.style.display = 'none';
    }
};
    

    // Function to trigger Excel download
    const downloadExcel = (orgId) => {
        // Download Excel file from the backend using the orgId
        fetch(`http://localhost:8080/organizations/${orgId}/download-excel`)
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${orgId}_organization_data.xlsx`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            })
            .catch(error => console.error(error));
    };

    const sendDataToBackend = (jsonData) => {
        const orgId = orgIdInput.value.trim();
        console.log(jsonData);
    
        if (!orgId) {
            console.error('ORGID is required.');
            return;
        }
    
        fetch(`http://localhost:8080/organizations/${orgId}/add-data`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: jsonData.trim(),
        })
        .then(response => response.text()) // Use response.text() to get plain text
        .then(data => {
            // Handle the response from the backend as needed
            console.log('Response from the backend:', data);
        })
        .catch(error => console.error('Error sending data to the backend:', error));
    };
    
    // Adding json data
    addDataButton.addEventListener('click', () => {
        hideTable();

        document.getElementById('json-result-404').style.display='none';
    
        // Show the JSON input and Submit JSON button
        dataEntryButton.style.display = 'flex';
        jsonInput.style.display = 'flex';
        submitJsonButton.style.display = 'flex';
    
        // Clear any existing JSON input
        jsonInput.value = '';
    });

    // After adding json sending data to backend function
    submitJsonButton.addEventListener('click', () => {
        const jsonData = jsonInput.value;
        console.log('jsonData:', jsonData);
        
        if(jsonData===null || jsonData===undefined || jsonData === ''){
            console.log('JSONnexpected');
            document.getElementById('message-to-enter-input').style.display='block';
            return;
        }
    
        if (jsonData) {

             document.getElementById('data-entry').style.display = 'none';

            document.getElementById('json-display').style.display = 'flex';
    
            // Send the JSON data to the backend
            sendDataToBackend(jsonData);
    
    
            // Hide the JSON input field and the "Submit Data" button
            jsonInput.style.display = 'none';
            submitJsonButton.style.display = 'none';
    
            // Show the "Add Data" button again
            addDataButton.style.display = 'block';
        }
    });
    
    

       // Function to show data entry section
       const showDataEntry = () => {
        dataEntry.style.display = 'block';
    };

    // Function to hide data entry section
    const hideDataEntry = () => {
        dataEntry.style.display = 'none';
    };

// After submitting orgid 
    submitButton.addEventListener('click', () => {
        const orgId = orgIdInput.value.trim();
        const currentOrigin = window.location.origin;
        console.log(`The origin of the frontend application is: ${currentOrigin}`);
        if (orgId !== '') {

            actionButtons.style.display = 'flex';
        } else {

            actionButtons.style.display = 'none';
            showDataEntry();
        }
    });


// Add event listener for the "Add Data" button
displayDataButton.addEventListener('click', () => {
    const orgId = orgIdInput.value.trim();

    dataEntryButton.style.display = 'none';
    jsonInput.style.display = 'none';
    submitJsonButton.style.display = 'none';
    document.getElementById('json-display').style.display = 'none';

    console.log('hi');
    if (orgId !== '') {
        fetchData(orgId);
    }
});


// Add an event listener to the "Download Excel" button
downloadExcelButton.addEventListener('click', () => {
    const orgId = orgIdInput.value.trim();
    if (orgId !== '') {
        downloadExcel(orgId);
    }
});
});

