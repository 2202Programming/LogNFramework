package NotVlad.components;

public enum LiftPosition {
	BOTTOM(0),SWITCH(1),SCALE(2),CLIMB(3);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
