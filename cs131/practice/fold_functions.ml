
let rec (fold_left : ('a -> 'b ->'a) -> 'a -> 'b list -> 'a ) =
  fun f acc l ->
  match l with
    [] -> acc
  | h::t -> (fold_left f (f acc h) t)  

(*=================================Fold Right=================================*)

let length l = List.fold_right (fun elem acc -> acc + 1) l 0

let rev l = List.fold_right (fun elem acc -> acc@[elem]) l []                               
let map f l = List.fold_right (fun elem acc -> (f elem)::acc) l []

let filter f l = List.fold_right (fun elem acc ->
                     if (f elem) then elem::acc else acc) l []

(*==================================Fold Left==================================*)

let length2 l = List.fold_left (fun acc elem -> acc + 1) 0 l

let rev2 l = List.fold_left (fun acc elem -> elem::acc) [] l
                            
let map2 f l = List.fold_left (fun acc elem -> acc@[(f elem)]) [] l
                              
let filter2 f l = List.fold_left (fun acc elem ->
                    if (f elem) then acc@[elem] else acc) [] l


(*===============================Insertion Sort===============================*)                                 
let insertion_sort l =
  let rec insert l e =
    match l with
      [] -> [e]
    | h::t -> if (e > h) then h::(insert t e) else e::l in
  List.fold_left insert [] l
                                                        
              


                                 
                                 
