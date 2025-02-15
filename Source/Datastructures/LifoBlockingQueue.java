package Source.Datastructures;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class LifoBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    private final BlockingDeque<E> deque;

    public LifoBlockingQueue() {
        this.deque = new LinkedBlockingDeque<>();
    }

    @Override
    public boolean offer(E e) {
        return deque.offerFirst(e);
    }

    @Override
    public E poll() {
        return deque.pollFirst();
    }

    @Override
    public E peek() {
        return deque.peekFirst();
    }

    @Override
    public Iterator<E> iterator() {
        return deque.iterator();
    }

    @Override
    public int size() {
        return deque.size();
    }

    // BlockingQueue methods
    @Override
    public void put(E e) throws InterruptedException {
        deque.putFirst(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return deque.offerFirst(e, timeout, unit);
    }

    @Override
    public E take() throws InterruptedException {
        return deque.takeFirst();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.pollFirst(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return deque.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return deque.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return deque.contains(o);
    }

    @Override
    public int drainTo(java.util.Collection<? super E> c) {
        return deque.drainTo(c);
    }

    @Override
    public int drainTo(java.util.Collection<? super E> c, int maxElements) {
        return deque.drainTo(c, maxElements);
    }

    // Custom priorityBlockingQueue method to demonstrate usage
    public static <E> LifoBlockingQueue<E> priorityBlockingQueue() {
        return new LifoBlockingQueue<>();
    }
}
