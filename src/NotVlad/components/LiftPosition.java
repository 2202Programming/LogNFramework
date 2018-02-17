package NotVlad.components;

public enum LiftPosition {
	BOTTOM(0),SWITCH(10000),SCALE(25000),CLIMB(12800),MAX(30000);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
