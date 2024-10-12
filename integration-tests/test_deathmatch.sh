set -euo pipefail

export BASE_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

export SPRO_JAR="$BASE_DIR/../target/build/spro.jar"
export RUN_SPRO="java -ea -esa -jar $SPRO_JAR"

echo $RUN_SPRO

echo "Running deathmatch:"
for n in test1
do
  echo "  $n"
  cd $BASE_DIR/deathmatch/$n
  source test.sh
done

