package packages.com.ms.commodities.edisondev.sarups.util;

public interface Stage {
	public Worker getWorker();
	
	public <T, U> void startWork(Source<T> src_, Sink<U> sink_);
	
	public void awaitCompletion();
}
