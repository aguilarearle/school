import java.util.*;

class C {
    void m() { System.out.println("C.m"); }

    void n() { this.m(); }
    
}

// D inherits code from C
class D extends C {

    // method overriding
    // key: dynamic dispatch, not static overloading
    void m() { System.out.println("D.m"); }
    
}


class Main {

    public static void main(String[] args) {
	D d = new D();
	d.m();

	C c = new D();
	c.m();// still invokes D.m

	new Main().callsM(c);// still invokes D.m

	c.n();// still invokes D.m!
	
    }

    void callsM(C c) {
	c.m();
	
    }
    
}


class Overload {
    void m(Object o) {System.out.println("Got an object");}
    void m(String s) {System.out.println(s);}
    void m(int i) {System.out.println(i);}
    
}

class Overload2 extends Overload {
    void m(String s) {System.out.println("D.m(s)");}
    
}

class Omain {
    public static void main(String[] args){
	Overload o = new Overload();
	String s = "hello";
	o.m(s);
	o.m(34);
	Object os = "hello2";
	o.m(os);

	Overload o2 = new Overload2();
	o2.m(s);
	/* Two phases
	   - At compile time: determine type signature of m: m(string)
	   - At runtime: dynamic dispatch withing the methods of that
	   - type
	   signature.
	*/
	o2.m(os);
	
    }
    
}

class A {
    public A(){
	System.out.println("A()");
	doStuff();
    }
    
    public void doStuff(){System.out.println("A.doStuff()");}
}

class B extends A{
    int i = 7;
    public B(){System.out.println("B()");}
    public void doStuff(){System.out.println("B.doStuff() " + i);}
}

class Base{
    public static void main (String[] args){
	B b = new B();
	//b.doStuff();
    }
}


interface Greeter {
    void greet();    
}

class Person implements Greeter {
    public void greet() { this.hello(new Integer(3)); }
    public void hello(Object o) { System.out.println("hello object"); }
    //public void hello(String s) { System.out.println("Person.hello(String)->void, " + s); }
}

class CSPerson extends Person {
    public void hello(Object o) { System.out.println("hello world!"); }
    
}

class FrenchPerson extends Person {
    public void hello(String s) { System.out.println("FrenchPerson.hello(String) -> void, " + s); }
    public void hello(Object o) { System.out.println("bonjour object"); }    
}




class P_test{

    public static void main (String[] args){
	
	Person p = new FrenchPerson();
	//((FrenchPerson) p).hello("Joe");
	p.hello("Joe");
	    
	//b.doStuff();
    }
}
