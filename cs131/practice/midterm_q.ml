(*
let cprod1 l1 l2 =
  match l1 with
    [] -> []
  | _ -> List.map
           (fun el -> List.map (fun f el -> f el) (List.map (fun x el -> (x,el)) l1)) l2
                  
          
let cprod2 l1 l2 =
  let helper x rest =
      (List.map (function y -> (x,y)) l2)@rest in
  List.fold_right helper l1 []

                 (*List.fold_right (fun x rest -> (List.map (function y -> (x,y)) l2)@rest) l1 []*)
  
let add x s =
  if (s x) then s else (function y -> (s x) || (y=x))

                         
let cprod3 s1 s2 =
  fun x y -> (s1 x) && (s2 y)


let opt2exn f =
  fun2 x ->
  match (f x) with
    None -> raise Error
  | Some(v) -> v
    

let exn2opt f =
  fun2 x ->
  try Some(f x)
  with Error -> None

let rec d1tod2 l =
  match l with
    [] -> Empty
  | (n,e)::t -> Entry( n, e,  (d1tod2 t)  )
  
let rec d1tod2' l =
  List.fold_right (function (x,y) rest -> Entry(x,y,rest) ) l Empty
                  
let union3 d1 d2 =
  (function x ->
            try (d2 x)
            with Not_found -> (d1 x))
 *)
(*
let concat l1 l2 =
  let rec helper acc l = 
    match l with
      [] -> acc
    | h::t -> (helper (acc@[h]) t)
  in helper l2 l1


let rec hasOddLength l = 
  match l with
    [] -> false
  | _::t -> not (hasOddLength t)

let hasOddLength' l =
  List.fold_right (function _ rest -> (not rest) ) l false


let union s1 s2 =
  function x -> (s1 x) || (s2 x)

*)                            
(*
let init = Off
                            
let toggle l = 
  match l with
    Off -> On
  | On -> Off
            
let isOn l =
  match l with
    On -> true
  | _ -> false
*)

(*                            
let init = false

let toggle l = not l

let isOn l = l                   
*)
               
(*     
let rec joindicts d1 d2 =
  match d1 with
    [] -> []
  | (k,v)::rest ->
     match (get1 v d2) with
       None -> joindicts rest d2
     | Some(w) ->  (k,w)::(joindicts rest d2)

let joindicts' d1 d2 =
  List.fold_right
    (fun (k,v) rest -> match (get1 v d2) with
                         None -> rest
                       | Some(w) -> (k,w)::rest) d1 []

type tree = Leaf | Node of int * tree list

let incTree n t =
  match t with
    Leaf -> Leaf
  | Node(v, rest) -> Node(v+n, List.map (incTree n) rest )

                         
let rec intsfrom n =
  Cons(n, (function () -> (intsfrom (n+1) ) ))


      
let rec lazymap f l =
  match l with
    Nil -> Nil
  | Cons(v, rest) -> Cons((f v), (function () -> lazymap f (rest ())) )
*)

(*
let rec count n l =
  match l with
    [] -> 0
  | h::t -> if (n = h) then (1 + (count n t)) else (count n t)

let count' n l =
  List.fold_right (fun x rest -> (if (x = n) then 1 else 0) + rest ) l 0


let init c = 0
let increment c = c+1
let value c = c
let reset c = 0
                
let inic c = Zero
let rec increment c = Inc(c)

let rec value c =
  match c with
    Zero -> 0
  | Inc(c1) -> 1 + (value c1)
                    
let rec value c = Zero
                    
let rec plus i1 i2 =
  match i1 with
    Z -> i2
  | S(n) -> S(plus n i2)
             
                    
let rec leq m n  =
  match (i1, i2) with
    (Z, Z) -> true
  | (Z, _) -> true
  | (_, Z) -> false
  | (S m',s n') -> leq m' n'
*)

let rec zip (l1,l2) =
  match (l1, l2) with
    ([],[]) -> []
  | (h1::t1, h2::t2) -> (h1,h2)::(zip (t1,t2))

let rec zip' (l1,l2) =
  List.map2 (fun x y -> (x,y)) l1 l2

let rec unzip l =
  match l with
    [] -> ([],[])
  | (x,y)::t ->
     let (x',y') = unzip t
     in  (x::x', y::y')

(*           
let remove_dups l =
  let rec helper acc l =
    match l with
      [] -> acc
    | e1::e2::rest ->
       if (e1 = e2) then helper e1::acc rest else helper e1::e2::acc rest
  in List.fold_right helper 
*)
                     
let rec remove_dups l =
  match l with
    [] -> []
  | h1::h2::t -> if (h1 = h2) then remove_dups (h2::t) else h1::(remove_dups (h2::t))
  | h::t ->
     let rest = (remove_dups t) in
     match rest with
       [] -> h::rest
     | h1::t1 -> if (h = h1) then h1::t1 else h::h1::t1
                   
let remove_dups_1 l =
  let rec helper l acc =
    match l with
      [] -> acc
    | h1::t1 ->
       (match acc with
          [] -> (helper t1 (h1::acc))
        | h2::t2 -> if (h1=h2) then (helper t1 acc) else (helper t1 (h1::acc)))
  in List.fold_right helper l [] 
  
  
                     
