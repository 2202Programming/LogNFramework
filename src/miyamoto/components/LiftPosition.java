package miyamoto.components;

public enum LiftPosition {
	BOTTOM(0),SWITCH(10000),LOWSCALE(20000),HIGHSCALE(27000),CLIMB(16000),MAX(28000);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
