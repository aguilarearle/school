

(* Problem 1 *)


            
let rec (member : 'a -> 'a list -> bool) =
  function x ->
           function s -> 
                    match s with
                      [] -> false
                    | h::t -> 
                        if h = x then true else (member x t) 

let _ = assert((member 1 []) = false)
let _ = assert((member 0 [0;1;2;3;4;5;6]) = true)
let _ = assert((member 0 [6;5;4;3;2;1;0]) = true)
let _ = assert((member (1,2) [(1,2);(3,4);(5,6);(7,8)]) = true)              
let _ = assert((member (1,6) [(1,2);(3,4);(5,6);(7,8)]) = false)
let _ = assert((member [1;2;3] [[1;2;3];[4;5;6];[8;9;10];[11;12;13]]) = true)
let _ = assert((member [21;42;37] [[1;2;3];[4;5;6];[8;9;10];[11;12;13]]) = false)
              
let (add : 'a -> 'a list -> 'a list) =
  function x ->
           function s ->
                    match s with
                      [] -> x::[]
                    | h::t -> if member x s then s else x::s

let _ = assert((add 1 []) = [1])
let _ = assert((add 1 [1]) = [1])
let _ = assert((add 1 [0;2;3;4;5]) = [1;0;2;3;4;5])
let _ = assert((add 1 [1;2;3;4;5]) = [1;2;3;4;5])
let _ = assert((add (1,2) [(1,2);(3,4);(5,6);(7,8)]) = [(1,2);(3,4);(5,6);(7,8)] )
let _ = assert((add (1,2) [(3,4);(5,6);(7,8)]) = [(1,2);(3,4);(5,6);(7,8)] )              
let _ = assert((add [1;2;3] [[1;2;3];[4;5;6];[8;9;10];[11;12;13]]) = [[1;2;3];[4;5;6];[8;9;10];[11;12;13]] )              
let _ = assert((add [1;2;3] [[4;5;6];[8;9;10];[11;12;13]]) = [[1;2;3];[4;5;6];[8;9;10];[11;12;13]] )                            
                                                                           
let rec (union : 'a list -> 'a list -> 'a list) =
  function s1 ->
           function s2 ->
                    match s1 with
                      [] -> s2
                    | h::t ->
                       match s2 with
                         [] -> s1
                       | _ ->
                          let rest = union t s2 in
                          if member h s2 then rest else h::rest

let _ = assert((union [] []) = [])
let _ = assert((union [0] []) = [0])
let _ = assert((union [1;2;3] [1;2;3]) = [1;2;3])
let _ = assert((union [1;2;3] [4;5;6]) = [1;2;3;4;5;6])              
let _ = assert((union [(1,2)] []) = [(1,2)])
let _ = assert((union [(1,2)] [(1,2);(3,4);(5,6)]) = [(1,2);(3,4);(5,6)])
let _ = assert((union [(1,2)] [(3,4);(5,6)]) = [(1,2);(3,4);(5,6)])
let _ = assert((union [[1;2;3]] [[1;2;3];[4;5;6];[8;9;10];[11;12;13]]) = [[1;2;3];[4;5;6];[8;9;10];[11;12;13]] )                            
let _ = assert((union [[1;2;3]] [[4;5;6];[8;9;10];[11;12;13]]) = [[1;2;3];[4;5;6];[8;9;10];[11;12;13]] )     

let rec (fastUnion : 'a list -> 'a list -> 'a list) =
  function s1 ->
           function s2 ->
                    match s1 with
                      [] -> s2
                    | h1::t1 ->
                       match s2 with
                         [] -> s1
                       | h2::t2 ->
                          if h1 = h2 then h1::(fastUnion t1 t2) else
                            if h1 < h2 then h1::(fastUnion t1 s2) else
                              h2::(fastUnion s1 t2)

let _ = assert((fastUnion [] []) = [])
let _ = assert((fastUnion [1] []) = [1])
let _ = assert((fastUnion [] [1]) = [1])              
let _ = assert((fastUnion [1;2;3] [1;2;3]) = [1;2;3])
let _ = assert((fastUnion [1;2;3] [4;5;6]) = [1;2;3;4;5;6])
              
let (intersection : 'a list -> 'a list -> 'a list) = 
  function s1 ->
           function s2 ->
                    match s1 with
                      [] -> []
                    | h1::t2 ->
                       match s2 with
                         [] -> []
                       | _ -> List.filter (function x -> member x s2 = true) s1 

let _ = assert((intersection [] []) = [])
let _ = assert((intersection [1] []) = [])
let _ = assert((intersection [1;2;3] [4;5;6]) = [])
let _ = assert((intersection [1;2;3;4;5] [4;5;6]) = [4;5])
let _ = assert((intersection [(1,2)] []) = [])
let _ = assert((intersection [(1,2)] [(3,4);(5,6)]) = [])              
let _ = assert((intersection [(1,2)] [(1,2);(3,4);(5,6)]) = [(1,2)])              
let _ = assert((intersection [[1;2;3]] [[1;2;3];[4;5;6];[8;9;10];[11;12;13]]) = [[1;2;3]] )
let _ = assert((intersection [[1;2;3]] [[4;5;6];[8;9;10];[11;12;13]]) = [] )              
                                          
let rec (setify : 'a list -> 'a list) =
  function s1 ->
           match s1 with
             [] -> []
           | h::t ->
              let inside = member h t in
              let rest = setify t in
              if inside then rest else h::rest
                                            
let _ = assert((setify []) = [])
let _ = assert((setify [1;1;1;1;1;1]) = [1])
let _ = assert((setify [0;1;1;1;1;1;1]) = [0;1])              
let _ = assert((setify [1;1;2;3;4;4;4;5;5;5;6;6;7;8;9;9]) = [1;2;3;4;5;6;7;8;9])
let _ = assert((setify [(1,2);(1,2);(1,2);(1,2);(1,2)] ) = [(1,2)])
let _ = assert((setify [(1,2);(1,2);(1,2);(1,3);(1,5);(1,5)] ) = [(1,2);(1,3);(1,5)])              
let _ = assert((setify [[1;2;3];[1;2;3];[1;2;3];[1;2;3]]) = [[1;2;3]] )
let _ = assert((setify [[1;2;3];[1;2;3];[3;2;3];[2;2;3];[2;2;3]]) = [[1;2;3];[3;2;3];[2;2;3]] )
              
let rec append_to_list x l =
  match l with
    [] -> l
  | h::t -> (x::h)::h::(append_to_list x t)                                               

let rec (powerset : 'a list -> 'a list list) =
  function s ->
           match s with
             [] -> [[]]
           | h::t -> (append_to_list h (powerset t)) 


(* Problem 2 *)

let rec (partition : ('a -> bool) -> 'a list -> 'a list * 'a list) =
  function func ->
           function l ->
                    match l with
                      [] -> ([],[])
                    | h::t ->
                       let rest = partition func t in
                       let pred = func h in
                       match rest with
                           ([],[]) -> if pred then ([h],[]) else ([],[h])
                         | (f,s) -> if pred then (h::f, s) else (f, h::s)
                                      
                            

let rec (whle : ('a -> bool) -> ('a -> 'a) -> 'a -> 'a) =
  function cond ->
           function func ->
                    function x ->
                             let pred = cond x in
                             match pred with
                               true -> (whle cond func (func x))
                             | false -> x 
                             
let rec (pow : int -> ('a -> 'a) -> ('a -> 'a)) =
  function n ->
           function func ->
                    match n with
                      0 -> (function v -> v)
                    | _ -> (function v -> (func ((pow (n-1) func) v) ))
