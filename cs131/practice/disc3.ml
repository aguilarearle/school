


type 'a tree = Node of ('a * 'a tree list)


(*       1 
 *      / \
 *     2   3
 *       / | \
 *      4  6  7
 *      |     | \ 
 *      5     8  9 
 *               |
 *               10*)

			 
let mytree = Node(1, [Node(2, []);
		      Node(3, [Node(4, [Node(5, [])]);
			       Node(6, []);
			       Node(7, [Node(8, []);
				        Node(9, [Node(10, [])])])])])

let mirrored_tree =   Node(1, [Node(3, [Node(7, [Node(9, [Node(10,[])]);
						 Node(8, [])]);
				        Node(6, []);
				        Node(4, [Node(5, [])])]);
			       Node(2, [])])
			  
let rec mirror_tree  =
  function Node(v, Children) ->
	   let rec mirror_children c = match c with 
	     | [] -> []
	     | h::t -> (mirror_tree t)@[(mirror_tree h)]
	   in Node(v, (mirror_children children))

let rec mirror_tree2 =
  function Node(v, Children) ->
	   let reverse_one_more tree reversed = reversed@[mirror_tree2 tree]
	   in Node (v, (List.fold_right reverse_one_more children []))




let rec (getLeftMostOnes: 'a tree -> 'a list) =
  function Node(v, children) ->
	   match children with
	     [] -> [v]
	   | h::t -> [v]@(getLeftMostOnes t)

			      
 
