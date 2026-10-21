$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$jarPath = Join-Path $projectRoot 'target\HSO_Re_2-1.0-jar-with-dependencies.jar'
$sourceRoot = Join-Path $projectRoot 'src\main\java'
$resourceRoot = Join-Path $projectRoot 'src\main\resources'

if (-not (Test-Path -LiteralPath $jarPath)) {
    throw "Missing dependency JAR: $jarPath"
}
if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'JDK javac was not found in PATH.'
}
if (-not (Get-Command jar -ErrorAction SilentlyContinue)) {
    throw 'JDK jar was not found in PATH.'
}

$serverPort = 19129
$configPath = Join-Path $projectRoot 'hso.conf'
$portLine = Get-Content -LiteralPath $configPath | Where-Object { $_ -match '^port\s*:' } | Select-Object -First 1
if ($portLine -match '^port\s*:\s*(\d+)') {
    $serverPort = [int]$Matches[1]
}
$portProbe = New-Object Net.Sockets.TcpClient
$serverRunning = $false
try {
    $portProbe.Connect('127.0.0.1', $serverPort)
    $serverRunning = $true
} catch [System.Net.Sockets.SocketException] {
    # Expected when the server is stopped.
} finally {
    $portProbe.Dispose()
}
if ($serverRunning) {
    Write-Host "[LOI] Server dang dung cong $serverPort. Hay dung server truoc khi build." -ForegroundColor Red
    exit 1
}

$buildRoot = Join-Path $env:TEMP 'hso-local-build'
$classOutput = Join-Path $buildRoot 'classes'
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
Get-ChildItem -LiteralPath $classOutput -Force -ErrorAction SilentlyContinue |
    Remove-Item -Recurse -Force

$sources = Get-ChildItem -LiteralPath $sourceRoot -Recurse -File -Filter '*.java' |
    Select-Object -ExpandProperty FullName

Write-Host "Compiling $($sources.Count) Java files..."
& javac -encoding UTF-8 --release 17 -cp $jarPath -d $classOutput $sources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

$backupPath = "$jarPath.before-local-build.bak"
Copy-Item -LiteralPath $jarPath -Destination $backupPath -Force
& jar uf $jarPath -C $classOutput .
if ($LASTEXITCODE -ne 0) {
    throw "jar update failed with exit code $LASTEXITCODE"
}
if (Test-Path -LiteralPath $resourceRoot) {
    & jar uf $jarPath -C $resourceRoot .
    if ($LASTEXITCODE -ne 0) {
        throw "resource update failed with exit code $LASTEXITCODE"
    }
}

Write-Host "Build complete: $jarPath"
Write-Host "Backup: $backupPath"
