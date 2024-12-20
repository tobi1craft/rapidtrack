$processes = Get-Process -Name OneDrive -ErrorAction SilentlyContinue
$path = $null

if ($processes) {
    $path = $processes[0].Path

    Write-Host "Shutting down OneDrive..."
    & $path "/shutdown" "/background"

    foreach ($p in $processes) {
        Write-Host "Waiting for OneDrive process ($( $p.Id )) to exit..."
        Wait-Process -Id $p.Id -Timeout 30 -ErrorAction SilentlyContinue
    }
}

Write-Host "Wait for IntelliJ IDEA to exit..."
while (Get-Process -Name "idea64" -ErrorAction SilentlyContinue) {
    Start-Sleep -Seconds 1
}

Write-Host "Cleaning gradle project..."
& .\gradlew clean

Write-Host "Restarting OneDrive..."
Start-Process "C:\Program Files\Microsoft OneDrive\OneDrive.exe" -ArgumentList "/background" -NoNewWindow