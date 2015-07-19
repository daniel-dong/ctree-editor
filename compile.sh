#!/bin/sh
javac -classpath "src/chtree/:src/chtree/database/:src/chtree/gui/:lib/piccolo2d-core-1.3.1.jar" src/chtree/*.java src/chtree/*/*.java -d bin/
