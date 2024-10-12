
export P1=spore
export P2=halter
export P3=runner
export P4=suicide

export MAX_TURNS=50
export ARENA_SIZE=256
export SEED=777

cp ../../dogfight/public-bots/$P1.asm .
cp ../../dogfight/public-bots/$P2.asm .
cp ../../dogfight/public-bots/$P3.asm .
cp ../../dogfight/public-bots/$P4.asm .

$RUN_SPRO $P1.asm > /dev/null

$RUN_SPRO $P2.asm > /dev/null

$RUN_SPRO $P3.asm > /dev/null

$RUN_SPRO $P4.asm > /dev/null

$RUN_SPRO deathmatch $MAX_TURNS $ARENA_SIZE $SEED ./$P1.bin 0 ./$P2.bin 0 ./$P3.bin 0 ./$P4.bin 0  > test.output

#$RUN_SPRO replay ./fight.replay 1 $ARENA_SIZE

diff -q test.output test.etalon
rm -f $P1.asm $P1.bin $P2.asm $P2.bin $P3.asm $P3.bin $P4.asm $P4.bin test.output fight.replay
