import java.util.*;


public class Stack {
    private String[] elements;
    private int size = 0;
    private static final int INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new String[INITIAL_CAPACITY];
    }

    public void push(String e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public String pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    public boolean isEmpty(){
	return size == 0;
    }


    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

}
