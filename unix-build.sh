#!/bin/bash

# This script compiles the Java files in the src/main directory and creates the client and server jar executables.
# It does not update the jar files.
# Rather, it creates new jar files each time it is ran.

# Remove the current content of the out directory.
echo "Removing content of the out directory..."
rm -rf out
echo "Content removed."

# Create the out directory while compiling the Java files.
echo "Compiling Java files..."
javac -cp ".:lib/*" -d out/ src/main/*.java -Xlint
echo "Compilation complete."

# Extract lib files to the out directory.
echo "Extracting lib files..."
cd out
for i in ../lib/*.jar; do
    jar xvf "$i"
done
echo "Files extracted."
cd ..

# Remove MANIFEST.MF files from the out directory.
echo "Removing MANIFEST.MF files..."
rm -f out/META-INF/MANIFEST.MF
echo "Files removed."

# Create the client executable jar file.
echo "Creating client.jar..."
jar cvfe tmp/client.jar main.Client -C out/ .
echo "client.jar created."

# Create the server executable jar file.
echo "Creating server.jar..."
jar cvfe tmp/server.jar main.Server -C out/ .
echo "server.jar created."

# Recreate the out directory.
echo "Removing out folder..."
rm -rf out
echo "Folder removed."
echo "Creating out folder..."
mkdir out
echo "Folder created."

# Move the jar files to the out directory.
echo "Moving jar files..."
mv tmp/* out
echo "Jar files moved."

# Remove the tmp directory.
echo "Removing tmp folder..."
rm -rf tmp
echo "Folder removed."

echo "Done."