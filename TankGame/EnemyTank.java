public class EnemyTank extends Tank {
   private int upCount = 0, downCount = 0, leftCount = 0, rightCount = 0;
   private double xDiff, yDiff;
   private boolean isAlive;
   private String tankType;  //basic = moves randomly, gun angle changes +1 degree
   							 //bunker = non moving, follows you, shoots at double speed
	//constructors
   public EnemyTank() {
      super(700,100,0,0);
      tankType = "basic";
      isAlive = false;
   }
   public EnemyTank(int enemyX, int enemyY, String tankType) {
      super(enemyX, enemyY, 0, 0);
      this.tankType = tankType;
      isAlive = true;
   }
   public int getGunX() {
      setGunX((int)(GUN_LENGTH*Math.cos(Math.toRadians(getAngle()))));
      return gunX;
   }
   public int getGunY() {
      setGunY((int)(GUN_LENGTH*Math.sin(Math.toRadians(getAngle()))));
      return gunY;
   }
   //checks if EnemyTank has been destroyed
   public boolean getAlive() {
      return isAlive;
   }
   public String getType() {
      return tankType;
   }
   public void setAlive(boolean isAlive) {
      this.isAlive = isAlive;
   }
   public void setType(String tankType) {
      this.tankType = tankType;
   }
   //moves EnemyTank semi-randomly (trying to get to centre of screen)
   public void moveTank(String[][] wall) {
      int ranNum = (int)(Math.random()*20);
      if(upCount < 20) {
         try {
            for(int i = getTankX()-21; i<getTankX()+21; i++) {
               for(int n = getTankY()-24; n < getTankY()-20; n++) {
                  if(wall[i][n].equals("wall")){
                     upCount = 20;
                  }
               }
            }
         }
         catch(Exception exc) {
            upCount = 20;
         }
         if(upCount < 20) {
            setTankY(getTankY()-3);
            upCount++;
         }
      }
      else if(downCount < 20) {
         try {
            for(int i = getTankX()-21; i<getTankX()+21; i++) {
               for(int n = getTankY()+20; n < getTankY()+24; n++) {
                  if(wall[i][n].equals("wall")){
                     downCount = 20;
                  }
               }
            }
         }
         catch(Exception exc) {
            downCount = 20;
         }
         if(downCount < 20) {
            setTankY(getTankY()+3);
            downCount++;
         }
      }
      else if(rightCount < 20) {
         try {
            for(int i = getTankX()+20; i<getTankX()+24; i++) {
               for(int n = getTankY()-21; n < getTankY()+21; n++) {
                  if(wall[i][n].equals("wall")){
                     rightCount = 20;
                  }
               }
            }
         }
         catch(Exception exc){
            rightCount = 20;
         }
         if(rightCount < 20) {
            setTankX(getTankX()+3);
            rightCount++;
         }
      }
      else if(leftCount < 20) {
         try {
            for(int i = getTankX()-24; i<getTankX()-20; i++) {
               for(int n = getTankY()-21; n < getTankY()+21; n++) {
                  if(wall[i][n].equals("wall")){
                     leftCount = 20;
                  }
               }
            }
         }
         catch(Exception exc){
            leftCount = 20;
         }
         if(leftCount < 20) {
            setTankX(getTankX()-3);
            leftCount++;
         }
      }		
      else if(ranNum == 0) {
         ranNum = (int)(Math.random()*20);
         if(getTankX() >= 400) {
            if(ranNum == 0) {
               upCount = 0;
            }
            else if(ranNum == 1) {
               downCount = 0;
            }
            else if(ranNum == 2) {
               rightCount = 0;
            }
            else {
               leftCount = 0;
            }
         }
         else if(getTankX() <= 400) {
            if(ranNum == 0) {
               upCount = 0;
            }
            else if(ranNum == 1) {
               downCount = 0;
            }
            else if(ranNum == 2) {
               leftCount = 0;
            }
            else {
               rightCount = 0;
            }
         }
      }
      else if(ranNum == 1) {
         ranNum = (int)(Math.random()*20);
         if(getTankY() <= 400) {
            if(ranNum == 0) {
               rightCount = 0;
            }
            else if(ranNum == 1) {
               leftCount = 0;
            }
            else if(ranNum == 2) {
               upCount = 0;
            }
            else {
               downCount = 0;
            }
         }
         else if(getTankY() >= 400) {
            if(ranNum == 0) {
               rightCount = 0;
            }
            else if(ranNum == 1) {
               leftCount = 0;
            }
            else if(ranNum == 2) {
               downCount = 0;
            }
            else {
               upCount = 0;
            }
         }
      }
   }
   //moving gun to follow player
   public void moveGun(Tank t){
      xDiff = t.getTankX()-getTankX()+0.0;
      yDiff = t.getTankY()-getTankY();
      if(yDiff > 0) {
         setAngle(Math.toDegrees(Math.atan(xDiff/-yDiff))%360+90);
      }
      else if(yDiff < 0) {
         setAngle(Math.toDegrees(Math.atan(xDiff/-yDiff))%360-90);
      }
   }
}
