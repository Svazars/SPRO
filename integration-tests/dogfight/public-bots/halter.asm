
mov 1, r5      ; for faster increment
mov 2048, r4   ; 2048 == HALT opcode
add ip, 50, r2 ; r2 == pointer where halt must be stored
mov ip, r3     ; r3 == LOOP
; LOOP
store r4, r2   ;   <-----|
add r5, r2, r2 ;         |
jump r3        ; --------|
