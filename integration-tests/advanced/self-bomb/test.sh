$RUN_SPRO self-bomb.asm > assembler.output
$RUN_SPRO output.bin exec 1000 1024 > test.output
diff -q test.output test.etalon
rm test.output output.bin assembler.output
