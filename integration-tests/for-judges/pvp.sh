
set -euo pipefail

export SPRO_JAR=/home/svazar/Code/gitReps/AsmWarsSysPro/SPRO/out/artifacts/SPRO_jar/SPRO.jar
export RUN_SPRO="java -ea -esa -jar $SPRO_JAR"

export P1NAME=pusher
export P1PATH=/home/svazar/Code/gitReps/AsmWarsSysPro/SPRO/intergration-tests/secret-bots/bronze
export P1BONUS=0

export P2NAME=replicator
export P2PATH=/home/svazar/Code/gitReps/AsmWarsSysPro/SPRO/intergration-tests/secret-bots/gold
export P2BONUS=0

export TURNS_UNTIL_SUDDEN_DEATH=500
export ARENA_SIZE=676
export SEED=777

#####################################################################################################

rm -rf tmp
mkdir tmp
cd tmp

cp $P1PATH/$P1NAME.asm .
cp $P2PATH/$P2NAME.asm .

$RUN_SPRO $P1NAME.asm > /dev/null
mv output.bin $P1NAME.bin

$RUN_SPRO $P2NAME.asm > /dev/null
mv output.bin $P2NAME.bin

$RUN_SPRO deathmatch $TURNS_UNTIL_SUDDEN_DEATH $ARENA_SIZE $SEED ./$P1NAME.bin $P1BONUS ./$P2NAME.bin $P2BONUS > test.output

$RUN_SPRO replay ./fight.replay 1 $ARENA_SIZE

# rm -f $P1.asm $P1.bin $P2.asm $P2.bin test.output fight.replay
