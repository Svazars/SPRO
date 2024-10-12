set -euo pipefail

export SPRO_JAR=`pwd`/../out/artifacts/SPRO_jar/SPRO.jar
export RUN_SPRO="java -ea -esa -jar $SPRO_JAR"

echo $RUN_SPRO

export BASE_DIR=`pwd`


echo "Running tests against secret bots:"
for n in test1 test2 test3 test4
do
  echo "  $n"
  cd $BASE_DIR/secret-bots/dogfight/$n
  source test.sh
done


echo "Running tests against simple bots:"
for n in test1 test2 test3
do
  echo "  $n"
  cd $BASE_DIR/dogfight/public-bots/$n
  source test.sh
done


