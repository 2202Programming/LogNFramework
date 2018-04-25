package miyamoto.components;

public enum LiftPosition {
	BOTTOM(0),EXCHANGE(1500),SECONDBLOCK(4800),PORTAL(7300),SWITCH(10000),LOWSCALE(20000),MIDSCALE(22500),HIGHSCALE(27000),CLIMB(16300),MAX(29000);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
