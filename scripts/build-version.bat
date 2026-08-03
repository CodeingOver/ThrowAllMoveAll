@echo off
if "%1"=="" (
    echo Usage: .\scripts\build-version.bat ^<mcVersion^>
    echo Examples:
    echo   .\scripts\build-version.bat 1.20.4
    echo   .\scripts\build-version.bat 1.21.4
    echo   .\scripts\build-version.bat 1.19.4
    exit /b 1
)

echo ==================================================
echo   Building Minecraft %1 JAR...
echo ==================================================
call "%~dp0..\gradlew.bat" :versions:%~1:build :collectJars
