@echo off
echo ===================================================
echo Compiling Campus Resource Allocation Simulator...
echo ===================================================
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src/com/campus/Main.java src/com/campus/VerificationTest.java
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation SUCCESSFUL!
    echo Run 'run.bat' to launch the desktop application.
    echo Run 'test.bat' to execute the automated OOP test suite.
) else (
    echo.
    echo Compilation FAILED! Ensure Java 17+ is installed and on your PATH.
)
pause
