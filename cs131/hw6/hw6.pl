duplist([],[]).
duplist([H|T1],[A,B|T2]) :- H=A, H=B, duplist(T1, T2). 

subset([],_).
subset([H1|T1],[H2|T2]) :- H1=H2, subset(T1,T2).
subset([H1|T1],[_|T2]) :- subset([H1|T1], T2).



/* These actions will change state by pickup */

blocksworld(State, [], State).
blocksworld(State1, [M|Ms], State3) :- move(State1, M, State2), blocksworld(State2, Ms, State3).


move( world( T1,S2  ,S3  ,H1 ), putdown(H1, stack1), world([H1|T1],S2 ,S3 ,none)) :- \+(H1 = none).
move( world( S1  ,T2,S3  ,H2 ), putdown(H2, stack2), world(S1 ,[H2|T2],S3 ,none)) :- \+(H2 = none).
move( world( S1  ,S2  ,T3,H3 ), putdown(H3, stack3), world(S1 ,S2 ,[H3|T3],none)) :- \+(H3 = none).

move(world([H1|T1],S2 ,S3 ,none), pickup(H1, stack1), world(T1   ,S2,S3,H1)) :- \+([H1|T1] = []).
move(world(S1 ,[H2|T2],S3 ,none), pickup(H2, stack2), world(S1,T2   ,S3,H2)) :- \+([H2|T2] = []).
move(world(S1 ,S2 ,[H3|T3],none), pickup(H3, stack3), world(S1,S2,T3   ,H3)) :- \+([H3|T3] = []).

    
sum_list([], 0 ,1).
sum_list([H|T], Aggr, M1):- sum_list(T, Rest, M2), M1 is M2 * 10, Aggr is H * M2 + Rest.


verbalarithmetic(Alpha, [H1|T1], [H2|T2], [H3|T3]):- 
fd_domain(Alpha,[0,1,2,3,4,5,6,7,8,9]), fd_all_different(Alpha), 
H1 #\= 0, H2 #\= 0, H3 #\= 0,
fd_labeling(Alpha),
sum_list([H1|T1], X1, _), sum_list([H2|T2], X2, _), sum_list([H3|T3], X3, _),
X is X1 + X2,
X = X3.    
