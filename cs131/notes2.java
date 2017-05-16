/*

Key concepts in Object Oriented Languages:

Subtyping: about interface compatibility 
  - A form of polymorphism

*/

class C {
	void m() { System.out.println("C.m")}

	void n() {this.m();} // At compile time "this" is of type C.
}

// D inherits code from C

class D extends C {
	// Method overriding
	// ketL dynamic distpatch, not static overloading
	void m() {System.out.println("D.m")}
}

class Main{

	public static void main(string[] args){
		D d = new D();
		d.m();
		// Creating a D but assigning it to a C.
		// We determine which method toinvoke at runtime based
		// on what type of object you are holding at run time
		// so we get "D.m" instead of "C.m" 
		C c = new D();
		c.m();

		new Main().callsM(new D());

		c.n();

	}

	void callsM(C c){
		c.m();
	}
}

/* Every method call is dynamically dispatched:

   - look up the method in the class of the receiver
   	 object at run time
   - If it's not there, look in its superclass recursively.
*/


class Overload {
	void m(Object o) {System.out.println("Got an object");}
	void m(String s) {System.out.println(s);}
	void m(int i) {System.out.println(i);}
}   

class Overload2 extends Overload {
	void m(string s) {System.out.println("D.m(s)")}
}

class Omain {
	public static void main(string[] args){
		Overload o = new Overload();
		Sring s = "hello";
		o.m(s);
		o.m(34);
		Object os = "hello2";
		o.m(os)

		Overload o2 = new Overload2();
		o2.m(s);
		/* Two phases
		     - At compile time: determine type signature of m: m(string)
		     - At runtime: dynamic dispatch withing the methods of that type
		       signature.
		*/
		o2.m(os);
	}
}

/*
  1. Object-Oriented: smart objects that "know" how to do certain things.
  	 Objects interact by asking one anohter to do things.
*/

// Simple Example: Chess game

interface Piece{
	// ...
	boolean IsLegaMove(int x, int y){

	}
}  	 

abstract class PieceImpl implements Piece{
	int x, int y;
}

class Rook implements Piece{
	// ...
	public boolean IsLegaMove(int x, int y){

	}
}

class Pawn implements Piece{
	// ...
	public boolean IsLegaMove(int x, int y){

	}	
}

class Board {
	void move(Piece p, int x, int y){
		/* The non-objected oriented way
		if (p is a Rook){
			// ...
		} else if (p is a pawn){
			// ...
		}*/

		// Correct way
		if (p.IsLegaMove(x,y)){
			// Update Board
		}
	}


}
