// js/maps.js
let currentMaps = [];
let userIsAdmin = false;

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

    // Check Roles
    const roles = payload.roles || payload.authorities || [];
    userIsAdmin = Array.isArray(roles) ? roles.includes('ROLE_ADMIN') : roles === 'ROLE_ADMIN' || (typeof roles === 'string' && roles.includes('ROLE_ADMIN'));
    
    // Mock admin
    if (username.toLowerCase() === 'admin') userIsAdmin = true;

    if (userIsAdmin) {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        document.getElementById('adminMenu').classList.remove('hidden');

        // Render add button
        const actionContainer = document.getElementById('adminActionContainer');
        actionContainer.innerHTML = `
            <button onclick="openMapModal()" class="btn-primary flex items-center gap-2 px-5 py-2.5 rounded-xl font-semibold shadow-lg shadow-val-red/20">
                <i class="ph-bold ph-plus"></i> Nuevo Mapa
            </button>
        `;
    }

    loadMaps();
});

async function loadMaps() {
    try {
        const response = await apiFetch('/api/maps');
        if (response.ok) {
            currentMaps = await response.json();
            renderMaps(currentMaps);
        } else {
            console.error('Failed to fetch maps');
        }
    } catch (error) {
        console.error('Error fetching maps:', error);
    }
}

function renderMaps(maps) {
    const grid = document.getElementById('mapsGrid');
    grid.innerHTML = '';

    if (maps.length === 0) {
        grid.innerHTML = '<div class="col-span-full text-center py-10 text-gray-500">No hay mapas registrados.</div>';
        return;
    }

    maps.forEach(map => {
        let diffStyle = 'bg-gray-500/20 text-gray-300';
        if (map.difficulty === 'EASY') diffStyle = 'bg-emerald-500/20 text-emerald-300';
        else if (map.difficulty === 'MEDIUM') diffStyle = 'bg-yellow-500/20 text-yellow-300';
        else if (map.difficulty === 'HARD') diffStyle = 'bg-red-500/20 text-red-300';

        let adminButtons = '';
        if (userIsAdmin) {
            adminButtons = `
                <div class="flex gap-2 mt-4 pt-4 border-t border-white/5">
                    <button class="flex-1 bg-white/5 hover:bg-indigo-500/20 hover:text-indigo-300 text-gray-300 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); editMap(${map.id})">
                        <i class="ph ph-pencil-simple text-sm"></i> Editar
                    </button>
                    <button class="flex-1 bg-red-500/10 hover:bg-red-500/20 text-red-400 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); deleteMap(${map.id})">
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
                    <h3 class="text-xl font-bold text-white group-hover:text-val-red transition-colors">${map.name}</h3>
                    <span class="bg-indigo-500/20 text-indigo-300 px-2 py-0.5 rounded text-[10px] uppercase font-bold tracking-widest mt-2 inline-block">${map.type}</span>
                    <span class="${diffStyle} px-2 py-0.5 rounded text-[10px] uppercase font-bold tracking-widest mt-2 inline-block ml-1">${map.difficulty}</span>
                </div>
                <div class="w-12 h-12 rounded bg-white/5 flex items-center justify-center font-bold text-xl border border-white/10 text-white/50">
                    ${map.name.substring(0, 1)}
                </div>
            </div>
            
            <div class="stats-container hidden flex-col mt-6 pt-4 border-t border-white/5 transition-all duration-300">
                <p class="text-sm text-gray-300 italic mb-4">${map.description || 'Sin descripción'}</p>
                <div class="grid grid-cols-2 gap-y-4 gap-x-2 text-sm mb-2">
                    <div class="bg-black/20 p-2 rounded-lg text-center">
                        <span class="text-gray-500 uppercase text-[9px] font-bold tracking-wider block mb-1">Total de Sites</span>
                        <span class="font-semibold text-white">${map.totalSite || '-'}</span>
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

    document.querySelectorAll('#mapsGrid .glass-card').forEach(card => {
        const stats = card.querySelector('.stats-container');
        if (stats) {
            stats.classList.add('hidden');
            stats.classList.remove('flex');
            card.classList.remove('border-val-red/30', 'bg-white/5');
        }
    });

    if (isCurrentlyHidden) {
        const statsContainer = cardElement.querySelector('.stats-container');
        statsContainer.classList.remove('hidden');
        statsContainer.classList.add('flex');
        cardElement.classList.add('border-val-red/30', 'bg-white/5');
    }
}

async function deleteMap(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este mapa?')) {
        try {
            const response = await apiFetch(`/api/maps/${id}`, { method: 'DELETE' });
            if (response.ok) {
                showToast('Mapa eliminado', 'success');
                loadMaps(); 
            } else {
                showToast('Error al eliminar el mapa', 'error');
            }
        } catch (error) {
            console.error(error);
            showToast('Error de red al eliminar', 'error');
        }
    }
}

const mapModal = document.getElementById('mapModal');
const mapModalContent = document.getElementById('mapModalContent');

function openMapModal(id = null) {
    const form = document.getElementById('mapForm');
    form.reset();
    document.getElementById('mapId').value = '';
    document.getElementById('mapModalTitle').textContent = 'Nuevo Mapa';

    if (id) {
        const map = currentMaps.find(m => m.id === id);
        if (map) {
            document.getElementById('mapModalTitle').textContent = 'Editar Mapa';
            document.getElementById('mapId').value = map.id;
            document.getElementById('mapName').value = map.name;
            document.getElementById('mapType').value = map.type;
            document.getElementById('mapDifficulty').value = map.difficulty;
            document.getElementById('mapSites').value = map.totalSite;
            document.getElementById('mapDescription').value = map.description || '';
        }
    }

    mapModal.classList.remove('hidden');
    void mapModal.offsetWidth;
    mapModal.classList.remove('opacity-0');
    mapModalContent.classList.remove('scale-95');
    mapModalContent.classList.add('scale-100');
}

function closeMapModal() {
    mapModal.classList.add('opacity-0');
    mapModalContent.classList.remove('scale-100');
    mapModalContent.classList.add('scale-95');
    setTimeout(() => {
        mapModal.classList.add('hidden');
    }, 300);
}

document.getElementById('mapForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const id = document.getElementById('mapId').value;
    const mapData = {
        name: document.getElementById('mapName').value,
        type: document.getElementById('mapType').value,
        difficulty: document.getElementById('mapDifficulty').value,
        totalSite: parseInt(document.getElementById('mapSites').value),
        description: document.getElementById('mapDescription').value
    };

    try {
        const url = id ? `/api/maps/${id}` : '/api/maps';
        const method = id ? 'PUT' : 'POST';
        
        const response = await apiFetch(url, {
            method: method,
            body: JSON.stringify(mapData)
        });

        if (response.ok) {
            showToast(id ? 'Mapa actualizado' : 'Mapa creado', 'success');
            closeMapModal();
            loadMaps();
        } else {
            const errorData = await response.json();
            showToast('Error: ' + (errorData.message || 'Error desconocido'), 'error');
        }
    } catch (error) {
        console.error(error);
        showToast('Error de red al guardar el mapa', 'error');
    }
});

function editMap(id) {
    openMapModal(id);
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.querySelector('span').textContent = message;
    
    toast.classList.remove('opacity-0', 'translate-y-4');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}
