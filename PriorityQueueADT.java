package AIRLINE_TICKETING;

public interface PriorityQueueADT <E>{
	void offer(E element);   // add an element
    E poll();                // remove & return highest-priority element
    E peek();                // look at highest-priority without removing
    int size();              // number of elements
    boolean isEmpty();       // true if empty
}
