#!/usr/bin/env bash

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
TEST_DIR="$SCRIPT_DIR/integration-tests"

cd "$TEST_DIR" && ./test_all.sh
cd "$TEST_DIR" && ./test_deathmatch.sh
cd "$TEST_DIR" && ./test_dogfight.sh
