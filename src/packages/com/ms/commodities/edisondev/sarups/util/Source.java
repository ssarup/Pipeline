package packages.com.ms.commodities.edisondev.sarups.util;

public interface Source<T> {
	/**
	 * Produce an item 'T' for further processing.
	 * A return value of 'null' indicates there is nothing more to produce.
	 * @return : item to be processed.
	 */
	public T produce();
}
