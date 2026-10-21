param(
    [Parameter(Mandatory = $true)]
    [string]$ClientHome,
    [string]$JarName = 'hso-client.jar'
)

$ErrorActionPreference = 'Stop'
$java = Join-Path $ClientHome 'runtime\jdk8u504-b01-jre\bin\java.exe'
$stdoutLog = Join-Path $ClientHome 'client-console.log'
$stderrLog = Join-Path $ClientHome 'client-startup.log'

# An already-running KEmulator keeps the redirected log handles open. In that
# case truncating the shared files throws and used to prevent a second launch.
# Fall back to per-launch logs so START-HSO.bat remains safe to run repeatedly.
try {
    Set-Content -LiteralPath $stdoutLog -Value '' -Encoding UTF8
    Set-Content -LiteralPath $stderrLog -Value '' -Encoding UTF8
} catch [System.IO.IOException] {
    $launchStamp = Get-Date -Format 'yyyyMMdd-HHmmss-fff'
    $stdoutLog = Join-Path $ClientHome "client-console-$launchStamp.log"
    $stderrLog = Join-Path $ClientHome "client-startup-$launchStamp.log"
    Set-Content -LiteralPath $stdoutLog -Value '' -Encoding UTF8
    Set-Content -LiteralPath $stderrLog -Value '' -Encoding UTF8
}

$javaCommand = '"' + $java + '" -cp "launcher;KEmulator\KEmulator.jar"' +
    ' -Djava.library.path=KEmulator -Xmx512M -Dfile.encoding=UTF-8' +
    ' HsoLauncher -updated -screen 360x640 -jar "' + $JarName + '"' +
    ' 1>"' + $stdoutLog + '" 2>"' + $stderrLog + '"'

$startInfo = New-Object Diagnostics.ProcessStartInfo
$startInfo.FileName = $env:ComSpec
$startInfo.Arguments = '/d /s /c "' + $javaCommand + '"'
$startInfo.WorkingDirectory = $ClientHome
# KEmulator is the interactive game window. Hiding the launcher process also
# hides the emulator on some Java 8/Windows combinations, making a successful
# launch look like the game never started.
$startInfo.WindowStyle = [Diagnostics.ProcessWindowStyle]::Normal
$startInfo.UseShellExecute = $true
$process = [Diagnostics.Process]::Start($startInfo)

Start-Sleep -Seconds 5
if ($process.HasExited -and $process.ExitCode -ne 0) {
    exit $process.ExitCode
}

$fatalPatterns = 'java.lang.Error:', 'Could not find or load main class', 'Exception in thread'
$errorText = (Get-Content -LiteralPath $stderrLog -Raw -ErrorAction SilentlyContinue) +
    (Get-Content -LiteralPath $stdoutLog -Raw -ErrorAction SilentlyContinue)
foreach ($pattern in $fatalPatterns) {
    if ($errorText -like "*$pattern*") {
        exit 1
    }
}

exit 0
