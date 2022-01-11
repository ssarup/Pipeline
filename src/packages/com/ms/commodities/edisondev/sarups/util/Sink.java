package packages.com.ms.commodities.edisondev.sarups.util;

public interface Sink<T> {
	/**
	 * Consume the passed 'item_'.  'item_' can be null.
	 * This is undefined once the object is 'close'd.
	 * @param item_ : to be consumed.
	 */
	public void consume(T item_);
	
	/**
	 * Sink will not consume any more items.
	 * This is used to indicate an end of stream.
	 */
	public void close();
}
