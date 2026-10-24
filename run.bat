@echo off
echo Launching Campus Resource Allocation Simulator GUI...
if not exist "bin\com\campus\Main.class" (
    echo Class files not found. Compiling first...
    call compile.bat
)
java -cp bin com.campus.Main
