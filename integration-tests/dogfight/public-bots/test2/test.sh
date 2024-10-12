export P1=runner
export P2=runner

export MAX_TURNS=10
export ARENA_SIZE=100
export SEED=42

cp ../$P1.asm .
cp ../$P2.asm .

$RUN_SPRO $P1.asm > /dev/null

$RUN_SPRO $P2.asm > /dev/null

$RUN_SPRO deathmatch $MAX_TURNS $ARENA_SIZE $SEED ./$P1.bin 0 ./$P2.bin 0 > test.output

diff -q test.output test.etalon
rm -f $P1.asm $P1.bin $P2.asm $P2.bin test.output fight.replay
