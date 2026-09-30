@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
(for /r src %%f in (*.java) do @echo "%%f") > out\fontes.txt
(for /r test %%f in (*.java) do @echo "%%f") >> out\fontes.txt
javac -encoding UTF-8 -Xlint:all -d out @out\fontes.txt
if errorlevel 1 exit /b 1
if /i "%~1"=="teste" (
    java -cp out petcare.PetCareTest
) else (
    java -cp out petcare.Main
)
