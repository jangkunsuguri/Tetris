public class Queue <E> {
    private int count;
    private int rear;
    private int front;
    private int size;
    private E[] data;

    public Queue(){
        this.front = 0;
        this.rear = 0;
        this.count = 0;
        this.size = 5;
        this.data = (E[]) new Object[size];
    }

    public Queue(int size){
        this.front = 0;
        this.rear = 0;
        this.count = 0;
        this.size = size;
        this.data = (E[]) new Object[size];
    }

    public boolean isFull(){
        return this.count == this.size;
    }

    public boolean isEmpty(){
        return this.count == 0;
    }

    public E front(){
        if (!isEmpty()){
            return this.data[front];
        }
        return null;
    }

    public E dequeue(){
        if (!isEmpty()){
            E element = data[front];
            front = (front+1)%size;
            count--;
            return element;
        }
        return null;
    }

    public boolean enqueue(E element){
        if (!isFull()){
            data[rear] = element;
            rear = (rear+1)%size;
            count++;
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        int i = this.front;
        StringBuilder result = new StringBuilder();
        while (i != rear){
            result.append(data[i].toString()).append(" ");
            i = (i+1)%size;
        }
        result.append("\n");
        return result.toString();
    }

    public void display(){
        System.out.print(this.toString());
    }
}
