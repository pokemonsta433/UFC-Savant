document.addEventListener("DOMContentLoaded", function () {
    const input = document.querySelectorAll("[data-search-input]");

    input.forEach((searchInput) => {
        const autocompleteList = searchInput.nextElementSibling;
        let currentFighters = []; // To store the fetched fighters

        searchInput.addEventListener("input", function () {
            const query = this.value;
            if (!query) {
                autocompleteList.innerHTML = "";
                autocompleteList.classList.remove("show"); // Hide the dropdown
                return;
            }

            fetch(`/api/fighters?search=${query}`)
                .then((response) => response.json())
                .then((fighters) => {
                    currentFighters = fighters; // Store fetched fighters
                    autocompleteList.innerHTML = "";
                    if (fighters.length > 0) {
                        autocompleteList.classList.add("show"); // Show the dropdown

                        // Limit to the first 5 fighters
                        const limitedFighters = fighters.slice(0, 5);

                        limitedFighters.forEach((fighter) => {
                            const item = document.createElement("div");
                            item.classList.add("autocomplete-item");
                            item.textContent = fighter;
                            item.addEventListener("click", function () {
                                window.location.href = `/fighter/${fighter}`;
                            });
                            autocompleteList.appendChild(item);
                        });
                    } else {
                        autocompleteList.classList.remove("show"); // Hide if no results
                    }
                })
                .catch((error) => console.error("Error fetching fighters:", error));
        });

        document.addEventListener("click", function (e) {
            if (e.target !== searchInput) {
                autocompleteList.innerHTML = ""; // Close the dropdown if clicked outside
                autocompleteList.classList.remove("show"); // Hide the dropdown
            }
        });

        searchInput.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                e.preventDefault();
                // If there are fighters in the autocomplete list, navigate to the first one
                if (currentFighters.length > 0) {
                    window.location.href = `/fighter/${currentFighters[0]}`;
                } else {
                    // If no fighters, navigate based on the current search string
                    window.location.href = `/fighter/${searchInput.value}`;
                }
            }
        });
    });
});
