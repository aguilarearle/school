
let rec duplist l1 l2 =
  match (l1,l2) with
    ([],[]) -> true
  | ([], _) -> false
  | (_, []) -> false
  | (h1::t1, a::b::t2) -> if ((h1 == a) && (h1 == b)) then (duplist t1 t2) else false
	   
let rec subset l1 l2 =
  match (l1, l2) with
    ([],[]) -> true
  | ([], _) -> true
  | (_ , []) -> false
  | (h1::t1, h2::t2) -> if (h1 == h2) then (subset t1 t2) else (subset (h1::t1) t2)
