$RUN_SPRO incorrect-format.bin exec 20 64 > test.output
diff -q test.output test.etalon
rm test.output
