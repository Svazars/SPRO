export P1=spore
export P2=halter

export MAX_TURNS=50
export ARENA_SIZE=256
export SEED=42

cp ../$P1.asm .
cp ../$P2.asm .

$RUN_SPRO $P1.asm > /dev/null
mv output.bin $P1.bin

$RUN_SPRO $P2.asm > /dev/null
mv output.bin $P2.bin

$RUN_SPRO deathmatch $MAX_TURNS $ARENA_SIZE $SEED ./$P1.bin 0 ./$P2.bin 0 > test.output
# $RUN_SPRO replay ./fight.replay 1 $ARENA_SIZE


diff -q test.output test.etalon
rm -f $P1.asm $P1.bin $P2.asm $P2.bin test.output fight.replay
