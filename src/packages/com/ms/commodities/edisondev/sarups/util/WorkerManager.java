/**
 * 
 */
package packages.com.ms.commodities.edisondev.sarups.util;

import java.util.HashMap;

class WorkerManager {
	private final static float MULT_FACTOR = 1.5f;
	private final int _numWorkers;
	private final Stage _stage;
	private final HashMap<Integer, ThrStorage> _thrStorage;
	
	
	private class ThrStorage {
		final int _thrId;
		final Thread _thr;
		boolean _isFinished;
		
		ThrStorage(int id_, Thread thr_) {
			_thrId = id_;
			_thr = thr_;
			_isFinished = false;
		}
	}
	
	
	private class ThrWorker<T, U> implements Runnable {
		private final int _id;
		private final WorkerManager _mgr;
		private final Stage _stage;
		private final TaskQueue<T> _inputQ;
		private final Sink<U> _sink;
		
		ThrWorker(int id_, WorkerManager mgr_, Stage stage_, TaskQueue<T> inputQ_, Sink<U> sink_) {
			_id = id_;
			_mgr = mgr_;
			_stage = stage_;
			_inputQ = inputQ_;
			_sink = sink_;
		}
		
		public void run() {
			T item = null;
			while (null != (item = _inputQ.pop())) {
				final Worker worker = _stage.getWorker();
				if (null != worker) worker.process(item, _sink);
			}
			// Add null for remaining threads.
			_inputQ.push(null);
			_mgr.finishThread(_id, _sink);
		}
	}
	
	
	private synchronized <T, U> Thread createThread(TaskQueue<T> queue_, Sink<U> sink_) {
		final int id = _thrStorage.size();
		final Runnable thrWorker = new ThrWorker<T, U>(id, this, _stage, queue_, sink_);
		final Thread thr = new Thread(thrWorker);
		final ThrStorage storage = new ThrStorage(id, thr);
		_thrStorage.put(id, storage);
		return thr;
	}
	
	private synchronized <U> void finishThread(int id_, Sink<U> sink_) {
		final ThrStorage storage = _thrStorage.get(id_);
		storage._isFinished = true;
		for (ThrStorage tmp : _thrStorage.values()) {
			// Return if threads is still working.
			if (!tmp._isFinished) return;
		}
		// Else, this is the last thread.
		sink_.close();
	}

	WorkerManager(int numWorkers_, Stage stage_) {
		_numWorkers = numWorkers_;
		_stage = stage_;
		_thrStorage = new HashMap<Integer, ThrStorage>();
	}
	
	<T, U> void manage(Source<T> src_, Sink<U> sink_) {
		final TaskQueue<T> queue = new TaskQueue<T>(Math.round(_numWorkers * MULT_FACTOR));
		for (int i = 0; i < _numWorkers; ++i) {
			final Thread thr = createThread(queue, sink_);
			thr.start();
		}
		
		T item = null;
		while (null != (item = src_.produce()))
			queue.push(item);
		queue.push(null);
	}
	
	void awaitCompletion() {
		for (ThrStorage storage : _thrStorage.values()) {
			final Thread thr = storage._thr;
			do {
				try {
					thr.join();
					break;
				} catch (InterruptedException e) { }
			}
			while (true);
		}
	}
}