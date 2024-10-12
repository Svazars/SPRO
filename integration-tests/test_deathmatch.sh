set -euo pipefail

export SPRO_JAR=`pwd`/../out/artifacts/SPRO_jar/SPRO.jar
export RUN_SPRO="java -ea -esa -jar $SPRO_JAR"

echo $RUN_SPRO

export BASE_DIR=`pwd`

echo "Running deathmatch:"
for n in test1 test2
do
  echo "  $n"
  cd $BASE_DIR/deathmatch/$n
  source test.sh
done

