#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
find src test -name '*.java' > out/fontes.txt
if command -v javac >/dev/null 2>&1; then
    javac -encoding UTF-8 -Xlint:all -d out @out/fontes.txt
else
    java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -Xlint:all -d out @out/fontes.txt
fi
if [ "${1:-}" = "teste" ]; then
    java -cp out petcare.PetCareTest
else
    java -cp out petcare.Main
fi
