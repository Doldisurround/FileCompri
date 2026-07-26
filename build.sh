#!/bin/bash

set -e

JAR_NAME="Programm.jar"
MAIN_CLASS="Main"

rm -rf build
mkdir -p build

# Kompilieren
./compile.sh

# Klassen kopieren
cp -r comp/* build/

# Libraries entpacken und hinzufügen
for lib in lib/*.jar; do
    if [ -f "$lib" ]; then
        unzip -q "$lib" -d build
    fi
done

# Signatur-Dateien entfernen (falls vorhanden)
rm -rf build/META-INF/*.SF build/META-INF/*.RSA build/META-INF/*.DSA

# Manifest erstellen
mkdir -p build/META-INF

cat > build/META-INF/MANIFEST.MF <<EOF
Manifest-Version: 1.0
Main-Class: $MAIN_CLASS
EOF

# JAR bauen
jar cfm "$JAR_NAME" build/META-INF/MANIFEST.MF -C build .

echo "Fertig: $JAR_NAME"
