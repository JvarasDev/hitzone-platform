// js/rankings.js
let currentRanks = [];
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
    
    if (username.toLowerCase() === 'admin') userIsAdmin = true;

    if (userIsAdmin) {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        document.getElementById('adminMenu').classList.remove('hidden');
    }

    loadRankings();
});

async function loadRankings() {
    try {
        const response = await apiFetch('/api/v1/ranks');
        if (response.ok) {
            currentRanks = await response.json();
            // Sort by Rank Tier (desc) and RR (desc) - simple mock sort based on RR assuming higher RR is better overall for now
            // To do this properly we'd need a rank enum weight
            currentRanks.sort((a, b) => b.rankRating - a.rankRating);
            renderRankings(currentRanks);
        } else {
            console.error('Failed to fetch ranks');
        }
    } catch (error) {
        console.error('Error fetching ranks:', error);
    }
}

function renderRankings(ranks) {
    const tbody = document.getElementById('rankingsTableBody');
    tbody.innerHTML = '';

    if (!ranks || ranks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="pl-6 py-8 text-center text-gray-500">No hay jugadores clasificados aún.</td></tr>';
        return;
    }

    ranks.forEach((rank, index) => {
        let rankStyle = 'text-gray-400';
        if (rank.currentTier === 'RADIANT') rankStyle = 'text-yellow-400 font-bold';
        else if (rank.currentTier === 'IMMORTAL') rankStyle = 'text-red-500 font-bold';
        else if (rank.currentTier === 'ASCENDANT') rankStyle = 'text-emerald-400 font-bold';
        else if (rank.currentTier === 'DIAMOND') rankStyle = 'text-purple-400 font-bold';
        else if (rank.currentTier === 'PLATINUM') rankStyle = 'text-cyan-400 font-bold';
        else if (rank.currentTier === 'GOLD') rankStyle = 'text-yellow-500 font-bold';
        else if (rank.currentTier === 'SILVER') rankStyle = 'text-gray-300 font-bold';
        else if (rank.currentTier === 'BRONZE') rankStyle = 'text-orange-700 font-bold';
        else if (rank.currentTier === 'IRON') rankStyle = 'text-gray-500 font-bold';

        let adminButtons = '-';
        if (userIsAdmin) {
            adminButtons = `
                <div class="flex items-center justify-end gap-2">
                    <button class="bg-white/5 hover:bg-indigo-500/20 hover:text-indigo-300 text-gray-400 p-2 rounded-lg transition-colors" onclick="openRankModal('${rank.username}')" title="Editar">
                        <i class="ph ph-pencil-simple text-sm"></i>
                    </button>
                </div>
            `;
        }

        const isTop3 = index < 3;
        const positionIcon = index === 0 ? '👑' : index === 1 ? '🥈' : index === 2 ? '🥉' : `${index + 1}º`;

        const tr = document.createElement('tr');
        tr.className = 'hover:bg-white/5 transition-colors group';
        tr.innerHTML = `
            <td class="pl-6 py-4">
                <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center font-bold text-gray-300 text-lg">
                        ${positionIcon}
                    </div>
                    <div>
                        <div class="font-bold text-white group-hover:text-val-red transition-colors">${rank.username}</div>
                        <div class="text-[10px] text-gray-500 uppercase tracking-widest mt-0.5">Top ${index + 1}</div>
                    </div>
                </div>
            </td>
            <td class="py-4">
                <span class="${rankStyle} uppercase tracking-widest text-sm">${rank.currentTier}</span>
            </td>
            <td class="py-4">
                <div class="font-mono text-white text-lg">${rank.rankRating} <span class="text-xs text-gray-500">RR</span></div>
            </td>
            <td class="pr-6 py-4 text-right">
                ${adminButtons}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

const rankModal = document.getElementById('rankModal');
const rankModalContent = document.getElementById('rankModalContent');

function openRankModal(username) {
    const form = document.getElementById('rankForm');
    form.reset();
    
    const rank = currentRanks.find(r => r.username === username);
    if (rank) {
        document.getElementById('rankUsername').value = rank.username;
        document.getElementById('rankTier').value = rank.currentTier;
        document.getElementById('rankRR').value = rank.rankRating;
        
        document.getElementById('rankModalTitle').textContent = `Ajustar Rango: ${username}`;

        rankModal.classList.remove('hidden');
        void rankModal.offsetWidth;
        rankModal.classList.remove('opacity-0');
        rankModalContent.classList.remove('scale-95');
        rankModalContent.classList.add('scale-100');
    }
}

function closeRankModal() {
    rankModal.classList.add('opacity-0');
    rankModalContent.classList.remove('scale-100');
    rankModalContent.classList.add('scale-95');
    setTimeout(() => {
        rankModal.classList.add('hidden');
    }, 300);
}

document.getElementById('rankForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('rankUsername').value;
    const rankData = {
        username: username,
        currentTier: document.getElementById('rankTier').value,
        rankRating: parseInt(document.getElementById('rankRR').value)
    };

    try {
        const response = await apiFetch(`/api/v1/ranks/${username}`, {
            method: 'PUT',
            body: JSON.stringify(rankData)
        });

        if (response.ok) {
            showToast('Rango actualizado', 'success');
            closeRankModal();
            loadRankings();
        } else {
            const errorData = await response.json();
            showToast('Error: ' + (errorData.message || 'Error desconocido'), 'error');
        }
    } catch (error) {
        console.error(error);
        showToast('Error de red al actualizar el rango', 'error');
    }
});

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.querySelector('span').textContent = message;
    toast.classList.remove('opacity-0', 'translate-y-4');
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}
