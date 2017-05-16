import java.util.*;

/* a type and a set of associated operations
	(messages) */
interface Set {
	boolean contains(String s);
	void addElem(String s);
}

// a client of the Set interface
class Client {
	void myClient(Set s) {
		s.addElem("bye")
		if (s.contains("hi"))
			s.addElem("there");
	}

}


class Main {
	public static void main(String[] args){
		Client c = new Client();;
		Set s = new ListSet()
		c.myClient(s);
		Set s2 = new ListSet();
		s2.addElem("hi");
		c.myClient(s2);
		System.out.println(s); 
		System.out.println(s2);
	}
}

/* Static typechecking of the client guarantees that the set
   is only used according to its interface.
   Can typecheck Client even though no Set implementations even exist!
   	- clean separation of interface and implementation
*/


/* Classes are implementations */
class ListSet implements Set {
	private List<String> l = new LinkedList<String>();

	public boolean contains(String s) {
		return this.l.contains(s);
	}

	public void addElem(String s) {
		if(!this.contains(s))
			this.l.add(s);
	}

	public String toString() {
		return this.l.toString()
	}
}
/* Typechecking of the implementation ensures that
    it provides method implementations for each of the Set's operations. */

/* Other implementations can be written as well:

	class ArraySet implements Set { ... }

*/

/* Java has parametric polymorphism:
	example: interface List<E> { ... } in the standard library (see above)
	E is a type variable (like 'a in OCaml)

	Clients explicitly instantiate E
	List<String> ls = ...;
	List<Car> cs = ...;

*/

/* But we also have a new kind of polymorphism:
	subtype polymorphism
*/

interface RemovableSet extends Set {
	void remove(String s);
}

class RListSet implements RemovableSet {
	private List<String> l = new LinkedList<String>();

	public boolean contains(String s) {
		return this.l.contains(s);
	}

	public void addElem(String s) {
		if(!this.contains(s))
			this.l.add(s);
	}

	public void remove(String s){
		this.l.remove(s);;
	}

	public String toString() {
		return this.l.toString()
	}
}

/* subtype polymorphism:  it's safe to pass
	an object of type S wherever an object of type
	T is expected, where S is a subtype of T */

/* Since RemoveableSet is a subset of Set then 
we can just change any of the declarations of 
Set to RemovableSet. In addition we can pass 
all subsets of Set to "Client"
*/

/* Why do we also need parametric polymorphism
 Implementation without polymorphism */
// Q: What is the disadvantage of using 
//    Object instead of parametric polymorphism
// A: 1. Can't enforce that all elements of the 
//    	 list are the same type
//
interface MyList /*<E>*/{
	//boolean contains(E e);
	boolean contains(Object o);
	// boolean add (E e)
	void add(Object o);
	// E get(int i)
	Object get(int i);
}
class MyListImpl implements MyList {
	protected List<String> l = new LinkedList<String>(); 
	//have to change from private to protected for inheritance
	// 
	public boolean contains(Object o) {
		return this.l.contains(o);
	}

	public void addElem(Object o) {
		if(!this.contains(o))
			this.l.add(o);
	}

	public  get(int i){
		return l.get(i)
	}

	public String toString() {
		return this.l.toString()
	}
}

class ListExample{
	public static void main(String[] args){
		MyList l1 = new MyListImpl();
		 l1.add(34);  
		 l1.add("hi");
		 l1.add("bye")
		String s = (string) li.get(0)
		System.out.println(l1);
		List<string> l2 = new LinkedList<string>
	     l2.add(34);
	     l2.add("bye")
		 l2.add("hi");
	     string s2 = l2.get(0)	
	}
}

// The following will signal an erros because e 
// does not neccesarily have "constains"
class Broken<E> {
	boolean m(E e){
		return e.contains("hi")
	}
}

// INHERITANCE

class RListSet extends ListSet implements RemovableSet{
	public void remove(String s){
		this.l.remove(s);
	}
}


/*Summary:
	- Subtyping is about interface compatibility: 
	  if S is a subtype of T, then it's safe to 
	  pass objects of type S where objects of type 
      T are expected.

	- injeritacne is about implementations:
	  if C inherits from D, the C gets to avoid 
	  duplicating code by getting it from D.

	- In Java:

	  1. interface I extends J --> Subtyping
	  2. class C implements I --> C objects have type I
	  3. class C extends D --> C inherits from D. 
	  						   C is also a subtype of D.	  						  
*/


// Subtyping VS Inheritance

// Example where I want sybtuping but not inheritance

// I don't want Square to inherit code from Rectangle
// But I do want to be able to pass Squares where 
// Rectangles are expected.
// Solution: Have them both implement a common interface.

interface Shape{
	float area();
	// ...
}

class Rectangle implements Shape{
	private float length, width;

	public float area() {return length * width; }

}	  						   

// Inheritance is not the answer if we want to 
// create a Square class. We are not really reusing
// code.

class Square implements Shape{
	private float side;
	private float area() {return side * side; }
}


// Example: Where I want inheritance but not subtyping

/*Recall ListSet from above

	class ListSet ...{
		protected List<String> l = ...;

		public boolean contains(String s) {...}
		...
	}
*/

/* How can ListBag avoid duplicating code but is 
   not a subtype of ListSet?	*/


// Solution: Introduce a superclass for the shared code.
abstract class ListCollection {
	protected List<String> l = ...;

	public boolean contains(String s) {...}

	public abstract addElem(String s);

}

// class ListSet exteds ListCollections { ... }

class ListBag extends ListSet{
	// Inherit l, contains, etc..

	void addElem(String s) {}

}
