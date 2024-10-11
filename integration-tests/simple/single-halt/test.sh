$RUN_SPRO singlehalt.asm > assembler.output
$RUN_SPRO output.bin exec 20 16 > test.output
diff -q test.output test.etalon
rm test.output output.bin assembler.output
