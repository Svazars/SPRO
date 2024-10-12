$RUN_SPRO pic-loop.asm > assembler.output
$RUN_SPRO pic-loop.bin exec 1000 1024 > test.output
diff -q test.output test.etalon
rm test.output pic-loop.bin assembler.output
