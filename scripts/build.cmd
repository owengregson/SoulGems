@echo off
setlocal

set ROOT=%~dp0..
for %%I in ("%ROOT%") do set ROOT=%%~fI

pushd "%ROOT%"

call scripts\install-ae-api.cmd
if errorlevel 1 (
  popd
  exit /b 1
)

call mvnw.cmd clean package
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
