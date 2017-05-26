/* Name: Earle Aguilar
   UID: 804501476
   Others With Whom I Discussed Things:
   Other Resources I Consulted:
   
*/

// import lists and other data structures from the Java standard library
import java.util.*;


// PROBLEM 2

// the type for a set of strings
interface Set {
//     int size();
//     boolean contains(String s);
       void add(String s);
}

// an implementation of Set using a linked list
class ListSet implements Set {
    protected SNode head;

    ListSet() {
        this.head = new SEmpty();
    }


    public void add(String s){
        
    }  
}

// a type for the nodes of the linked list
interface SNode {
    SNode query(SNode n, boolean isBigger, String s);
}

// represents an empty node (which ends a linked list)
class SEmpty implements SNode {
    SEmpty() {}

}

// represents a non-empty node
class SElement implements SNode {
    protected String elem;
    protected SNode next;

    SElement(String elem, SNode next) {
        this.elem = elem;
        this.next = next;
    }
}