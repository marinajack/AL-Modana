dtmc

module tank

s : [0..7] init 0;
in_x : [0..1] init 0;
out_x :[0..1] init 0;
in_p : [-200000000..200000000]init 0;
out_p : [-200000000..200000000] init 0;
[] s=3 & in_x= 0-> 1 : (s'=6)  & (out_x'=1);
[] s=3 & in_x= 1-> 1 : (s'=5)  & (out_x'=0);
[] s=4 -> 1 : (s'=0) ;
[] s=6 -> 1 : (s'=0) ;
[] s=0 -> 0.5 : (s'=1)  + 0.5 : (s'=2) ;
[] s=7 -> 1 : (s'=0) ;
[] s=1 -> 0.9 : (s'=0)  + 0.1 : (s'=3) ;
[] s=2 -> 0.2 : (s'=4)  & (out_p'=in_p-1000) + 0.8 : (s'=7)  & (out_p'=in_p);
[] s=5 -> 1 : (s'=0) ;

endmodule
