import java.util.*;

class Primitives{

	public static void main(String[] args){
		List<Integer> l = new LinkedList<Integer>();
		l.add(new Integer(3)); // Explicit
		l.add(4); // Implicitely autoboxed

		int fst = l.get(0).intValue(); // Explicit
		int snd = l.get(1); //Implicit auto-unboxed 

		System.out.println(3 = l.get(0).intValue());
		System.out.println(3 = l.get(0));
		System.out.println(
			(new Integer(3)) == l.get(0));
		System.out.println(
			(new Integer(3)).equals(l.get(0)) );
	}
}

/* The declaration above will signal an error.
   Switch <int> to <Interger>
   1. Integer is like a class:
     a. class integer{
		private int i;
		... // getter and setter	
     }
 */

class Node<E> {
	E elem; // Compiler has no problem since we just need 64-bits for a pointer
	Node <E> next;
}


/* First-class functions in Java. */

/*1st Version: Classes as first class functions.*/

class BackwardsCompare implements Comparator<String>{
	public int compare(String s1, String s2){
		return s1.compareTo(s2);
	}
}

class Sort {

	public static void main(String[] args){
		List<String> l = Array.as.List(args);
		Collections.sort(l, new BackwardsCompare());
		for  (String s : l)
			Systemout.println(s); 

	} 
}

/* 2nd Version: Anonymous class as first-class functions*/

class Sort2 {

	public static void main(String[] args){
		List<String> l = Array.as.List(args);
		Collections.sort(l, 
			new Comparator<String>(){
				public int compare(String s1, String s2){
					return s2.compareTo(s1);
				}
			});
		for  (String s : l)
			Systemout.println(s); 

	} 
}

/* 3nd Version: Lambdas*/

class Sort3 {

	public static void main(String[] args){
		List<String> l = Array.as.List(args);
		Collections.sort(l, (s1, s2) -> s2.compareTo(s1));
		for (String s : l)
			Systemout.println(s); 
	} 
}

// stream example:   --Stream-->|Filter| --Stream--> |Map| --Stream--> |Sum| --Stream-->
// Get parallalism accros the pipeline and parallalism for each individual Module.

// Sum the elements of an array

class SumStream{
	public static void main(String[] args){
		int size = Integer.parseInt(args[0]);
		int[] a = new int[size];

		for(int i = 0; i < size; i++){
			a[i] = i;
		}
		int sum = Arrays.stream(a)
		    . parallel() // This is too simple so 
		    // the overhead will reduce speedup
		    . reduce(
			     0, 
			     (i1, i2) -> i1+i2
			     )
		    System.out.println(sum);				
	}
}


class SortedStream{

	public static void main(String[] args){
		int size = Integer.parseInt(args[0]);
		double[] a = new double[size];
		for (int i = 0; i < size ; i++){
			a[i] = Math.random() * size;
		}
		double[] out = Arrays.stream(a). parallel(). sorted().toArray;
		
		for (int i = 0; i < 10; i ++){
			system.out.println(out[i]);
		}

	}
}

