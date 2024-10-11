$RUN_SPRO arith-add.asm > assembler.output
$RUN_SPRO output.bin exec 7 150 > test.output
#$RUN_SPRO output.bin debug-novis 150

diff -q test.output test.etalon
rm test.output output.bin assembler.output
