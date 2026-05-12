// js/api.js
const isFileProtocol = window.location.protocol === 'file:';

// API Gateway base URL
const API_BASE_URL = isFileProtocol ? 'http://localhost:8080' : '';

// Helper for making API calls with JWT token
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('hitzone_token');
    
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        
        if (response.status === 401 || response.status === 403) {
            // Unauthorized or Forbidden, might need to logout
            if (!endpoint.includes('/api/auth/')) {
                logout();
            }
        }
        
        return response;
    } catch (error) {
        console.error("API Fetch Error:", error);
        throw error;
    }
}

function logout() {
    localStorage.removeItem('hitzone_token');
    window.location.href = '/login.html';
}

function decodeJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch(e) {
        return null;
    }
}
