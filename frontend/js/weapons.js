// js/weapons.js
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
    
    // Mock admin check for dev (temporalmente habilitado para todos los usuarios)
    const isMockAdmin = true; // Antes: username.toLowerCase() === 'admin'; 

    if (isAdmin || isMockAdmin) {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        
        // Mostrar botón de agregar y habilitar acciones
        const addBtn = document.getElementById('addWeaponBtn');
        if(addBtn) addBtn.classList.remove('hidden');
        
        // Mostrar menú admin
        const adminMenu = document.getElementById('adminMenu');
        if(adminMenu) adminMenu.classList.remove('hidden');
    }

    // Load weapons for everyone, but actions (edit/delete) are for admins
    loadWeapons(isAdmin || isMockAdmin);
});

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.querySelector('span').textContent = message;
    
    toast.classList.remove('opacity-0', 'translate-y-4');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}

// Global variable to store current weapons for editing
let currentWeapons = [];
let userIsAdmin = false;

async function loadWeapons(isAdmin) {
    userIsAdmin = isAdmin;
    try {
        const response = await apiFetch('/api/weapons');
        if (response.ok) {
            currentWeapons = await response.json();
            renderWeaponsGrid(currentWeapons);
        } else {
            console.error('Error fetching weapons', response.statusText);
        }
    } catch (error) {
        console.error('Error in loadWeapons:', error);
    }
}

function renderWeaponsGrid(weapons) {
    const grid = document.getElementById('weaponsGrid');
    if (!grid) return;

    grid.innerHTML = '';
    
    if (weapons.length === 0) {
        grid.innerHTML = `<div class="col-span-full text-center py-12 text-gray-400">No hay armas registradas.</div>`;
        return;
    }

    weapons.forEach(weapon => {
        let catStyle = 'bg-white/10 text-white';
        if (weapon.category === 'SNIPER') catStyle = 'bg-indigo-500/20 text-indigo-300';
        else if (weapon.category === 'PISTOL') catStyle = 'bg-emerald-500/20 text-emerald-300';
        else if (weapon.category === 'HEAVY') catStyle = 'bg-red-500/20 text-red-300';
        else if (weapon.category === 'RIFLE') catStyle = 'bg-val-red/20 text-val-red border border-val-red/10';

        let adminButtons = '';
        if (userIsAdmin) {
            adminButtons = `
                <div class="flex gap-2 mt-4 pt-4 border-t border-white/5">
                    <button class="flex-1 bg-white/5 hover:bg-indigo-500/20 hover:text-indigo-300 text-gray-300 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); editWeapon(${weapon.id})">
                        <i class="ph ph-pencil-simple text-sm"></i> Editar
                    </button>
                    <button class="flex-1 bg-red-500/10 hover:bg-red-500/20 text-red-400 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); deleteWeapon(${weapon.id})">
                        <i class="ph ph-trash text-sm"></i> Eliminar
                    </button>
                </div>
            `;
        }

        const card = document.createElement('div');
        card.className = 'glass-card p-6 flex flex-col cursor-pointer group hover:-translate-y-1 transition-all duration-300';
        card.onclick = function() { toggleCard(this); };

        card.innerHTML = `
            <div class="flex justify-between items-start">
                <div>
                    <h3 class="text-xl font-bold text-white group-hover:text-val-red transition-colors">${weapon.name}</h3>
                    <span class="${catStyle} px-2 py-0.5 rounded text-[10px] uppercase font-bold tracking-widest mt-2 inline-block">${weapon.category}</span>
                </div>
                <div class="font-mono text-gray-400 font-semibold bg-black/40 px-2 py-1 rounded text-sm">${weapon.price ? weapon.price + ' ¤' : '-'}</div>
            </div>
            
            <!-- Stats expandidos, ocultos por defecto -->
            <div class="stats-container hidden flex-col mt-6 pt-4 border-t border-white/5 transition-all duration-300">
                <div class="grid grid-cols-2 gap-y-4 gap-x-2 text-sm mb-2">
                    <div class="bg-black/20 p-2 rounded-lg">
                        <span class="text-gray-500 uppercase text-[9px] font-bold tracking-wider block mb-1">Daño Cuerpo</span>
                        <span class="font-semibold text-white">${weapon.damage || '-'}</span>
                    </div>
                    <div class="bg-black/20 p-2 rounded-lg">
                        <span class="text-gray-500 uppercase text-[9px] font-bold tracking-wider block mb-1">Fire Rate</span>
                        <span class="font-semibold text-white">${weapon.fireRate ? weapon.fireRate + ' r/s' : '-'}</span>
                    </div>
                    <div class="bg-black/20 p-2 rounded-lg col-span-2 flex justify-between items-center">
                        <span class="text-gray-500 uppercase text-[9px] font-bold tracking-wider">Cargador</span>
                        <span class="font-semibold text-white">${weapon.magazineSize ? weapon.magazineSize + ' balas' : '-'}</span>
                    </div>
                </div>
                ${adminButtons}
            </div>
        `;
        grid.appendChild(card);
    });
}

function toggleCard(cardElement) {
    const isCurrentlyHidden = cardElement.querySelector('.stats-container').classList.contains('hidden');

    // Primero cerramos todas las cards
    document.querySelectorAll('#weaponsGrid .glass-card').forEach(card => {
        const stats = card.querySelector('.stats-container');
        if (stats) {
            stats.classList.add('hidden');
            stats.classList.remove('flex');
            card.classList.remove('border-val-red/30', 'bg-white/5');
        }
    });

    // Si la card clickeada estaba cerrada, la abrimos
    if (isCurrentlyHidden) {
        const statsContainer = cardElement.querySelector('.stats-container');
        statsContainer.classList.remove('hidden');
        statsContainer.classList.add('flex');
        cardElement.classList.add('border-val-red/30', 'bg-white/5');
    }
}

async function deleteWeapon(id) {
    if (confirm('¿Estás seguro de que deseas eliminar esta arma?')) {
        try {
            const response = await apiFetch(`/api/weapons/${id}`, { method: 'DELETE' });
            if (response.ok) {
                showToast('Arma eliminada', 'success');
                loadWeapons(userIsAdmin); 
            } else {
                showToast('Error al eliminar el arma', 'error');
            }
        } catch (error) {
            console.error(error);
            showToast('Error de red al eliminar', 'error');
        }
    }
}

// Modal Weapon Logic
const weaponModal = document.getElementById('weaponModal');
const weaponModalContent = document.getElementById('weaponModalContent');

function openWeaponModal(id = null) {
    const form = document.getElementById('weaponForm');
    form.reset();
    document.getElementById('weaponId').value = '';
    document.getElementById('weaponModalTitle').textContent = 'Nueva Arma';

    if (id) {
        const weapon = currentWeapons.find(w => w.id === id);
        if (weapon) {
            document.getElementById('weaponModalTitle').textContent = 'Editar Arma';
            document.getElementById('weaponId').value = weapon.id;
            document.getElementById('weaponName').value = weapon.name;
            document.getElementById('weaponCategory').value = weapon.category;
            document.getElementById('weaponPrice').value = weapon.price;
            document.getElementById('weaponDamage').value = weapon.damage;
            document.getElementById('weaponFireRate').value = weapon.fireRate;
            document.getElementById('weaponMagazine').value = weapon.magazineSize;
        }
    }

    weaponModal.classList.remove('hidden');
    void weaponModal.offsetWidth;
    weaponModal.classList.remove('opacity-0');
    weaponModalContent.classList.remove('scale-95');
    weaponModalContent.classList.add('scale-100');
}

function closeWeaponModal() {
    weaponModal.classList.add('opacity-0');
    weaponModalContent.classList.remove('scale-100');
    weaponModalContent.classList.add('scale-95');
    setTimeout(() => {
        weaponModal.classList.add('hidden');
    }, 300);
}

document.getElementById('weaponForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const id = document.getElementById('weaponId').value;
    const weaponData = {
        name: document.getElementById('weaponName').value,
        category: document.getElementById('weaponCategory').value,
        price: parseInt(document.getElementById('weaponPrice').value),
        damage: parseFloat(document.getElementById('weaponDamage').value),
        fireRate: parseFloat(document.getElementById('weaponFireRate').value),
        magazineSize: parseInt(document.getElementById('weaponMagazine').value),
        description: ""
    };

    try {
        const url = id ? `/api/weapons/${id}` : '/api/weapons';
        const method = id ? 'PUT' : 'POST';
        
        const response = await apiFetch(url, {
            method: method,
            body: JSON.stringify(weaponData)
        });

        if (response.ok) {
            showToast(id ? 'Arma actualizada' : 'Arma creada', 'success');
            closeWeaponModal();
            loadWeapons(userIsAdmin);
        } else {
            const errorText = await response.text();
            showToast('Error: ' + errorText, 'error');
        }
    } catch (error) {
        console.error(error);
        showToast('Error de red al guardar el arma', 'error');
    }
});

function editWeapon(id) {
    openWeaponModal(id);
}
