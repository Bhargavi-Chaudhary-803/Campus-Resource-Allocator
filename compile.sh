#!/bin/bash
echo "==================================================="
echo "Compiling Campus Resource Allocation Simulator..."
echo "==================================================="
mkdir -p bin
javac -encoding UTF-8 -d bin -sourcepath src src/com/campus/Main.java src/com/campus/VerificationTest.java
if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation SUCCESSFUL!"
    echo "Run './run.sh' to launch the desktop application."
    echo "Run './test.sh' to execute the automated OOP test suite."
else
    echo ""
    echo "Compilation FAILED! Ensure Java 17+ is installed."
fi
