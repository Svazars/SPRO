$RUN_SPRO arith-add.asm > assembler.output
$RUN_SPRO arith-add.bin exec 7 150 > test.output
#$RUN_SPRO arith-add.bin debug-novis 150

diff -q test.output test.etalon
rm test.output arith-add.bin assembler.output
