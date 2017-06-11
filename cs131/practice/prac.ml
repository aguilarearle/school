type nat = Z | S of nat

let rec plus m n =
  match m with
    Z -> n
  | S m' -> S((plus m' n))

let rec leq m n =
  match (m,n) with
    (Z,Z) -> true
  | (Z, S(n')) -> true
  | (S(m'), Z) -> false
  | (S(m'), S(n')) -> (leq m' n')
