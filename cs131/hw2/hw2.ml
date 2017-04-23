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

(*let _ = assert( (evalExp (BinOp(BinOp(Num 1.0, Add, Num 2.0), Mult, Num 3.0)) ) = 9.0 *)
              
                                        
(* a type for stack instructions *)	  
type instr = Push of float | Swap | Calculate of op


                                                   
let rec exec_helper calc inst =  
  match inst with
    [] -> let res::t = calc in res
  | h::t ->
     match h with
       Push(x) -> (exec_helper (x::calc) t)
     | Swap -> let h1::h2::t2 = calc in (exec_helper (h2::h1::t2) t)
     | Calculate(y) ->
        let h1::h2::t2 = calc in
        match y with
          Add -> (exec_helper ((h2 +. h1)::t2) t)
        | Sub -> (exec_helper ((h2 -. h1)::t2) t)
        | Mult -> (exec_helper ((h2 *. h1)::t2) t)
        | Div -> (exec_helper ((h2 /. h1)::t2) t)

let (execute : instr list -> float) =
  function i ->
           match i with
             [] -> 0.0
           | _ -> exec_helper [] i

let (compile : exp -> instr list) =
  function e ->
           let rec compile_helper e l =
             match e with
               Num(x) -> Push(x)::l
             | BinOp(a,b,c) -> (compile_helper a l) @
                                 (compile_helper c l) @ [(Calculate(b) )]
           in compile_helper e []                                                         


let rec decompile_helper inst e =
  match inst with
    [] -> let exp::t = e in exp
  | h::t ->
     match h with
       Push(x) -> (decompile_helper t (Num(x)::e) )
     | Swap ->
        let BinOp(a,b,c)::t2 = e in (decompile_helper t (BinOp(c,b,a)::t2))
     | Calculate(x) ->
        let h1::h2::t2 = e in
        (decompile_helper t (BinOp(h2, x, h1)::t2) )                             
          
let (decompile : instr list -> exp) =
  function instructions ->
           match instructions with
             [] -> Num(0.0)
           | _ -> decompile_helper instructions []

         

(* EXTRA CREDIT *)        
let (compileOpt : exp -> (instr list * int)) =
  raise ImplementMe

