package template;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Template extends Robot{

    public void run(){
        //optional: add any setup code that you want to run here
        //eg, change your robot's color, figure out where your robot spawns
        
        while(true){
        
            //This is the code that will run continuously until the end of the battle
            System.out.println("This is a print statement");
            ahead(1);
            turnRight(1);
        }
    }

}
