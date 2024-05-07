:: This batch file compiles the Java files in the src/main directory and creates the client and server jar executables.
:: It does not update the jar files.
:: Rather, it creates new jar files each time it is ran.
@echo off

:: Remove the current content of the out directory.
echo Removing current out folder...
rmdir /S /Q out
echo Folder removed.

:: Create the out directory while compiling the Java files.
echo Compiling Java files...
javac -cp ".;lib/*" -d out/ src/main/*.java -Xlint
echo Compilation complete.

:: Extract lib files to the out directory.
echo Extracting lib files...
cd out
for /r ..\lib %%i in (*.jar) do (
    jar xvf %%i
)
echo Files extracted.
cd ..

:: Remove MANIFEST.MF files from the out directory.
echo Removing MANIFEST.MF files...
del /S /Q out\META-INF\MANIFEST.MF
echo Files removed.

:: Create the client executable jar file.
echo Creating client.jar...
jar cvfe tmp/client.jar main.Client -C out/ .
echo client.jar created.

:: Create the server executable jar file.
echo Creating server.jar...
jar cvfe tmp/server.jar main.Server -C out/ .
echo server.jar created.

:: Recreate the out directory.
echo Removing out folder...
rmdir /S /Q out
echo Folder removed.
echo Creating out folder...
mkdir out
echo Folder created.

:: Move the jar files to the out directory.
echo Moving jar files...
move tmp\* out
echo Jar files moved.

:: Remove the tmp directory.
echo Removing tmp folder...
rmdir /Q tmp
echo Folder removed.

echo Done.