

import java.utils.concurrent.*;

class SumTask extends RecursiveTask<Long>{
	/*Inherits two operations:
		void fork(){
			(conceptually) Starts a new thread and
			execute this.compute() on new thread. 
		}

		Long join(){
			wait for this.compute() to finish 
			return its result.
		}		
	*/

	private int[] elems;
	private int low, high;
	private static final int SEQUENTIAL_CUTOFF = 10000;
	SumTask(int[] arr, int low, int high){
		this.low = low;
		this.high = high;
		this.elems = arr;
	}

	protected Long compute(){
		if((high - low) <= SEQUENTIAL_CUTOFF){
			//sum sequentially
			long res = 0;
			for (int i = low; i < high; i++){
				res += elems[i];
			return res;
			}
		} 

		int mid = (high + low) / 2;
		SumTask left = new SumTask(elems, low, mid);
		SumTask right = new SumTask(elems, mid, high);
		left.fork();
		right.fork();
		long lSum = left.join();
		long rSum = right.join();
		return lSum + rSum;
		/*
		left.fork(); // This medthod makes sure we are not wasting threads.
		long lSum = left.compute();
		long rSum = right.join();
		*/

	}

}


class Sum{

	public static void main(Strign[] args){
		int size = Integer.parseInt(args[0]);
		int[] a = new int[size];
		for (int i = 0; i < size; i++){
			a[i] = i;
		}

		// Sum the elements in parallel
		long l = new SumTask(a, 0,size(a)).compute();
		System,
	}
}


interface List {
	void add(String s);
	String get(int i) throws BadIndexException;
}

class BadIndexException extends Exception {}

class ListImpl implements List{
	private String[] arr = new String[10];
	private int size = 0;

	public void add(String s){
		// really eed to check if you're out of space
		// and resize the array ingoring that
		arr[size] = s;
		size++;
	}


	public String get(int i) throws BadIndexException{
		if (i >= 0 && i < size){
			return arr[i];
		}
		else 
			throw new BadIndexException();
	}
}

class Client{

	public static void main(String[] args){
		list l = new ListImpl();
		l.add("hi");
		try{
			System.out.println(l.get(3));
		} catch(BadIndexException e){
			System.out.println("Bad Index");
		}
	}
}


// Exceptin Safety
/* Problem How do you make sure to leave an object's state in 
   a "safe" configuration regarless of what exceptions occur
   and when?
*/

class XException extends Exception{}
class YException extends Exception{}

class ExnSafety{
	private int x , y;

	void updateX() throws XException{
		// ...
	}

	void updateY() throws YException{
		// ...
	}

	// requirement: either both x and y are updated
	// or neither is updated
	void updateBoth() throws XException, YException{
		int oldx = x;
		int oldy = y;
		try{
			updateX();
			updateY();
		} catch (XException e){
			x = oldx;
			throw e;
		} catch (YException e){
			x = oldx;
			y = oldy;
			throw e;
		}
	}
}   


class ComputeException extends Exception{}

class Example2 {

	void compute() throws ComputeException{
		// ...
	}

	void readFileAndCompute(File f) throws ComputeException{
		String s = f.read();
		try{
		this.compute(s);
		f.close();
		} catch (ComputeException e){
			// clean up the state
		} finally{
			f.close();
		}	
	}
}