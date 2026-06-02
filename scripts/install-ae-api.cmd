@echo off
setlocal

set AE_VERSION=9.9.14
set ROOT=%~dp0..
for %%I in ("%ROOT%") do set ROOT=%%~fI
set AE_JAR=%ROOT%\libs\ae-api-%AE_VERSION%.jar
set LOCAL_AE_JAR=%USERPROFILE%\.m2\repository\net\advancedplugins\ae-api\%AE_VERSION%\ae-api-%AE_VERSION%.jar

pushd "%ROOT%"

if not exist "%AE_JAR%" (
  if exist "%LOCAL_AE_JAR%" (
    echo Using locally installed net.advancedplugins:ae-api:%AE_VERSION%.
    popd
    exit /b 0
  ) else (
    echo Missing "%AE_JAR%".
    echo Download the official AdvancedEnchantments API jar and save it there first.
    popd
    exit /b 1
  )
)

call mvnw.cmd org.apache.maven.plugins:maven-install-plugin:3.1.3:install-file ^
  -Dfile="%AE_JAR%" ^
  -DgroupId=net.advancedplugins ^
  -DartifactId=ae-api ^
  -Dversion=%AE_VERSION% ^
  -Dpackaging=jar

set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
