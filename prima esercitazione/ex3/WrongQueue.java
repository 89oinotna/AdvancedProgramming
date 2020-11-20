import java.util.*;


/**
   An example of bad programming
 **/
public class WrongQueue {
    class Node{
		String data;
		Node next;
    }
    
    private Node head;
    private Node current;
    private Node last;

    
    public void enqueue(String str){
		Node nnode = new Node();
		nnode.data = str;
		if(head == null){
			current = last = head = nnode;
		}else{

			if(current != null){
				last = current;
				last.next = nnode;
			}
			current = nnode;
			if(head==null){
				head=current;
			}


		}
    }

    public String dequeue(){
		String result = null;

		if(current != null){
			result = current.data;
			if(current==head){
				head=last;
			}
			if(last!=null){
				last.next=current.next;
			}
			current = current.next;
		}

		return result;
	    
    }

    
    
    public static void main(String[] args){
		Random rnd = new Random();
		WrongQueue queue = new WrongQueue();
		final int SIZE = 1024*1024;

		for(int i=0; i < 5000000; ++i){
			if(i % 2 == 0){
				char[] chars = new char[SIZE];
				Arrays.fill(chars, 'f');
				queue.enqueue("String n. " + rnd.nextInt(101) + new String(chars));
			}
			else
				System.out.printf("Dequeue \"%s\"\n", queue.dequeue().substring(0,15));
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){}
		}
    }
}
