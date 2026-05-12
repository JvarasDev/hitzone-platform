// js/dashboard.js
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('hitzone_token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    const payload = decodeJwt(token);
    if (!payload) {
        logout();
        return;
    }

    // Set Username
    const username = payload.sub || 'Usuario';
    document.getElementById('userNameDisplay').textContent = username;
    document.getElementById('userAvatar').textContent = username.substring(0, 2).toUpperCase();

    // Set Role and Admin access
    const roles = payload.roles || payload.authorities || [];
    const isAdmin = Array.isArray(roles) ? roles.includes('ROLE_ADMIN') : roles === 'ROLE_ADMIN' || (typeof roles === 'string' && roles.includes('ROLE_ADMIN'));
    
    // Mock admin check for dev
    const isMockAdmin = username.toLowerCase() === 'admin'; 

    if (isAdmin || isMockAdmin) {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        
        // Show admin sections
        document.getElementById('adminMenu').classList.remove('hidden');
        document.getElementById('adminSection').classList.remove('hidden');
    }
});

// Modal Logic
const modal = document.getElementById('sessionModal');
const modalContent = document.getElementById('modalContent');

function openModal() {
    modal.classList.remove('hidden');
    // Trigger reflow
    void modal.offsetWidth;
    modal.classList.remove('opacity-0');
    modalContent.classList.remove('scale-95');
    modalContent.classList.add('scale-100');
}

function closeModal() {
    modal.classList.add('opacity-0');
    modalContent.classList.remove('scale-100');
    modalContent.classList.add('scale-95');
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 300);
}

document.getElementById('sessionForm').addEventListener('submit', (e) => {
    e.preventDefault();
    closeModal();
    showToast('Sesión registrada exitosamente.', 'success');
});

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    // update icon based on type if needed, assume checkmark for now
    toast.querySelector('span').textContent = message;
    
    toast.classList.remove('opacity-0', 'translate-y-4');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}

