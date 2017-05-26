/* Name: Earle Aguilar

   UID: 804501476

   Others With Whom I Discussed Things:

   Other Resources I Consulted:
   
*/

// import lists and other data structures from the Java standard library
import java.util.*;

// PROBLEM 1

// a type for arithmetic expressions
interface Exp {
    //    List<Instr> compile_res = new LinkedList<Instr>();
    double eval(); 	                       // Problem 1a
    List<Instr> compile(); 	               // Problem 1c
}

class Num implements Exp {
    protected double val;
    List<Instr> compile_res = new LinkedList<Instr>();
    
    public Num(double myval){
	val = myval;
    }

    public double eval(){
	return val;
    }

    public List<Instr> compile(){
	compile_res.add( new Push(val) );
	return compile_res;
    }
    
    public boolean equals(Object o) { return (o instanceof Num) && ((Num)o).val == this.val; }

    public String toString() { return "" + val; }
}

class BinOp implements Exp {
    protected Exp left, right;
    protected Op op;
    List<Instr> compile_res = new LinkedList<Instr>();
    public BinOp(Exp val1, Op operator, Exp val2){
	left = val1;
	op = operator;
	right = val2;
    }

    public double eval(){
	double res1 = left.eval();
	double res2 = right.eval();
	return op.calculate(res1,res2);
    }

    public List<Instr> compile(){
	compile_res.addAll(left.compile());
	compile_res.addAll(right.compile());
	compile_res.add(new Calculate(op));
	System.out.println(compile_res);
	return compile_res;
    }    

    public boolean equals(Object o) {
    	if(!(o instanceof BinOp))
    		return false;
    	BinOp b = (BinOp) o;
    	return this.left.equals(b.left) && this.op.equals(b.op) &&
		    	this.right.equals(b.right);
    }

    public String toString() {
	return "BinOp(" + left + ", " + op + ", " + right + ")";
    }
}

// a representation of four arithmetic operators
enum Op {
    ADD, SUB, MULT, DIV;

	double calculate(double x, double y) {
        switch(this) {
            case ADD:   return x + y;
            case SUB:  return x - y;
            case MULT:  return x * y;
            case DIV: return x / y;
        }
        throw new AssertionError("Unknown Op: " + this);
    }
}

// a type for arithmetic instructions
interface Instr {
    void inst_exec(Stack<Double> exec_lst);
}

class Push implements Instr {
    protected double val;

    public Push(double v){
	val = v;
    }

    public void inst_exec(Stack<Double> exec_lst){
	exec_lst.push(val);
    }
    
    public boolean equals(Object o) { return (o instanceof Push) && ((Push)o).val == this.val; }
    
    public String toString() {
	return "Push " + val;
    }
    
}

class Calculate implements Instr {
    protected Op op;

    public Calculate(Op operator){
	op = operator;
    }
    
    public void inst_exec(Stack<Double> exec_lst){
	double right = exec_lst.pop();
	double left = exec_lst.pop();
	double res = op.calculate(left, right);
	exec_lst.push(res);
    }
    public boolean equals(Object o) { return (o instanceof Calculate) && 
    						  ((Calculate)o).op.equals(this.op); }

    public String toString() {
		return "Calculate " + op;
    }    
}

class Instrs {
    protected List<Instr> instrs;
    protected Stack<Double> result = new Stack<Double>();
    
    public Instrs(List<Instr> instrs) { this.instrs = instrs; }

    public double execute() {
	for (Instr i: instrs){
	    i.inst_exec(result);
	}
	return result.pop();
    }
}


class CalcTest {
    public static void main(String[] args) {
	 //    // a test for Problem 1a
	// (1+2)*3
	Exp exp1 = new BinOp(new BinOp(new Num(1.0), Op.ADD, new Num(2.0)),Op.MULT,new Num(3.0));
	// (1-2)*5
	Exp exp2 = new BinOp(new BinOp(new Num(1.0), Op.SUB, new Num(2.0)),Op.MULT,new Num(5.0));
	// (-1 * 2) * 5
	Exp exp3 = new BinOp(new BinOp(new Num(-1.0), Op.MULT, new Num(2.0)),Op.MULT,new Num(5.0));
	// 0 - (10 / -2)
	Exp exp4 = new BinOp(new Num(0.0), Op.SUB, new BinOp(new Num(10.0), Op.DIV, new Num(-2.0)));
	// 5 - (10 * (6/3) )
	Exp exp5 = new BinOp(new Num(5.0), Op.SUB, new BinOp(new Num(10.0), Op.MULT,new BinOp(new Num(6), Op.DIV, new Num(3) ) ) );
	// (((1 + 3) / 2) - 0) + (((6-3) / 3) * 0)
	Exp exp6 = new BinOp(new BinOp(new BinOp(new BinOp(new Num(1) ,Op.ADD, new Num(3)),Op.DIV,new Num(2.0)) ,Op.SUB,new Num(0.0)) ,Op.ADD, new BinOp( new BinOp(new BinOp(new Num(6.0),Op.SUB,new Num(3.0)),Op.DIV,new Num(3.0)),Op.MULT,new Num(0.0)) );
	assert(exp1.eval() == 9.0);
	assert(exp2.eval() == -5.0);
	assert(exp3.eval() == -10.0);
	assert(exp4.eval() == 5.0);
	assert(exp5.eval() == -15.0);
	assert(exp6.eval() == 2.0);
	// // a test for Problem 1b
	
	List<Instr> is = new LinkedList<Instr>();

	is.add(new Push(1.0));
	is.add(new Push(2.0));
	is.add(new Calculate(Op.ADD));
	is.add(new Push(3.0));
	is.add(new Calculate(Op.MULT));
	Instrs instrs = new Instrs(is);
	assert(instrs.execute() == 9.0);

	List<Instr> is2 = new LinkedList<Instr>();
	
	is2.add(new Push(-1.0));
	is2.add(new Push(2.0));
	is2.add(new Calculate(Op.MULT));
	is2.add(new Push(5.0));
	is2.add(new Calculate(Op.MULT));
	Instrs instrs2 = new Instrs(is2);
	assert(instrs2.execute() == -10.0);

	List<Instr> is3 = new LinkedList<Instr>();
	
	is3.add(new Push(5.0));
	is3.add(new Push(10.0));
	is3.add(new Push(6.0));
	is3.add(new Push(3.0));
	is3.add(new Calculate(Op.DIV));
	is3.add(new Calculate(Op.MULT));
	is3.add(new Calculate(Op.SUB));

	Instrs instrs3 = new Instrs(is3);
	assert(instrs3.execute() == -15.0);
	
	// // a test for Problem 1c
	assert(exp1.compile().equals(is));
	
	assert(exp3.compile().equals(is2));
	
	
	assert(exp5.compile().equals(is3));
    }
}


// PROBLEM 2

// the type for a set of strings
interface Set {
    int size();
    boolean contains(String s);
    void add(String s);
    void print_set();
}

// an implementation of Set using a linked list
class ListSet implements Set {
    protected SNode head;

    ListSet() {
        this.head = new SEmpty();
    }

    public int size(){
	return head.count();
    }

    public void add(String s){
	SNode new_elem = head.match_and_add(s);
	this.head = new_elem;
    }

    public boolean contains(String s){
	return head.search(s);
    }

    public void print_set(){
	System.out.print(head.print());
    }

}

// a type for the nodes of the linked list
interface SNode {
    SNode match_and_add(String s);
    int count();
    String print();
    boolean search(String s);
}

// represents an empty node (which ends a linked list)
class SEmpty implements SNode {
    SEmpty() {}
    public SNode match_and_add(String s){
	return new SElement(s, new SEmpty());
    }
    public int count(){
	return 0;
    }

    public boolean search(String s){
	return false;
    }
    public String print(){
	return "";
    }

}

// represents a non-empty node
class SElement implements SNode {
    protected String elem;
    protected SNode next;

    SElement(String elem, SNode next) {
        this.elem = elem;
        this.next = next;
    }

    public int count(){
	return 1 + this.next.count();
    }

    public SNode match_and_add(String s){
	if(this.elem.compareTo(s) < 0){
	    SNode temp = this.next;
	    this.next = temp.match_and_add(s);
	    return this;
	}
	else if (this.elem.compareTo(s) > 0){
	    return new SElement(s, this);
	}
	else
	    return this;
    }

    

    public boolean search(String s){
	if(this.elem.compareTo(s) == 0){
	    return true;
	}
	else if (this.elem.compareTo(s) < 0){
	    return this.next.search(s);
	}
	else
	    return false;
    }
    
    public String print(){
	return (this.elem + " " + this.next.print());
    }

}

class SetTest {
    public static void main(String[] args) {
	Set l = new ListSet();

	l.add("Mario");
	l.add("Cuzak");
	l.add("Alan");
	l.add("Destiny");
	l.add("Alan");
	l.add("John");
	l.add("Edith");
	l.add("Frank");
	l.add("Angel");
	l.add("Xavier");
	l.add("John");
	l.add("Edith");
	l.add("Frank");
	l.print_set(); //abcdefx
	System.out.println(l.size());
	System.out.println(l.contains("Herbert"));
	System.out.println(l.contains("John"));
	System.out.println(l.contains("Susan"));
	System.out.println(l.contains("Donald"));
	System.out.println(l.contains("Frank"));
	System.out.println(l.contains("Edith"));
    }
}
