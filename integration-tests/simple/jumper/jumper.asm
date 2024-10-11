mov 1, r2 ; r2 == 1
jump 18 ; goto L1
add r2, r5, r5
add r2, r5, r5
add r2, r5, r5
; L1
add r5, 1, r5 ; r5 == 1
add ip, 12, r4
jump r4
add r2, r5, r5
add r2, r5, r5
add r2, r5, r5
; L2
add 1, r5, r5 ; r5 == 2
Halt

