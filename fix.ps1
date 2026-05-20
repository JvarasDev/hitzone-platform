$services = @(
    @{name="ms-maps"; pkg="ms_maps"},
    @{name="ms-persistence"; pkg="ms_persistence"},
    @{name="ms-rank"; pkg="ms_rank"},
    @{name="ms-weapons"; pkg="ms_weapons"},
    @{name="ms-agents"; pkg="ms_agents"},
    @{name="ms-news"; pkg="ms_news"},
    @{name="fullstack"; pkg="fullstack"}
)

$src = "c:\Users\juanm\Documents\fullstack_project\ms-matches\src\main\java\cl\hitzone\ms_matches\exception"

foreach ($srv in $services) {
    $dest = "c:\Users\juanm\Documents\fullstack_project\$($srv.name)\src\main\java\cl\hitzone\$($srv.pkg)\exception"
    if (!(Test-Path $dest)) {
        New-Item -ItemType Directory -Force -Path $dest | Out-Null
    }
    Copy-Item -Path "$src\*.java" -Destination $dest -Force
    
    Get-ChildItem -Path $dest -Filter "*.java" | ForEach-Object {
        (Get-Content $_.FullName) -replace "package cl\.hitzone\.ms_matches\.exception;", "package cl.hitzone.$($srv.pkg).exception;" | Set-Content -Path $_.FullName
    }
}

Get-ChildItem -Path "c:\Users\juanm\Documents\fullstack_project" -Filter "application.yml" -Recurse | ForEach-Object {
    (Get-Content $_.FullName) -replace "password:\s*HitzonePassword123!", "password: `$`{DB_PASSWORD:HitzonePassword123!`}" | Set-Content -Path $_.FullName
}
Get-ChildItem -Path "c:\Users\juanm\Documents\fullstack_project\config-server" -Filter "*.yml" -Recurse | ForEach-Object {
    (Get-Content $_.FullName) -replace "password:\s*HitzonePassword123!", "password: `$`{DB_PASSWORD:HitzonePassword123!`}" | Set-Content -Path $_.FullName
}
