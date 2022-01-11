package packages.com.ms.commodities.edisondev.sarups.util;

import java.util.ArrayList;

public class PipelineMgr {
	private final ArrayList<Stage> _stages;
	
	public PipelineMgr() {
		_stages = new ArrayList<Stage>();
	}
	
	public void addStage(Stage stage_) {
		_stages.add(stage_);
	}
	
	public void manage() {
		for (Stage stage : _stages) {
			stage.startWork(null, null);
		}
	}
}
