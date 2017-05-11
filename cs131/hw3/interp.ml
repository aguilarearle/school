
(* Name: Earle Aguilar
 
   UID: 804501476

   Others With Whom I Discussed Things:

   Other Resources I Consulted:
   
*)

(* EXCEPTIONS *)

(* This is a marker for places in the code that you have to fill in.
   Your completed assignment should never raise this exception. *)
exception ImplementMe of string

(* This exception is thrown when a type error occurs during evaluation
   (e.g., attempting to invoke something that's not a function).
   You should provide a useful error message.
*)
exception DynamicTypeError of string

(* This exception is thrown when pattern matching fails during evaluation. *)  
exception MatchFailure  

(* EVALUATION *)

(* See if a value matches a given pattern.  If there is a match, return
   an environment for any name bindings in the pattern.  If there is not
   a match, raise the MatchFailure exception.
 *)

           
let rec patMatch (pat:mopat) (value:movalue) : moenv =
  match (pat, value) with
      (* an integer pattern matches an integer only when they are the same constant;
	 no variables are declared in the pattern so the returned environment is empty *)
    (IntPat(i), IntVal(j)) when i=j -> Env.empty_env()
  | (BoolPat(i), BoolVal(j)) when i = j -> Env.empty_env()
  | (WildcardPat, _) -> Env.empty_env()
  | (VarPat(x), _) -> (Env.add_binding x value (Env.empty_env()))
  | (TuplePat(l1), TupleVal(l2)) ->
     (match (l1, l2) with
        ([], _) -> Env.empty_env()
      | (h1::t1, h2::t2) ->
         let l1_ = (TuplePat (t1)) in
         let l2_ = (TupleVal (t2)) in
         (Env.combine_envs (patMatch h1 h2) (patMatch l1_ l2_) )
      | _ -> raise MatchFailure      
     )
  | (DataPat(delim1, dat1), DataVal(delim2, dat2) ) when (delim1 = delim2) ->
     (match (dat1, dat2) with
        (None, None) -> Env.empty_env()
       | Some(p) , Some(v) -> (patMatch p v))    
  | _ -> raise MatchFailure

               
let rec patMatch_helper l e =
    match l with
      [] -> raise MatchFailure
    | (l', e')::t ->
       try (patMatch l' e, e') with
         _ -> (patMatch_helper t e)                  
(* Evaluate an expression in the given environment and return the
   associated value.  Raise a MatchFailure if pattern matching fails.
   Raise a DynamicTypeError if any other kind of error occurs (e.g.,
   trying to add a boolean to an integer) which prevents evaluation
   from continuing.
*)
let rec evalExpr (e:moexpr) (env:moenv) : movalue =
  match e with
      (* an integer constant evaluates to itself *)
    IntConst(i) -> IntVal(i)
  | BoolConst(b) -> BoolVal(b)
  | If(guard, thn ,els) ->
     (match evalExpr guard env with
        BoolVal(true) -> evalExpr thn env
      | BoolVal(false) -> evalExpr els env
      | _ -> raise (DynamicTypeError "Boolean guard expected.")
     )
  | Var(x) -> (try (Env.lookup x env) with _ -> raise (DynamicTypeError "Unbound Value"))
  | BinOp(l, o, r) ->
     let exp_l = (evalExpr l env) in
     let exp_r = (evalExpr r env) in
     (match (exp_l, exp_r) with
        (IntVal(x), IntVal(y)) ->
        (match o with
           Plus -> IntVal(x+y)
         | Minus -> IntVal(x-y)
         | Times -> IntVal(x*y)
         | Eq -> BoolVal(x = y)
         | Gt -> BoolVal(x > y)
        )
      | _ ->  raise (DynamicTypeError "BinOp operation valid only for ints.") 
     )
  | Negate(x) ->
     let exp_r = (evalExpr x env) in
     (match exp_r with
        IntVal(x) -> IntVal(-x)
      | _ -> raise (DynamicTypeError "Negate expects integer.")
     )
  | Function(var, bdy) -> FunctionVal(None, var, bdy, env)
  | FunctionCall(f,arg) ->
     (
       let func = (evalExpr f env) in
       match func with
         FunctionVal(Some v, var, bdy, f_env) ->
         (let env2 = (patMatch var (evalExpr arg env))
          in let env3 = (Env.combine_envs f_env env2)
             in let env4 = (Env.add_binding v func env3)
                in (evalExpr bdy env4 ))
       | FunctionVal(None, var, bdy, f_env) ->
          (let env2 = (patMatch var (evalExpr arg env))
           in let env3 = (Env.combine_envs f_env env2)
              in (evalExpr bdy env3))             
       | _ -> raise (DynamicTypeError "Function Expected.")
     )
  | Tuple(tp) ->
     TupleVal (List.map (function e -> evalExpr e env) tp)
  | Data(delim, dat) ->
     (
       match dat with
         None -> DataVal(delim, None)
       | Some (e) -> DataVal(delim, Some (evalExpr e env)))
  | Match(dat, patlist) ->     
     (let dat1 = evalExpr dat env in
      match patMatch_helper patlist dat1 with
        (env2, exp2) ->
        let env3 = (Env.combine_envs env env2) in
        (evalExpr exp2 env3) )
                  
(* Evaluate a declaration in the given environment.  Evaluation
   returns the name of the variable declared (if any) by the
   declaration along with the value of the declaration's expression.
*)
let rec evalDecl (d:modecl) (env:moenv) : moresult =
  match d with
    (* a top-level expression has no name and is evaluated to a value *)
    Expr(e) -> (None, evalExpr e env)
  | Let(v,e) -> (Some v, evalExpr e env)
  | LetRec(v,e) ->
     (match (evalExpr e env) with
        FunctionVal( _, var, bdy, env) -> (Some v, FunctionVal(Some v, var, bdy, env) )
      | _ -> raise (ImplementMe "Function Expected")
     )
       
                  
