
(* A simple test harness for the MOCaml interpreter. *)

(* put your tests here:
   each test is a pair of a MOCaml declaration and the expected
   result:
     - the MOCaml declaration is a string of exactly what you would type into the interpreter prompt,
       without the trailing ";;"
     - the expected result is a string of exactly what you expect the interpreter to print as a result
   use the string "dynamic type error" as the result if a DynamicTypeError is expected to be raised.
   use the string "match failure" as the result if a MatchFailure is expected to be raised.
   use the string "implement me" as the result if an ImplementMe exception is expected to be raised

   call the function runtests() to run these tests
*)
let exprTests = [
  (* int *)
    ("3", "3");
  (* bool *)
    ("true", "true");
    ("false", "false");
  (* varTests *)
  (* BinOpTests *)
  (* Negate *)
    ("-(2)", "-2");
    ("-(-4)", "4");
  (* if *)
    ("if (3 > 3) then (3+1) else (4+1)", "5");
    ("if (3 = 3) then (3+1) else (4+1)", "4");
  (* functionTests *)
  (* matchTests *)
  (* Tuple *)
    ("(2*2, 3*2, 4*2)", "(4, 6, 8)");
    ("((3*2,3+2),4)", "((6, 5), 4)");
  (* Data *)
    ("Some(3)", "Some 3");
    ("None", "None");
    
  ]

let varTests = [
    ("let x = 34", "val x = 34");
    ("let x = 35", "val x = 35");
    ("y", "dynamic type error");
    
  ]

let binOpTests = [
    ("let x = 34", "val x = 34");
    ("x + 4", "38");
    ("x - 4", "30");
    ("x * 4", "136");
    ("x = 33", "false");
    ("x = 34", "true");
    ("x > 34", "false");
    ("x > 33", "true");
    ("x + true", "dynamic type error");
    
  ]

let matchTests = [
  (* Match *)
    ("match 4 with 2->4 | 3->6 | 4->8", "8"); (* IntPat  *)
    ("match 4 with 2->4 | 3->6", "match failure"); (* IntPat  *)
    ("match true with false -> 1 | true -> 9", "9"); (* BoolPat *)
    ("match 5 with _ -> 10", "10"); (* WildcardPat *)
    ("match 5 with x -> x + 6", "11"); (* VarPat *)
    ("match (3,4) with (x,y) -> x * y", "12"); (* TuplePat *)
    ("match Some (13) with Some (n) -> n", "13"); (* DataPat *)
    ("match None with None -> 1", "1");
    ("match Some (3) with Some(x) -> x", "3");
    ("match (3, 4) with (x, y) -> (y, x)", "(4, 3)");
    ("match 4 with x -> x", "4");
    ("match Some (3, 4) with Some (x, y) -> (y, x)", "(4, 3)");
    
  ]

let functionTests = [
    ("let flip = function (x, y) -> (y, x)", "val flip = <fun>");
    ("flip (4, 3)", "(3, 4)");
    ("let v = 15", "val v = 15");
    ("let rec cd n = match n with 0 -> 1999 | _ -> cd (n - 1)", "val cd = <fun>");
    ("cd 2", "1999");
    
  ]

                      (* The Test Harness
   You don't need to understand the code below.
                       *)

let testOne test env =
  let decl = main token (Lexing.from_string (test^";;")) in
  let res = evalDecl decl env in
  let str = print_result res in
  match res with
    (None,v) -> (str,env)
  | (Some x,v) -> (str, Env.add_binding x v env)

let d = 140

let test tests =
  let (results, finalEnv) =
    List.fold_left
      (fun (resultStrings, env) (test,expected) ->
        let (res,newenv) =
          try testOne test env with
            Parsing.Parse_error -> ("parse error", env)
          | DynamicTypeError _ -> ("dynamic type error", env)
          | MatchFailure -> ("match failure", env)
          | ImplementMe s -> ("implement me", env) in
        (resultStrings@[res], newenv)
      )
      ([], Env.empty_env()) tests
  in
  List.iter2
    (fun (t,er) r ->
      let sfx = if er=r then "✔ ok" else "✗ expected " ^ er in
      let pfx = t ^ " ➤ " ^ r in
      let dln = (max 10 (d + 2 - (String.length pfx) - (String.length sfx))) in
      let dts = " " ^ (String.make dln  '.') ^ " " in
      print_endline (pfx ^ dts ^ sfx))
    tests results

let testWrap =
  fun cases title ->
  let l = (String.length title) in
  let h = print_endline ("---[ " ^ title ^ " ]" ^ (String .make (d - 7 - l) '-')) in
  let f = test cases in
  print_endline (String .make d '-')

let recTest1 = [
    ("let myList = List (1, List (2, List (3, None)))",
     "val myList = List (1, List (2, List (3, None)))");

    ("let rec incList l = match l with None -> None | List(e, sl) -> List(e + 1, incList sl)",
     "val incList = <fun>");

    ("let rec mapList (f, lst) = match lst with None -> None | List(e, sl) -> List(f e, mapList (f, sl))",
     "val mapList = <fun>");

    ("let double = function x -> x * 2", "val double = <fun>");

    ("incList myList", "List (2, List (3, List (4, None)))");
    ("mapList (double, myList)", "List (2, List (4, List (6, None)))");
    
  ]

let recTest2 = [
    ("let rec double i = i*2", "val double = <fun>");
    ("let rec twice f = function x -> f(f(x))", "val twice = <fun>");

    ("let x = 4", "val x = 4");
    ("let rec double x = x*2", "val double = <fun>");
    ("let rec twice g =  g(g(x))", "val twice = <fun>");

    ("(twice double)","16");
    ("(twice double) 4","dynamic type error");
    
  ]


let piazzaTests = [
    ("3", "3");
    ("false", "false");
    ("let x = 34", "val x = 34");
    ("y", "dynamic type error");
    ("x + 4", "38");
    ("let double = function x -> x * 2", "val double = <fun>");
    ("double 6", "12");
    ("let two = 2", "val two = 2");
    ("let addTwo = function x -> x + two", "val addTwo = <fun>");
    ("addTwo 5", "7");
    ("let two = 3", "val two = 3");
    ("addTwo 5", "7");
    ("let add = function a -> function b -> a + b", "val add = <fun>");
    ("add 10 (-3)", "7");
    ("let p = (1, 2)", "val p = (1, 2)");
    ("let leaf = Leaf", "val leaf = Leaf");
    ("let node = Node(Leaf, 1, Leaf)", "val node = Node (Leaf, 1, Leaf)");
    ("match x with 34 -> true | _ -> false", "true");
    ("match x with 35 -> true | _ -> false", "false");
    ("if true then 1 else 0", "1");
    ("match p with (a, b) -> a + b", "3");
    ("match p with (a, b, c) -> a + b + c", "match failure");
    ("match node with Node(l, v, r) -> (l, r)", "(Leaf, Leaf)");
    ("let iffPositive = function x -> if x > 0 then x else false", "val iffPositive = <fun>");
    ("iffPositive 3", "3");
    ("iffPositive (-3)", "false");
    ("let rec sumTree n = match n with Leaf -> 0 | Node(l, v, r) -> v + (sumTree l) + (sumTree r)", "val sumTree = <fun>");
    ("let a = Node(Leaf, 2, Leaf)", "val a = Node (Leaf, 2, Leaf)");
    ("let b = Node(Leaf, 3, Leaf)", "val b = Node (Leaf, 3, Leaf)");
    ("let c = Node(a, 11, b)", "val c = Node (Node (Leaf, 2, Leaf), 11, Node (Leaf, 3, Leaf))");
    ("let d = Node(Leaf, 5, Leaf)", "val d = Node (Leaf, 5, Leaf)");
    ("let root = Node(c, 100, d)", "val root = Node (Node (Node (Leaf, 2, Leaf), 11, Node (Leaf, 3, Leaf)), 100, Node (Leaf, 5, Leaf))");
    ("sumTree root", "121");
    ("let rec fib x = match x with 0 -> 0 | 1 -> 1 | n -> (fib (n-1)) + (fib (n-2))", "val fib = <fun>");
    ("fib 19", "4181");
    ("let rec fibIt a = function b -> function n -> if n > 0 then (fibIt b (a+b) (n-1)) else a", "val fibIt = <fun>");
    ("fibIt 0 1 19", "4181");
    
  ]


let gitTest1 = [
    ("3", "3");                                  (* IntConst of int *)
    ("true", "true");                               (* BoolConst of bool *)
    ("false", "false");                             (* BoolConst of bool *)
    ("let x = 34", "val x = 34");                   (* Let of string * moexpr *)
    ("x", "34");                                    (* Var of string *)
    ("y", "dynamic type error");                    (* Var of string *)
    ("3 + 4", "7");                                 (* BinOp of moexpr * Plus * moexpr *)
    ("x + 4", "38");                                (* BinOp of moexpr * Plus * moexpr *)
    ("3 * 4", "12");                                (* BinOp of moexpr * Minus * moexpr *)
    ("3 + 4", "7");                                 (* BinOp of moexpr * Times * moexpr *)
    ("3 = 4", "false");                             (* BinOp of moexpr * Eq * moexpr *)
    ("5 = 5", "true");                              (* BinOp of moexpr * Eq * moexpr *)
    ("3 > 4", "false");                             (* BinOp of moexpr * Gt * moexpr *)
    ("4 > 3", "true");                              (* BinOp of moexpr * Gt * moexpr *)
    ("-x", "-34");                                  (* Negate of moexpr *)
    ("-8", "-8");                                   (* Negate of moexpr *)
    ("-(8+9)", "-17");                              (* Negate of moexpr *)
    ("let y = 50", "val y = 50");                   (* Let of string * moexpr *)
    ("if x > y then true else false", "false");     (* If of moexpr * moexpr * moexpr *)
    ("if y > x then true else false", "true");       (* If of moexpr * moexpr * moexpr *)

    ("x + 4", "38");
    ("x - 4", "30");
    ("x * 4", "136");
    ("x = 34", "true");
    ("x = 0", "false");
    ("x > 33", "true");
    ("35 > x", "true");
    ("x > 35", "false");
    ("-x", "-34");
    ("33 > -x", "true");
    ("-[]", "dynamic type error");
    ("if 1 then 2 else 3", "dynamic type error");
    ("if [2; 3] then 5 else 10", "dynamic type error");
    ("if 2 > 5 then 20 else 3", "3");

    ("let newfunc = function x -> x * x", "val newfunc = <fun>");
    ("newfunc 2", "4");
    ("newfunc(2)", "4");
    ("let timesThreeNums = function a -> function b -> function c -> a * b * c", "val timesThreeNums = <fun>");
    ("timesThreeNums 2 3 4", "24");
    ("let multSix = timesThreeNums 2 3", "val multSix = <fun>");
    ("multSix 2", "12");
    ("let a = function true -> false", "val a = <fun>");
    ("a 1", "match failure"); (* not sure about this one *)
    ("let b = function _ -> true", "val b = <fun>");
    ("b 1", "true");
    ("b [1; 2; 3]", "true");
    ("(function _ -> x) 42", "34");

    ("match 5 with s -> s", "5");
    ("match 10 with _ -> x", "34");
    ("match 20 with x -> x", "20");
    ("match 1 with 1 -> (match 2 with 1 -> false) | _ -> true", "match failure");

    ("let rec recTest num = match num with 0 -> 0 | s -> 1 + (if s > 0 then recTest(s-1) else recTest(s+1))", "val recTest = <fun>");
    ("recTest 10", "10");
    ("recTest (-10)", "10");
    ("let rec recTest2 _ = recTest2", "val recTest2 = <fun>");
    ("recTest2", "<fun>");
    ("recTest2 1 2 3 4 5 6 7 8 9 10 11 12", "<fun>");
    ("let rec fact n = if n > 0 then n * fact (n - 1) else 1", "val fact = <fun>");
    ("fact 5", "120");

    ("let f = function x -> x", "val f = <fun>");
    ("let g = function x -> x + (f x)", "val g = <fun>");
    ("let f = function x -> x * 23", "val f = <fun>");
    ("g 5", "10");
    ("f 5", "115");
    ("let rec f g = match g with f -> f 0", "val f = <fun>");
    ("f (function _ -> 5)", "5"); (* recursive name shadowed by match. *)

    ("false + false", "dynamic type error");
    ("1 + false", "dynamic type error");
    ("1 - false", "dynamic type error");
    ("1 * false", "dynamic type error");
    ("1 > false", "dynamic type error");
    ("1 = false", "dynamic type error");

    ("let f = 2", "val f = 2");
    ("let rec f f = match f with 0 -> f | _ -> (f+1)", "val f = <fun>");
    ("f 1","2")
      
  ]


let gitTest2 = [

    (* Declarations *)
    ("3", "3");
    ("false", "false");
    ("let  i1 = 10", "val i1 = 10");
    ("let  i2 = 34", "val i2 = 34");
    ("let  b1 = false", "val b1 = false");
    ("let  b2 = true", "val b2 = true");
    ("let x = 34", "val x = 34");
    ("x" , "34");
    ("y", "dynamic type error");
    ("x + 4", "38");
    ("let b = true" ,"val b = true");
    ("x + b", "dynamic type error");

    (* BinOp *)
    ("let  i3 = i1 * i2", "val i3 = 340");
    ("let i3double = i3 + i3", "val i3double = 680");
    ("i3 > i1", "true");
    ("i1 = 10", "true");
    ("b1 = b2", "dynamic type error");
    ("5 + 6", "11");
    ("13 - 3", "10");
    ("15 * 2", "30");
    ("5 = 1" , "false");
    ("5 = 5" , "true");
    ("5 > 1" , "true");
    ("5 > 5" , "false");
    ("5 > false" , "dynamic type error");
    ("i3 > false" , "dynamic type error");
    ("5 > b1" , "dynamic type error");
    ("true > false" , "dynamic type error");
    ("true = true" , "dynamic type error");
    ("false = true" , "dynamic type error");
    ("1 = true" , "dynamic type error");
    ("true = 10" , "dynamic type error");
    ("true + false" , "dynamic type error");
    ("true - false" , "dynamic type error");
    ("true * false" , "dynamic type error");
    ("5 + false", "dynamic type error");
    ("[5] > 7" , "dynamic type error");
    ("[5] = 5" , "dynamic type error");
    ("[17] * 40" , "dynamic type error");
    ("30 - [15]" , "dynamic type error");

    (* If statements *)
    ("if true then 5 else false" , "5");
    ("if false then 5 else false" , "false");
    ("if i1>5 then i1 else 5" , "10");
    ("if i3=i1 then b1 else b2" , "true");
    ("if (5+10)>(12 -1) then (if b2 then 100 else true) else false" , "100");
    ("if (5+10)=(12 -1) then (if b2 then 100 else true) else false" , "false");
    ("if 1 then 5 else 10" , "dynamic type error");
    ("if i1 then 15 else 10" , "dynamic type error");
    ("if true then true + false else 14 " , "dynamic type error");
    ("if true = 1 then 10 else 1" , "dynamic type error");

    (* Pattern Matching *)
    ("match true  with  10 -> 100" , "match failure");
    ("match 10 with 14  -> true " , "match failure");
    ("match (10+5) with 0 -> false | 15 -> true" , "true");
    ("match (i3*2) with i2 -> (i2 + 1 )| i3double -> i1  " , "681");
    ("match (function x->x) with  14 ->  10" , "match failure");
    ("match [] with  10 -> 14 " , "match failure");
    ("match true with  [] ->  1" , "match failure");
    ("match [] with  true -> 0 " , "match failure");
    ("match 10 with []  -> 1  " , "match failure");
    ("match 10 with true  -> 0  " , "match failure");

    (* Non-Recursive Functions *)
    ("let f = (function x -> x + 1)" , "val f = <fun>");
    ("f 100" , "101");
    ("f true", "dynamic type error");
    ("f x", "35");
    ("let add = (function x ->  (function y -> x + y) )" ,"val add = <fun>");
    ("add 4 5" , "9");

    (* Recursive Functions *)
    ("let inc = function a -> a + 1", "val inc = <fun>");
    ("inc 5", "6");

    (*Using functions as parameters *)
    ("let sumList = (function l -> sumListH l (function x -> x) )", "val sumList = <fun>");
    ("let a = 50" ,"val a = 50");
    ("let inc2 = (function a -> (a + 1))", "val inc2 = <fun>");
    ("let rec inc2r a = a + 1", "val inc2r = <fun>");
    ("(inc2 ( inc2r (10 + 1) ) - 11 )= 2", "true");
    ("let funcDec = (function f -> (function a -> (f a) - 1) )", "val funcDec = <fun>");
    ("let rec fact2 n = match n with 0 -> 1 | _ -> n*(fact2 (n-1))", "val fact2 = <fun>");  (* fixed some typo *)
    ("let c1 = funcDec fact2 5" , "val c1 = 119");
    ("let c2 = funcDec fact2 (funcDec fact2 3)" , "val c2 = 119");
    ("let factDec = funcDec fact2", "val factDec = <fun>");
    ("let c3 = factDec 5", "val c3 = 119");
    ("c1 = c2", "true");
    ("(c1 - c3) = 0", "true");
    ("funcDec inc2r 17", "17");

    (* Functions Parameter Pattern Matching *)
    ("let fw = (function _ -> 5)", "val fw = <fun>");
    ("let rec gw _ = 5", "val gw = <fun>");
    
  ]

                 (* RUN THE TESTS *)

let t = (testWrap exprTests "tests")
let t = (testWrap exprTests "expr")
let t = (testWrap varTests "var")
let t = (testWrap binOpTests "binOp")
let t = (testWrap functionTests "function")
let t = (testWrap matchTests "match")
let t = (testWrap recTest1 "recursive1")
let t = (testWrap recTest2 "recursive2")
let t = (testWrap gitTest1 "git 1")
let t = (testWrap gitTest2 "git 2")

