package NotVlad.components;

public enum LiftPosition {
	BOTTOM(0),SWITCH(10000),SCALE(20000),CLIMB(100000);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
