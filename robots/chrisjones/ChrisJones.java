package chrisjones;
import robocode.Bullet;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;


public class ChrisJones extends Robot {

    //constants for tuning
    int maxShootingDistance = 600;
    int minRange = 100;

    //variables to keep track of opponent
    boolean foundOpp = false;
    int oppLastSeenX = 0;
    int oppLastSeenY = 0;
    double oppLastSeenBearing = 0;
    double oppLastSeenDistance = 0;
    double myHeadingLastTimeOppSeen = 0;
    long oppLastSeenTurn = 0;


    public void run(){        

        while(true){
            if(foundOpp){
                
                //set tank's heading to be 30 degrees to the right of target
                offsetTankFromOpp(30);

                //point gun exactly at target
                pointGunAndRadar();

                if(oppLastSeenDistance > maxShootingDistance){
                    System.out.println(getTime() + "--Driving toward opp ");
                    ahead(oppLastSeenDistance / 3);
                }else{
                    double firePower = Rules.MAX_BULLET_POWER - (Rules.MAX_BULLET_POWER / (oppLastSeenDistance - minRange));
                    System.out.println(getTime() + "--Shooting! distance: " + oppLastSeenDistance + " power: " + firePower);
                    // Fire a bullet with maximum power if the gun is ready
                    if (getGunHeat() == 0) {
                        fire(firePower);
                    }
                    else if(oppLastSeenDistance > minRange){
                        ahead(Math.max(10, (oppLastSeenDistance - minRange)/2));
                    }
                    else{
                        //I can't do anything else here - gun is too hot and I'm already too close. So just go back a bit and evade
                        back(50);
                    }
                } 
                System.out.println(getTime() + "Scanning ");
                turnRadarLeft(45);
                turnRadarRight(45);
                if(oppLastSeenTurn < (getTime() - 5)){
                    System.out.println(getTime() + "Lost the enemy!");
                    foundOpp = false;
                }
            }
            else{
                System.out.println(getTime() + "Searching for enemy - 360 spin");
                //the radar spins way faster than the robot (radar: 45 degress/turn, gun:20, tank: 10)
                turnRadarRight(360);
            }
        }
    }

    public void offsetTankFromOpp(int degrees){
        if(Math.abs(oppLastSeenBearing + degrees) > 10){
            System.out.println(getTime() + "--turning " + oppLastSeenBearing + degrees);
            turnRight(oppLastSeenBearing + degrees);
        }
    }

    public void pointGunAndRadar(){
        double headingThatWouldPointToOpp = myHeadingLastTimeOppSeen + oppLastSeenBearing;


        //point gun toward opponent
        if(Math.abs(getGunHeading() - headingThatWouldPointToOpp) > 5){
            double degreesToTurn = getGunHeading() - headingThatWouldPointToOpp;
            System.out.println(getTime() + "--Lining up gun with tank. DegreesToTurn before correction: " + degreesToTurn);
            
            if (degreesToTurn < -180){
                System.out.println("Correcting!");
                degreesToTurn = 360 + degreesToTurn;
            }else if(degreesToTurn > 180){
                System.out.println("Correcting!");
                degreesToTurn = degreesToTurn - 360;
            }

            System.out.println(getTime() + "--Lining up gun with tank. Turn gun left by " + degreesToTurn);
            
            turnGunLeft(degreesToTurn);
        }
        //line up radar 22 degress to right of tank
        //TODO: make this more efficient. Right now i'm wasting ~1 turn by lining up and then going to the right
        if(Math.abs(getRadarHeading() - headingThatWouldPointToOpp) > 5){
            double degreesToTurn = getRadarHeading() - headingThatWouldPointToOpp;
            System.out.println(getTime() + "--Lining up radar with tank. DegreesToTurn before correction: " + degreesToTurn);
            if (degreesToTurn < -180){
                System.out.println("Correcting!");
                degreesToTurn = 360 + degreesToTurn;
            }else if(degreesToTurn > 180){
                System.out.println("Correcting!");
                degreesToTurn = degreesToTurn - 360;
            }
            System.out.println(getTime() + "--Lining up radar with tank. Turn radar left by " + degreesToTurn);
            
            turnRadarLeft(degreesToTurn-22);
            //turnRadarRight(22);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        if(!foundOpp){
            stop();
        }
        foundOpp = true;
        
        myHeadingLastTimeOppSeen = getHeading();
        oppLastSeenBearing = e.getBearing();
        oppLastSeenDistance = e.getDistance();
        oppLastSeenTurn = e.getTime();
        System.out.println(e.getTime() + "Scanned enemy! heading " + myHeadingLastTimeOppSeen + " bearing " + oppLastSeenBearing + " distance " + oppLastSeenDistance);
        
    }


    public void onRobotDeath(RobotDeathEvent event){
        stop();
        System.out.println("LOL TAKE THAT");
        while(true){
            turnRight(1000);
        }
    }


    //public 
}
