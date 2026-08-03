@echo off
if "%1"=="" (
    echo Usage: .\scripts\publish-version.bat ^<mcVersion^>
    echo Examples:
    echo   .\scripts\publish-version.bat 1.20.4
    echo   .\scripts\publish-version.bat 1.21.4
    echo   .\scripts\publish-version.bat 1.19.4
    exit /b 1
)

echo ==================================================
echo   Publishing Minecraft %1 to Modrinth...
echo ==================================================
.\gradlew.bat :versions:%1:modrinth
