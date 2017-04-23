exception ImplementMe

(* Problem 1: Vectors and Matrices *)

(* type aliases for vectors and matrices *)            
type vector = float list                                 
type matrix = vector list

let (vplus : vector -> vector -> vector) =
  function v1 ->
           function v2 ->
                    List.map2 (+.) v1 v2  

let (mplus : matrix -> matrix -> matrix) =
  function m1 ->
           function m2 ->
                    List.map2 vplus m1 m2

let (dotprod : vector -> vector -> float) =
  function v1 ->
           function v2 ->
                    let sol1 = List.map2 ( *. ) v1 v2 in
                    List.fold_left ( +. ) 0.0 sol1

let build_row l acc =
  match l with
    [] -> acc
  | _ ->
     match acc with
       [] -> List.map (fun el1 -> [el1]) l
     | _ -> List.map2 (fun el1 el2 -> el1::el2) l acc


let (transpose : matrix -> matrix) =
  function m -> 
           match m with
             [] -> m
           | _ -> List.fold_right build_row m []

let row_by_row acc m1 m2 =
  match m1 with
    [] -> acc
  | h::t -> (List.map2 dotprod m1 m2)::acc  
  
let (mmult : matrix -> matrix -> matrix) =
  function m1 ->
           function m2->
                    match (m1,m2) with
                      ([],[]) -> []
                    | (_,_) ->
                       let mat = List.map (fun el -> List.map (fun f -> f el)
                                                              (List.map dotprod m1 )) (transpose m2) in
                       transpose mat

(* Problem 2: Calculators *)           
           
(* a type for arithmetic expressions *)
        
type op = Add | Sub | Mult | Div
type exp = Num of float | BinOp of exp * op * exp

let rec (evalExp : exp -> float) =
  function e ->
           match e with
             Num(x) -> x
           | BinOp(a,b,c) ->
              match b with
                Add -> (evalExp a) +. (evalExp c) 
              | Sub -> (evalExp a) -. (evalExp c)
              | Mult -> (evalExp a) *. (evalExp c)
              | Div -> (evalExp a) /. (evalExp c)

let _ = assert( (evalExp BinOp(BinOp(Num 1.0, Add, Num 2.0), Mult, Num 3.0) ) = 9 )
              
                                        
(* a type for stack instructions *)	  
type instr = Push of float | Swap | Calculate of op

let (execute : instr list -> float) =
  raise ImplementMe
      
let (compile : exp -> instr list) =
  raise ImplementMe

let (decompile : instr list -> exp) =
  raise ImplementMe

(* EXTRA CREDIT *)        
let (compileOpt : exp -> (instr list * int)) =
  raise ImplementMe

