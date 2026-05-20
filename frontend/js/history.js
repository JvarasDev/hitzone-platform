// js/history.js
let username = '';

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
    username = payload.sub || 'Usuario';
    document.getElementById('userNameDisplay').textContent = username;
    document.getElementById('userAvatar').textContent = username.substring(0, 2).toUpperCase();

    // Check Roles
    const roles = payload.roles || payload.authorities || [];
    const isAdmin = Array.isArray(roles) ? roles.includes('ROLE_ADMIN') : roles === 'ROLE_ADMIN' || (typeof roles === 'string' && roles.includes('ROLE_ADMIN'));
    
    if (isAdmin || username.toLowerCase() === 'admin') {
        document.getElementById('userRoleBadge').textContent = 'ADMIN';
        document.getElementById('userRoleBadge').className = 'text-[10px] font-bold text-val-red uppercase tracking-widest bg-val-red/10 px-2 py-0.5 rounded inline-block mt-0.5 border border-val-red/20';
        document.getElementById('adminMenu').classList.remove('hidden');
    }

    loadHistory();
    loadKda();
});

async function loadHistory() {
    try {
        const response = await apiFetch(`/api/matches/player/${username}/history`);
        if (response.ok) {
            const matches = await response.json();
            renderHistory(matches);
        } else {
            console.error('Failed to fetch history');
        }
    } catch (error) {
        console.error('Error fetching history:', error);
    }
}

async function loadKda() {
    try {
        const response = await apiFetch(`/api/matches/player/${username}/kda`);
        if (response.ok) {
            const data = await response.json();
            document.getElementById('totalKills').textContent = data.kills || 0;
            document.getElementById('totalDeaths').textContent = data.deaths || 0;
            document.getElementById('totalAssists').textContent = data.assists || 0;
        } else {
            console.error('Failed to fetch KDA');
        }
    } catch (error) {
        console.error('Error fetching KDA:', error);
    }
}

function renderHistory(matches) {
    const tbody = document.getElementById('historyTableBody');
    tbody.innerHTML = '';

    if (!matches || matches.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="pl-6 py-8 text-center text-gray-500">No hay partidas registradas en tu historial.</td></tr>';
        return;
    }

    matches.forEach(match => {
        let resultStyle = 'bg-gray-500/20 text-gray-400';
        let resultText = match.result;
        
        if (match.result === 'WIN') {
            resultStyle = 'bg-emerald-500/20 text-emerald-400';
            resultText = 'VICTORIA';
        } else if (match.result === 'LOSS') {
            resultStyle = 'bg-red-500/20 text-red-400';
            resultText = 'DERROTA';
        } else if (match.result === 'DRAW') {
            resultStyle = 'bg-yellow-500/20 text-yellow-400';
            resultText = 'EMPATE';
        }

        // Buscamos las estadisticas del usuario actual en esta partida
        const playerStats = match.players ? match.players.find(p => p.username === username) : null;
        
        const kdaDisplay = playerStats 
            ? `<span class="text-white font-bold">${playerStats.kills}</span> / <span class="text-gray-400">${playerStats.deaths}</span> / <span class="text-gray-400">${playerStats.assists}</span>`
            : '-';
            
        const agentDisplay = playerStats && playerStats.agentName
            ? playerStats.agentName
            : 'Desconocido';

        const tr = document.createElement('tr');
        tr.className = 'hover:bg-white/5 transition-colors group cursor-pointer';
        tr.innerHTML = `
            <td class="pl-6 py-4">
                <div class="font-mono text-xs text-gray-500 mb-1">#${match.matchId}</div>
                <div class="font-bold text-white group-hover:text-val-red transition-colors">${match.mapName || 'Desconocido'}</div>
            </td>
            <td class="py-4">
                <span class="${resultStyle} px-2 py-1 rounded text-[10px] uppercase font-bold tracking-widest">${resultText}</span>
                <div class="text-xs text-gray-500 mt-1">${match.scoreTeam || 0} - ${match.scoreEnemy || 0}</div>
            </td>
            <td class="py-4">
                <div class="text-sm font-semibold text-gray-300">${match.gameMode || 'COMPETITIVE'}</div>
                <div class="text-xs text-gray-500 mt-1">${formatDuration(match.durationS)}</div>
            </td>
            <td class="py-4">
                ${kdaDisplay}
            </td>
            <td class="pr-6 py-4 text-right">
                <div class="flex items-center justify-end gap-3">
                    <span class="font-semibold text-white">${agentDisplay}</span>
                    <div class="w-8 h-8 rounded bg-white/5 flex items-center justify-center font-bold text-sm text-gray-400 border border-white/10">
                        ${agentDisplay.substring(0, 1)}
                    </div>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function formatDuration(seconds) {
    if (!seconds) return '--:--';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}m ${secs}s`;
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.querySelector('span').textContent = message;
    
    toast.classList.remove('opacity-0', 'translate-y-4');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4');
    }, 3000);
}
