package packages.com.ms.commodities.edisondev.sarups.util;


public abstract class ThrStage implements Stage {
	private final int _numThreads;
	private final WorkerManager _workerMgr;
	
	private class ThrWorkerManager<T, U> implements Runnable {
		final WorkerManager _mgr;
		final Source<T> _src;
		final Sink<U> _sink;
		
		ThrWorkerManager(WorkerManager mgr_, Source<T> src_, Sink<U> sink_) {
			_mgr = mgr_;
			_src = src_;
			_sink = sink_;
		}
		
		public void run() {
			_mgr.manage(_src, _sink);
		}
	}
	
	
	public ThrStage(int numThreads_) {
		_numThreads = numThreads_;
		_workerMgr = new WorkerManager(_numThreads, this);
	}
	
	@Override
	public<T, U> void startWork(Source<T> src_, Sink<U>sink_) {
		final Runnable runner = new ThrWorkerManager<T, U>(_workerMgr, src_, sink_);
		new Thread(runner).start();
	}
	
	@Override
	public void awaitCompletion() {
		_workerMgr.awaitCompletion();
	}
}
