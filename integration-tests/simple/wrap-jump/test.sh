$RUN_SPRO wrap-jump.asm > assembler.output
$RUN_SPRO wrap-jump.bin exec 17 444 > test.output
diff -q test.output test.etalon
rm test.output wrap-jump.bin assembler.output
