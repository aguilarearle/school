
/* The OO way above is more declarative and more modular:
	we can add new kinds of pieces without having to change the Board.move method
*/


/* Chess in OCaml:

type piece = Pawn of ... | Rook of ... | ...

let isLegalMove p x y =
	match p with
		Pawn ... -> ...
	|   Rook ... -> ...
	|   ...

Pattern matching in OCaml plays the role of dynamic dispatch in Java

*/


// Java's memory semantics

class Memory {

	public void addHi(List<String> l0) {
		l0.add("hi");
	}

	public static void main(String[] args) {
		List<String> l = new LinkedList<String>();
		l.add("hello");

		System.out.println(l.size());

		/* key points:
			variables never hold objects.
			they hold *object references*
				(pointers to objects).
			assignment and parameter passing
				copies the reference (the pointer), not the object.
		*/

		List<String> l2 = l;	// l2 and l now are references to the same object...
		l2.remove("hello");

		System.out.println(l.size());	// so now l has been affected by the l2.remove call

		new Memory().addHi(l);			// the same thing happens with parameter passing

		System.out.println(l.size());
	}

}

/* Still call-by-value parameter passing:
	evaluate the actual parameter to a value
	copy that value to the formal parameter

	this is what happens in Java, but it's 
	confusing because the values are always 
	object references (pointers), never objects.

	key property of call-by-value:
	  the value of the actual parameter cannot
	  	be changed by the call
*/

class Integer {
	int i;
	Integer(int i) { this.i = i; }

	public int value() { return i; }
	public void setValue(int i) { this.i = i; }

	public String toString() {
		return "" + this.i;
	}
}

class ParameterPassing {

	void plus(Integer a, Integer b) {
//		a = new Integer(a+b);    // this version leaves x.i in main() below as 3
		a.setValue(a.value() + b.value());  // this version changes x.i to 7

		// but either way, the value of x itself (a particular object reference) is
		// unchanged by the call.  that is the hallmark of call-by-value semantics

	}



	public static void main(String[] args) {
		Integer x = new Integer(3);
		Integer y = new Integer(4);
		new ParameterPassing().plus(x,y);
		System.out.println(x);
	}
}

/* Other Notes*/




