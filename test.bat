@echo off
echo Running Campus Resource Allocation Simulator OOP Verification Suite...
if not exist "bin\com\campus\VerificationTest.class" (
    echo Class files not found. Compiling first...
    call compile.bat
)
java -cp bin com.campus.VerificationTest
pause
