; ---- init -----
Add IP, 16, R1   ; R1 := IP + 16 == LOOP_START
Add IP, 26, R2   ; R2 := IP + 30 == AFTER_LOOP
Mov 0 , R4       ; R4 := 0
Mov 7, R5       ; R5 := 15
; ---- loop ----
Mov R5, R3       ; R3 := R5   <----------------LOOP_START-----------------|
JumpZero R5, R2  ; if (R5 == 0) goto AFTER_LOOP                           |
Mul R3, R3, R3   ; R3 := R3 * R3                                          |
Add R3, R4, R4   ; R4 += R3                                               |
Sub  R5, 1, R5   ; R5--                                                   |
Jump R1          ; goto R1 --- goto LOOP_START ---------------------------|
; ---- after loop ----
; R4 = 7*7 + 6*6 + ... + 1*1 = 140
Halt
