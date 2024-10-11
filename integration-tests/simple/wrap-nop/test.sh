$RUN_SPRO wrap-nop.asm > assembler.output
$RUN_SPRO output.bin exec 25 1024 > test.output
diff -q test.output test.etalon
rm test.output output.bin assembler.output
