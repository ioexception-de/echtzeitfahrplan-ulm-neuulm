package de.ioexception.me.autocomplete;

/**
 * @author Michael Mueller
 */
interface AutoCompletionHandler extends Runnable
{
	/**
	 * Cancels the current request (if possible)
	 */
	void cancel();

	String handle(String request);
}
