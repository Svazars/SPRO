
nop            ; to be replaced with halt
mov 4, r2      ; infinite loop delta
sub ip, 2, r1  ; r1 == address of nop
store 2048, r1 ; replace first nop with halt
nop            ; <--------------|
sub ip, r2, ip  ; infinite loop  |
halt
