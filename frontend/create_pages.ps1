$pages = @(
    @{ name="agents"; title="Hitzone - Agentes"; header="Catálogo de Agentes"; subtitle="Explora y administra los agentes de Hitzone." },
    @{ name="maps"; title="Hitzone - Mapas"; header="Catálogo de Mapas"; subtitle="Explora y administra los mapas de Hitzone." },
    @{ name="history"; title="Hitzone - Historial"; header="Historial de Sesiones"; subtitle="Revisa tus sesiones de entrenamiento anteriores." },
    @{ name="global"; title="Hitzone - Resumen Global"; header="Resumen Global"; subtitle="Estadísticas generales del sistema." },
    @{ name="rankings"; title="Hitzone - Rankings"; header="Top Rankings"; subtitle="Los mejores jugadores de la plataforma." },
    @{ name="users"; title="Hitzone - Usuarios"; header="Gestión de Usuarios"; subtitle="Administración de cuentas y permisos." },
    @{ name="settings"; title="Hitzone - Ajustes"; header="Ajustes del Sistema"; subtitle="Configuración general de la plataforma." }
)

$weaponsHtml = Get-Content "weapons.html" -Raw

foreach ($page in $pages) {
    $html = $weaponsHtml

    # Fix title
    $html = $html -replace "<title>Hitzone - Armas</title>", "<title>$($page.title)</title>"

    # Fix header
    $html = $html -replace "Arsenal de Armas", $($page.header)
    $html = $html -replace "Catálogo de Armas", $($page.header)
    $html = $html -replace "Explora y administra el arsenal completo de Hitzone.", $($page.subtitle)

    # Fix JS ref
    $html = $html -replace '<script src="/js/weapons.js"></script>', "<script src=`"/js/$($page.name).js`"></script>"

    # Fix active sidebar (remove active from weapons, add to this page)
    # This regex is a bit tricky, let's just do simple replacements
    $html = $html -replace 'class="sidebar-item active flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-white mb-1"(\s*)>\s*<i class="ph ph-sword text-lg"></i> Armas', 'class="sidebar-item flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400 hover:text-white mb-1"$1>
                <i class="ph ph-sword text-lg"></i> Armas'
                
    # Now set active to the current page link by finding the a tag with href="$page.name.html"
    $html = $html -replace "href=`"$($page.name)\.html`" class=`"sidebar-item flex items-center gap-3 px-3 py-2\.5 rounded-lg text-sm font-medium text-gray-400 hover:text-white( mb-1)?`"", "href=`"$($page.name).html`" class=`"sidebar-item active flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-white$1`""

    # Clear table headers and body for non-weapons
    $html = $html -replace '(?s)<thead class="bg-black/40">.*?</thead>', "<thead class=`"bg-black/40`"><tr><th class=`"pl-6 pt-4 pb-4`">Próximamente</th></tr></thead>"
    $html = $html -replace '(?s)<tbody id="weaponsTableBody" class="px-6">.*?</tbody>', "<tbody id=`"mainTableBody`" class=`"px-6`"><tr><td class=`"pl-6 py-8 text-gray-400`">Contenido en construcción...</td></tr></tbody>"
    
    # Remove Add Weapon button
    $html = $html -replace '(?s)<button id="addWeaponBtn".*?</button>', ""
    
    # Remove Modal
    $html = $html -replace '(?s)<!-- Modal Arma -->.*?</div>\s*</div>', "<!-- Modal Placeholder -->"

    $html | Set-Content "$($page.name).html" -Encoding UTF8

    # Create empty JS file
    "// JS para $($page.name)`nconsole.log('$($page.name) loaded');" | Set-Content "js\$($page.name).js" -Encoding UTF8
}

Write-Host "Done!"
