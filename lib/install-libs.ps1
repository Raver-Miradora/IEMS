# install-libs.ps1
# Installs all custom JAR files from the lib/ folder into the local Maven repository.
# JAR naming convention: groupId--artifactId--version.jar

$libDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$jars = Get-ChildItem -Path $libDir -Filter "*.jar" -ErrorAction SilentlyContinue

if (-not $jars) {
    Write-Host "No JAR files found in $libDir" -ForegroundColor Yellow
    Write-Host "Place your custom JARs here using the format: groupId--artifactId--version.jar"
    Write-Host "See lib/README.md for the list of required JARs."
    exit 1
}

$installed = 0
$failed = 0

foreach ($jar in $jars) {
    $parts = $jar.BaseName -split '--'
    if ($parts.Length -ne 3) {
        Write-Host "[SKIP] $($jar.Name) — expected format: groupId--artifactId--version.jar" -ForegroundColor Yellow
        continue
    }

    $groupId = $parts[0]
    $artifactId = $parts[1]
    $version = $parts[2]

    Write-Host "[INSTALL] $groupId`:$artifactId`:$version from $($jar.Name)..." -ForegroundColor Cyan
    
    mvn install:install-file `
        "-Dfile=$($jar.FullName)" `
        "-DgroupId=$groupId" `
        "-DartifactId=$artifactId" `
        "-Dversion=$version" `
        "-Dpackaging=jar" `
        "-DgeneratePom=true" -q

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  OK" -ForegroundColor Green
        $installed++
    } else {
        Write-Host "  FAILED" -ForegroundColor Red
        $failed++
    }
}

Write-Host ""
Write-Host "Done: $installed installed, $failed failed." -ForegroundColor $(if ($failed -gt 0) { "Yellow" } else { "Green" })
