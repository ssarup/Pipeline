package packages.com.ms.commodities.edisondev.sarups.util;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue<T> {
	private static final int DEFAULT_CAPACITY = 64;
	private final int _capacity;
	private Queue<T> _queue;
	private boolean _isClosed = false;
	
	public TaskQueue() {
		_capacity = DEFAULT_CAPACITY;
		_queue = new LinkedList<T>();
		_isClosed = false;	
	}

	public TaskQueue(int capacity_) {
		_capacity = capacity_;
		_queue = new LinkedList<T>();
		_isClosed = false;
	}
	
	/**
	 * May block if queue is full.
	 * Will fail if queue is already closed.
	 * Elements are added at the back.
	 * @param elem_
	 * @throws InterruptedException 
	 */
	public synchronized void push(T elem_) {
		do {
			if (_isClosed) {
				notifyAll();
				return;
			}
			try {
				if (_queue.size() == _capacity) wait();
				else break;
			} catch (InterruptedException e) { }
		}
		while (true);
		_queue.offer(elem_);
		notifyAll();
	}
	
	/**
	 * May block if queue is empty.
	 * Will return null if there are no more elements in queue
	 * and it has already been closed.
	 * Elements are removed from the front.
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized T pop() {
		do {
			if (_isClosed && _queue.isEmpty()) return null;
			try {
				if (_queue.isEmpty()) wait();
				else break;
			} catch (InterruptedException e) { }
		}
		while (true);
		T retVal = _queue.remove();
		notifyAll();
		return retVal;
	}
	
	/**
	 * Close the queue, no more 'push' allowed.
	 * 'pop' will return elements until the queue is cleaned out.
	 */
	public synchronized void close() {
		_isClosed = true;
		notifyAll();
	}
	
	/**
	 * Return max capacity.
	 * @return
	 */
	public synchronized int capacity() {
		return _capacity;
	}
	
	/**
	 * Return current size.
	 * @return
	 */
	public synchronized int size() {
		return _queue.size();
	}
	
	/**
	 * Is queue empty.
	 */
	public synchronized boolean isEmpty() {
		return _queue.isEmpty();
	}
	
	/**
	 * Return true if queue is 'close'd and is empty. 
	 * @return
	 */
	public synchronized boolean isClosed() {
		return _isClosed;
	}
}
