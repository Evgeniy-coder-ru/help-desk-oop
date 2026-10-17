# run.ps1 - запуск обязательного сценария Help Desk.
# Пароль БД берётся из переменной окружения DB_PASSWORD.
# Использование: powershell -ExecutionPolicy Bypass -File run.ps1

$ErrorActionPreference = "Stop"

# Корректный вывод кириллицы в консоли Windows.
& chcp 65001 | Out-Null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$jdk = "C:\Program Files\Java\jdk-17\bin"
$jar = "lib\postgresql-42.7.13.jar"

if (-not (Test-Path "out")) {
    Write-Host "Каталог out отсутствует, выполняю сборку..."
    & "$PSScriptRoot\build.ps1"
}

if (-not $env:DB_PASSWORD) {
    $env:DB_PASSWORD = "helpdesk_pwd_2026"
    Write-Host "DB_PASSWORD не задан, использую значение по умолчанию для локальной разработки."
}

& "$jdk\java.exe" -cp "out;$jar" Main
