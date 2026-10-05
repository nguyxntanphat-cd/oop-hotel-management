#!/usr/bin/env bash
# Biên dịch và chạy chương trình (Linux/macOS). Chạy từ thư mục gốc repo: ./scripts/run.sh
set -e
cd "$(dirname "$0")/.."
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out hotel.Main
