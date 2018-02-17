package NotVlad;

import drive.IDrive;
import drive.Reversable;
import robot.Global;
import robot.IControl;
import robotDefinitions.controls.ReversableController;

public class ReverseDrive extends IControl{
	private Reversable drive;
	private ReversableController controller;
	private boolean reverse;
	
	public ReverseDrive(IDrive drive){
		if(drive instanceof Reversable){
			this.drive = (Reversable)drive;
		}
		if(Global.controllers instanceof ReversableController){
			controller = (ReversableController)Global.controllers;			
		}
	}
	
	public void autonomousInit(){
		if(drive != null && controller != null){
			drive.reverseDrive(false);
			reverse = false;
		}
	}
	
	public void teleopInit(){
		if(drive != null && controller != null){
			drive.reverseDrive(false);
			reverse = false;
		}
	}
	
	public void teleopPeriodic(){
		if(drive != null && controller != null){
			if(controller.reverseDrive()){
				reverse = !reverse;
			}
			drive.reverseDrive(reverse);
		}
	}
}
