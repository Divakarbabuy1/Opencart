#!/bin/bash

echo "========================================="
echo "Starting OpenCart Test Automation Suite"
echo "========================================="

# 1. Define the Java Environment directly inside the execution thread
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# 2. Jump straight to your absolute project directory
cd /Users/the_walker/eclipse-workspace/Opencart

# 3. Run the test execution suite
mvn clean test

echo "========================================="
echo "Execution completed."
echo "========================================="