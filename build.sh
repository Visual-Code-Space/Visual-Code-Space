#!/bin/bash

set -eu

dir=$(dirname $0)

bash $dir/gradlew -Pandroid.aapt2FromMavenOverride=$HOME/.androidide/aapt2 "$@"