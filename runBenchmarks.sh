#!/bin/bash

FILE=/tmp/jsonpathkt
FILE2=/tmp/jsonpathkt2
./gradlew :jsonpath-benchmarks:jvmRun > "$FILE"
if [ $? -eq 0 ]; then
  cat "$FILE" | sed 's/^ *//' | grep '^|' > "$FILE2"

  # print path times
  cat "$FILE2" | grep '^|  \$'
  echo ''
  # print compile times
  cat "$FILE2" | grep -v '^|  \$'
fi
