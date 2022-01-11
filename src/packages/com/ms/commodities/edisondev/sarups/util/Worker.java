package packages.com.ms.commodities.edisondev.sarups.util;

public abstract class Worker {
	/**
	 * Transform the 'input_' and return back the output.
	 * This might return a null if there is nothing to transform.
	 * @param <T> : input
	 * @param <U> : output
	 * @param input_ : Input to be transformed
	 * @return : Transformed output, may be null.
	 */
	public abstract <T, U> U transform(T input_);
	
	/**
	 * Process the 'item_' and write the transformed value to 'sink_', if
	 * possible.
	 * @param <T> : 'item_' type
	 * @param <U> : transformed 'type'
	 * @param item_ : to be worked on
	 * @param sink_ : output may be written here
	 */
	public <T, U> void process(T item_, Sink<U> sink_) {
		U workedItem = transform(item_);
		if (null != workedItem) sink_.consume(workedItem);
	}
}
