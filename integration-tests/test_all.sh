set -euo pipefail

export BASE_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

export SPRO_JAR="$BASE_DIR/../target/build/spro.jar"
export RUN_SPRO="java -ea -esa -jar $SPRO_JAR"

echo $RUN_SPRO

echo "Running simple tests:"
for n in incorrect-format single-halt jumper wrap-pop wrap-jump wrap-nop arith-add
do
  echo "  $n"
  cd $BASE_DIR/simple/$n
  source test.sh
done

echo "Running advanced tests:"
for n in PIC-loop self-bomb
do
  echo "  $n"
  cd $BASE_DIR/advanced/$n
  source test.sh
done
