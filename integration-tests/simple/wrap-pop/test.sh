$RUN_SPRO wrap-pop.asm > assembler.output
$RUN_SPRO wrap-pop.bin exec 24 150 > test.output
#$RUN_SPRO wrap-pop.bin debug-novis 150

diff -q test.output test.etalon
rm test.output wrap-pop.bin assembler.output
