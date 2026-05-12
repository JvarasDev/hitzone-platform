import re
import io

pages = [
    {"name": "agents", "title": "Hitzone - Agentes", "header": "Catálogo de Agentes", "subtitle": "Explora y administra los agentes de Hitzone."},
    {"name": "maps", "title": "Hitzone - Mapas", "header": "Catálogo de Mapas", "subtitle": "Explora y administra los mapas de Hitzone."},
    {"name": "history", "title": "Hitzone - Historial", "header": "Historial de Sesiones", "subtitle": "Revisa tus sesiones de entrenamiento anteriores."},
    {"name": "global", "title": "Hitzone - Resumen Global", "header": "Resumen Global", "subtitle": "Estadísticas generales del sistema."},
    {"name": "rankings", "title": "Hitzone - Rankings", "header": "Top Rankings", "subtitle": "Los mejores jugadores de la plataforma."},
    {"name": "users", "title": "Hitzone - Usuarios", "header": "Gestión de Usuarios", "subtitle": "Administración de cuentas y permisos."},
    {"name": "settings", "title": "Hitzone - Ajustes", "header": "Ajustes del Sistema", "subtitle": "Configuración general de la plataforma."}
]

with io.open("weapons.html", "r", encoding="utf-8") as f:
    weapons_html = f.read()

for page in pages:
    html = weapons_html

    # Replace Title
    html = re.sub(r'<title>Hitzone - Armas</title>', f'<title>{page["title"]}</title>', html)
    
    # Replace Header & Subtitle
    html = re.sub(r'Arsenal de Armas', page["header"], html)
    html = re.sub(r'Catálogo de Armas', page["header"], html)
    html = re.sub(r'Explora y administra el arsenal completo de Hitzone\.', page["subtitle"], html)
    
    # Replace JS script
    html = re.sub(r'<script src="/js/weapons.js"></script>', f'<script src="/js/{page["name"]}.js"></script>', html)
    
    # Remove active state from Weapons
    html = re.sub(r'class="sidebar-item active([^"]+)"\s*>\s*<i class="ph ph-sword', r'class="sidebar-item\1">\n                <i class="ph ph-sword', html)
    
    # Add active state to the current page link
    # Find: class="sidebar-item flex ... hover:text-white( mb-1)?" (when href matches)
    pattern = rf'href="{page["name"]}\.html" class="sidebar-item (flex items-center[^"]+)"'
    # we want to insert 'active ' and change text-gray-400 to text-white, and remove hover:text-white
    # It's easier to just do a strict replace
    html = re.sub(
        rf'href="{page["name"]}\.html" class="sidebar-item flex items-center gap-3 px-3 py-2\.5 rounded-lg text-sm font-medium text-gray-400 hover:text-white( mb-1)?"',
        f'href="{page["name"]}.html" class="sidebar-item active flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-white\\1"',
        html
    )

    # Replace table body and header with placeholder
    html = re.sub(r'<thead class="bg-black/40">.*?</thead>', '<thead class="bg-black/40"><tr><th class="pl-6 pt-4 pb-4">Próximamente</th></tr></thead>', html, flags=re.DOTALL)
    html = re.sub(r'<tbody id="weaponsTableBody" class="px-6">.*?</tbody>', '<tbody id="mainTableBody" class="px-6"><tr><td class="pl-6 py-8 text-gray-400">Contenido en construcción...</td></tr></tbody>', html, flags=re.DOTALL)
    
    # Remove the Add Button completely
    html = re.sub(r'<button id="addWeaponBtn".*?</button>', '', html, flags=re.DOTALL)
    
    # Remove the Modal completely
    html = re.sub(r'<!-- Modal Arma -->.*?</div>\s*</div>\s*</div>', '<!-- Modal Placeholder -->', html, flags=re.DOTALL)

    with io.open(f"{page['name']}.html", "w", encoding="utf-8") as f:
        f.write(html)
        
    with io.open(f"js/{page['name']}.js", "w", encoding="utf-8") as f:
        f.write(f"// JS para {page['name']}\nconsole.log('{page['name']} cargado exitosamente');\n")

print("Done with Python!")
