$RUN_SPRO jumper.asm > assembler.output
$RUN_SPRO jumper.bin exec 100 1024 > test.output
diff -q test.output test.etalon
rm test.output jumper.bin assembler.output
