package auto.stopConditions;

import java.util.List;import javax.security.auth.x500.X500Principal;

import auto.IStopCondition;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.Encoder;

public class DistanceStopCondition implements IStopCondition {
	private List<Encoder> enc;
	private int duration;
	
	public DistanceStopCondition(List<Encoder> encoder, int inches) {
		enc = encoder;
		duration = inches;
	}

	public void init() {
		for(Encoder x: enc){
			x.reset();
		}
	}

	public boolean stopNow1() {
		double sum = 0;
		for(Encoder x: enc){
			x.setDistancePerPulse(0.06265);//x.setDistancePerPulse(0.333333333333333333);
			//x.get() returns encoder counts
			//encoder count -> inches will need to be put here
			sum += x.getDistance();
		}
		SmartWriter.putD("Current Distance Per Pulse", enc.get(0).getDistancePerPulse());
		SmartWriter.putD("AUTO - AVG Encoder Count", sum/enc.size());
		return (sum/enc.size()) > duration;
	}
	
	public boolean stopNow(){
		double sum = 0;
		
			enc.get(1).setDistancePerPulse(0.06265);//x.setDistancePerPulse(0.333333333333333333);
			//x.get() returns encoder counts
			//encoder count -> inches will need to be put here
			sum += enc.get(1).getDistance();
		
		SmartWriter.putD("Current Distance Per Pulse", enc.get(1).getDistancePerPulse());
		SmartWriter.putD("AUTO - AVG Encoder Count", sum);
		return (sum) > duration;
	}

}
