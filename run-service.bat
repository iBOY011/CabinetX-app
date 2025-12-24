@echo off
setlocal enabledelayedexpansion

REM Extract module name from path or name
set "MODULE_ARG=%~1"
if "%MODULE_ARG%"=="" set "MODULE_ARG=gateway-service"

REM Remove trailing slash if present
if "%MODULE_ARG:~-1%"=="\" set "MODULE_ARG=%MODULE_ARG:~0,-1%"
if "%MODULE_ARG:~-1%"=="/" set "MODULE_ARG=%MODULE_ARG:~0,-1%"

REM Get just the module name (basename)
for %%i in ("%MODULE_ARG%") do set "MODULE_NAME=%%~ni"

set "ENV_FILE=%~2"
if "%ENV_FILE%"=="" set "ENV_FILE=.\env\%MODULE_NAME%.env"

if not exist "%ENV_FILE%" (
  echo Env file not found: %ENV_FILE%
  exit /b 1
)

REM Load environment variables from the .env file
for /f "tokens=*" %%i in (%ENV_FILE%) do (
  set "line=%%i"
  REM Skip comments and empty lines
  if not "!line:~0,1!"=="#" if not "!line!"=="" (
    REM Split on first = 
    for /f "tokens=1,* delims==" %%a in ("!line!") do (
      set "%%a=%%b"
    )
  )
)

cd "%MODULE_ARG%"
mvn spring-boot:run