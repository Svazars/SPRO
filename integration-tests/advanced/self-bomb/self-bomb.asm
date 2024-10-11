; we expect that this program is loaded at address 0
Mov       0, R5 ; <----------------------------------------|
Store  2048, 0  ; 2048 is 0x8000 which is Halt             |
JumpZero R5, 0  ; -----------------------------------------|
; Yes, it is NOT an infinite loop, it explodes
