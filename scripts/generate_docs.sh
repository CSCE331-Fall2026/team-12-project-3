#!/bin/bash

echo "Cleaning docs folder..."
rm -rf docs
echo "Generating documentation..."
mvn clean package > /dev/null 2>&1
cp -r target/reports/apidocs docs
echo "Documentation generated successfully."