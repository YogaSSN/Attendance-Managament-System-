@echo off
echo Compiling Track It !!...

REM Ensure bin directory exists
if not exist bin mkdir bin

REM Generate sources list
if exist sources.txt del /f /q sources.txt
for /r src %%f in (*.java) do @echo %%f>>sources.txt

REM Compile all sources with UTF-8 and include libs on classpath
javac -encoding UTF-8 -d bin -cp "lib/*;bin" @sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
) else (
    echo Compilation successful!
)
pause
