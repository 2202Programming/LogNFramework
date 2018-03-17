package miyamoto.components;

public enum LiftPosition {
	BOTTOM(0),EXCHANGE(1500),SECONDBLOCK(4800),PORTAL(7300),SWITCH(10000),LOWSCALE(20000),MIDSCALE(24000),HIGHSCALE(27000),CLIMB(17100),MAX(28000);
	
	private final int number;
	private LiftPosition(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
