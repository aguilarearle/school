let rec unzip l =
  match l with
    [] -> [[]]
   | (f,s)::t -> 
     let l1 = f :: (unzip t) in
     let l2 = s :: (unzip t) in
     l1::l2
