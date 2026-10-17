# build.ps1 - компиляция проекта Help Desk (JDK 17 + pgJDBC).
# Использование: powershell -ExecutionPolicy Bypass -File build.ps1

$ErrorActionPreference = "Stop"

$jdk  = "C:\Program Files\Java\jdk-17\bin"
$jar  = "lib\postgresql-42.7.13.jar"

if (-not (Test-Path $jdk)) {
    throw "Не найден JDK 17: $jdk"
}
if (-not (Test-Path $jar)) {
    throw "Не найден JDBC-драйвер: $jar"
}

if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Force -Path out | Out-Null

$files = Get-ChildItem -Recurse -Path src -Filter *.java |
    ForEach-Object { $_.FullName }

& "$jdk\javac.exe" -encoding UTF-8 -d out -cp $jar $files

if ($LASTEXITCODE -ne 0) {
    throw "Компиляция завершилась с ошибкой"
}

Write-Host "Сборка успешна. Классы в каталоге out\"
