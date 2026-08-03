@echo off
set MC_VER=%~1

if "%MC_VER%"=="" goto :USAGE

echo [INFO] Launching Minecraft %MC_VER% with ThrowAll ^& MoveAll mod...
call "%~dp0..\gradlew.bat" :versions:%MC_VER%:runClient
goto :EOF

:USAGE
echo ============================================================
echo   ThrowAll ^& MoveAll -- Quick Version Testing Tool
echo ============================================================
echo Usage: test-version.bat ^<version^>
echo.
echo Representative versions to test:
echo   1.19.4  -- Era 1 (Legacy MatrixStack ^& ButtonWidget)
echo   1.20.1  -- Era 2 (DrawContext 1-arg renderBackground)
echo   1.21.4  -- Era 3 (DrawContext 4-arg renderBackground)
echo   26.1    -- Era 4 (Mojang Mappings)
echo.
echo Examples:
echo   .\scripts\test-version.bat 1.20.1
echo   .\scripts\test-version.bat 1.21.4
echo ============================================================
exit /b 1
