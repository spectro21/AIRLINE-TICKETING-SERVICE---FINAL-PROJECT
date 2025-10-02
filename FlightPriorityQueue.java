package AIRLINE_TICKETING;
import java.util.ArrayList;
import java.util.List;


public class FlightPriorityQueue <E extends Comparable<E>> implements PriorityQueueADT <E> {
    private final List<E> heap = new ArrayList<>();

    @Override
    public void offer(E element) {
        heap.add(element);
        siftUp(heap.size() - 1);
    }

    @Override
    public E poll() {
        if (heap.isEmpty()) return null;
        E root = heap.get(0);
        E last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return root;
    }

    @Override
    public E peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // ---- Heap helpers ----
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parent)) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int size = heap.size();
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) smallest = left;
            if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) smallest = right;

            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        E temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}

