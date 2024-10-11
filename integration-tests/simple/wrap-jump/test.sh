$RUN_SPRO wrap-jump.asm > assembler.output
$RUN_SPRO output.bin exec 17 444 > test.output
diff -q test.output test.etalon
rm test.output output.bin assembler.output
