// TODO: onload function should retrieve the data needed to populate the UI

window.onload = function() {
  // Make a GET request to the API endpoint that returns the spam analysis results
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/results")
    .then(response => response.json())
    .then(data => {
      const tableBody = document.querySelector("tbody");
      data.forEach(result => {
        const row = tableBody.insertRow();
        const filenameCell = row.insertCell();
        const categorizationCell = row.insertCell();
        const actualCategoryCell = row.insertCell();
        filenameCell.textContent = result.filename;
        categorizationCell.textContent = result.categorization;
        actualCategoryCell.textContent = result.actualCategory;
      });
    })
    .catch(error => console.error(error));
}
