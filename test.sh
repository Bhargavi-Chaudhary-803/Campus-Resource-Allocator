#!/bin/bash
if [ ! -f "bin/com/campus/VerificationTest.class" ]; then
    echo "Class files not found. Compiling first..."
    chmod +x compile.sh
    ./compile.sh
fi
java -cp bin com.campus.VerificationTest
