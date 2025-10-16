// Toggle module dropdown
function toggleModule(moduleId) {
    const dropdown = document.getElementById(`dropdown-${moduleId}`);
    const arrow = document.getElementById(`arrow-${moduleId}`);

    dropdown.classList.toggle('open');
    arrow.classList.toggle('open');
}

// Load page content
function loadPage(module, page) {
    event.preventDefault();
    const contentBody = document.getElementById('content-body');

    // Show loading
    contentBody.innerHTML = '<div class="loading">Loading...</div>';

    // Load module page
    fetch(`modules/${module}/${page}.html`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Page not found');
            }
            return response.text();
        })
        .then(html => {
            contentBody.innerHTML = html;
            // Execute any scripts in the loaded content
            const scripts = contentBody.querySelectorAll('script');
            scripts.forEach(script => {
                const newScript = document.createElement('script');
                newScript.textContent = script.textContent;
                document.body.appendChild(newScript);
                document.body.removeChild(newScript);
            });
        })
        .catch(error => {
            contentBody.innerHTML = `
                <div class="page-container">
                    <div class="error-message">
                        Failed to load page: ${error.message}
                    </div>
                </div>
            `;
        });
}

// Capitalize first letter
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Show message
function showMessage(message, type = 'success') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `${type}-message`;
    messageDiv.textContent = message;

    const contentBody = document.getElementById('content-body');
    contentBody.insertBefore(messageDiv, contentBody.firstChild);

    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}

// Customer functions
window.submitCustomer = function() {
    const form = document.getElementById('new-customer-form');
    const container = document.getElementById('message-container');

    if (!form.checkValidity()) {
        form.reportValidity();
        return false;
    }

    const data = {
        firstName: document.getElementById('customer-firstname').value,
        lastName: document.getElementById('customer-lastname').value,
        email: document.getElementById('customer-email').value,
        phone: document.getElementById('customer-phone').value,
        address: document.getElementById('customer-address').value
    };

    fetch('/centralizer/customers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to create customer');
        }
        return response.json();
    })
    .then(result => {
        container.innerHTML = '<div class="success-message">Customer created successfully!</div>';
        form.reset();
        setTimeout(() => container.innerHTML = '', 3000);
    })
    .catch(error => {
        container.innerHTML = '<div class="error-message">Error: ' + error.message + '</div>';
        setTimeout(() => container.innerHTML = '', 3000);
    });

    return false;
};
