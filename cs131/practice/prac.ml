exception ImplementMe

(* Problem 1: Vectors and Matrices *)

(* type aliases for vectors and matrices *)
type vector = float list
type matrix = vector list


let rec (transpose : matrix -> matrix) =
  function m1 ->
	   match m1 with
	     [] -> []
	   | h::t -> (transpose h)@(transpose t)


	   
(*
[[1;2]
 [4;5]]
*)	   


(*
h = [1;2]
t = [4;5]
*)
	      
(*
[[1;4]
 [2;5]]
*)	   
