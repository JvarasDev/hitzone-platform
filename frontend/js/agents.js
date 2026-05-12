// js/agents.js
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
    const isMockAdmin = true; 

    if (isAdmin || isMockAdmin) {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        
        // Mostrar botón de agregar y habilitar acciones
        const addBtn = document.getElementById('addAgentBtn');
        if(addBtn) addBtn.classList.remove('hidden');
        
        // Mostrar menú admin
        const adminMenu = document.getElementById('adminMenu');
        if(adminMenu) adminMenu.classList.remove('hidden');
    }

    // Load agents for everyone
    loadAgents(isAdmin || isMockAdmin);
});

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.querySelector('span').textContent = message;
    
    toast.classList.remove('opacity-0', 'translate-y-4');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}

let currentAgents = [];
let userIsAdmin = false;

async function loadAgents(isAdmin) {
    userIsAdmin = isAdmin;
    try {
        const response = await apiFetch('/api/agents');
        if (response.ok) {
            currentAgents = await response.json();
            renderAgentsGrid(currentAgents);
        } else {
            console.error('Error fetching agents', response.statusText);
        }
    } catch (error) {
        console.error('Error in loadAgents:', error);
    }
}

function renderAgentsGrid(agents) {
    const grid = document.getElementById('agentsGrid');
    if (!grid) return;

    grid.innerHTML = '';
    
    if (agents.length === 0) {
        grid.innerHTML = `<div class="col-span-full text-center py-12 text-gray-400">No hay agentes registrados.</div>`;
        return;
    }

    agents.forEach(agent => {
        let roleStyle = 'bg-white/10 text-white';
        let roleName = agent.role;
        if (agent.role === 'DUELIST') { roleStyle = 'bg-red-500/20 text-red-300'; roleName = 'Duelista'; }
        else if (agent.role === 'CONTROLLER') { roleStyle = 'bg-indigo-500/20 text-indigo-300'; roleName = 'Controlador'; }
        else if (agent.role === 'INITIATOR') { roleStyle = 'bg-emerald-500/20 text-emerald-300'; roleName = 'Iniciador'; }
        else if (agent.role === 'SENTINEL') { roleStyle = 'bg-yellow-500/20 text-yellow-300'; roleName = 'Centinela'; }

        let adminButtons = '';
        if (userIsAdmin) {
            adminButtons = `
                <div class="flex gap-2 mt-4 pt-4 border-t border-white/5">
                    <button class="flex-1 bg-white/5 hover:bg-indigo-500/20 hover:text-indigo-300 text-gray-300 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); editAgent(${agent.id})">
                        <i class="ph ph-pencil-simple text-sm"></i> Editar
                    </button>
                    <button class="flex-1 bg-red-500/10 hover:bg-red-500/20 text-red-400 py-2 rounded-lg text-xs font-semibold transition-colors flex items-center justify-center gap-2" onclick="event.stopPropagation(); deleteAgent(${agent.id})">
                        <i class="ph ph-trash text-sm"></i> Eliminar
                    </button>
                </div>
            `;
        }

        const card = document.createElement('div');
        card.className = 'glass-card p-6 flex flex-col cursor-pointer group hover:-translate-y-1 transition-all duration-300 relative overflow-hidden';
        card.onclick = function() { toggleCard(this); };
        
        let avatarHtml = agent.imageUrl 
            ? `<img src="${agent.imageUrl}" class="w-16 h-16 rounded-full object-cover border-2 border-white/10 shadow-lg" alt="${agent.name}">` 
            : `<div class="w-16 h-16 rounded-full bg-white/5 border-2 border-white/10 flex items-center justify-center text-xl font-bold">${agent.name.substring(0, 1)}</div>`;

        let abilitiesHtml = '';
        if (agent.abilities && agent.abilities.length > 0) {
            abilitiesHtml = '<div class="mt-4 pt-4 border-t border-white/5 grid grid-cols-4 gap-2">';
            const order = { 'Q': 1, 'E': 2, 'C': 3, 'ULTIMATE': 4 };
            const sortedAbs = [...agent.abilities].sort((a, b) => order[a.type] - order[b.type]);
            sortedAbs.forEach(ab => {
                let color = 'text-white';
                if(ab.type === 'E') color = 'text-emerald-400';
                if(ab.type === 'C') color = 'text-indigo-400';
                if(ab.type === 'ULTIMATE') color = 'text-val-red';
                abilitiesHtml += `
                    <div class="col-span-1 bg-black/20 rounded-lg p-2 flex flex-col items-center justify-center text-center group relative" title="${ab.name}: ${ab.description}">
                        <span class="text-[10px] font-bold ${color} mb-1">${ab.type === 'ULTIMATE' ? 'ULT' : ab.type}</span>
                        <span class="text-[9px] text-gray-400 truncate w-full" style="max-width: 100%;">${ab.name}</span>
                    </div>
                `;
            });
            abilitiesHtml += '</div>';
        }

        card.innerHTML = `
            <div class="flex justify-between items-start z-10 relative">
                <div class="flex gap-4 items-center w-full">
                    ${avatarHtml}
                    <div class="flex-1">
                        <h3 class="text-xl font-bold text-white group-hover:text-val-red transition-colors">${agent.name}</h3>
                        <span class="${roleStyle} px-2 py-0.5 rounded text-[10px] uppercase font-bold tracking-widest mt-1 inline-block">${roleName}</span>
                    </div>
                </div>
            </div>
            
            <!-- Stats expandidos, ocultos por defecto -->
            <div class="stats-container hidden flex-col mt-6 pt-4 border-t border-white/5 transition-all duration-300 z-10 relative">
                <p class="text-sm text-gray-300 italic mb-2">${agent.description || 'Sin descripción disponible.'}</p>
                ${abilitiesHtml}
                ${adminButtons}
            </div>
        `;
        grid.appendChild(card);
    });
}

function toggleCard(cardElement) {
    const isCurrentlyHidden = cardElement.querySelector('.stats-container').classList.contains('hidden');

    document.querySelectorAll('#agentsGrid .glass-card').forEach(card => {
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

async function deleteAgent(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este agente?')) {
        try {
            const response = await apiFetch(`/api/agents/${id}`, { method: 'DELETE' });
            if (response.ok) {
                showToast('Agente eliminado', 'success');
                loadAgents(userIsAdmin); 
            } else {
                showToast('Error al eliminar el agente', 'error');
            }
        } catch (error) {
            console.error(error);
            showToast('Error de red al eliminar', 'error');
        }
    }
}

// Modal Agent Logic
const agentModal = document.getElementById('agentModal');
const agentModalContent = document.getElementById('agentModalContent');

function openAgentModal(id = null) {
    const form = document.getElementById('agentForm');
    form.reset();
    document.getElementById('agentId').value = '';
    document.getElementById('agentModalTitle').textContent = 'Nuevo Agente';

    if (id) {
        const agent = currentAgents.find(a => a.id === id);
        if (agent) {
            document.getElementById('agentModalTitle').textContent = 'Editar Agente';
            document.getElementById('agentId').value = agent.id;
            document.getElementById('agentName').value = agent.name;
            document.getElementById('agentRole').value = agent.role;
            document.getElementById('agentDescription').value = agent.description || '';
            document.getElementById('agentImageUrl').value = agent.imageUrl || '';

            // Populate abilities if present
            if(agent.abilities) {
                const q = agent.abilities.find(a => a.type === 'Q');
                if(q) { document.getElementById('abQName').value = q.name; document.getElementById('abQDesc').value = q.description; }
                const e = agent.abilities.find(a => a.type === 'E');
                if(e) { document.getElementById('abEName').value = e.name; document.getElementById('abEDesc').value = e.description; }
                const c = agent.abilities.find(a => a.type === 'C');
                if(c) { document.getElementById('abCName').value = c.name; document.getElementById('abCDesc').value = c.description; }
                const x = agent.abilities.find(a => a.type === 'ULTIMATE');
                if(x) { document.getElementById('abXName').value = x.name; document.getElementById('abXDesc').value = x.description; }
            }
        }
    }

    agentModal.classList.remove('hidden');
    void agentModal.offsetWidth;
    agentModal.classList.remove('opacity-0');
    agentModalContent.classList.remove('scale-95');
    agentModalContent.classList.add('scale-100');
}

function closeAgentModal() {
    agentModal.classList.add('opacity-0');
    agentModalContent.classList.remove('scale-100');
    agentModalContent.classList.add('scale-95');
    setTimeout(() => {
        agentModal.classList.add('hidden');
    }, 300);
}

document.getElementById('agentForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const id = document.getElementById('agentId').value;
    const agentData = {
        name: document.getElementById('agentName').value,
        role: document.getElementById('agentRole').value,
        description: document.getElementById('agentDescription').value,
        imageUrl: document.getElementById('agentImageUrl').value,
        abilities: [
            { type: 'Q', name: document.getElementById('abQName').value, description: document.getElementById('abQDesc').value },
            { type: 'E', name: document.getElementById('abEName').value, description: document.getElementById('abEDesc').value },
            { type: 'C', name: document.getElementById('abCName').value, description: document.getElementById('abCDesc').value },
            { type: 'ULTIMATE', name: document.getElementById('abXName').value, description: document.getElementById('abXDesc').value }
        ]
    };

    try {
        const url = id ? `/api/agents/${id}` : '/api/agents';
        const method = id ? 'PUT' : 'POST';
        
        const response = await apiFetch(url, {
            method: method,
            body: JSON.stringify(agentData)
        });

        if (response.ok) {
            showToast(id ? 'Agente actualizado' : 'Agente creado', 'success');
            closeAgentModal();
            loadAgents(userIsAdmin);
        } else {
            const errorText = await response.text();
            showToast('Error: ' + errorText, 'error');
        }
    } catch (error) {
        console.error(error);
        showToast('Error de red al guardar el agente', 'error');
    }
});

function editAgent(id) {
    openAgentModal(id);
}
