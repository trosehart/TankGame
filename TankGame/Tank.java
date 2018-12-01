public class Tank {
   private int tankX, tankY;
   int gunX, gunY;
   static final int GUN_LENGTH = 65;
   private double angle;
	//constructors
   public Tank() {
      tankX = 100;
      tankY = 700;
      gunX = 0;
      gunY = 0;
   }
   public Tank(int tankX, int tankY, int gunX, int gunY) {
      this.tankX = tankX;
      this.tankY = tankY;
      this.gunX = gunX;
      this.gunY = gunY;
   }
   //get and set methods
   public void setTankX(int tankX) {
      this.tankX = tankX;
   }
   public void setTankY(int tankY) {
      this.tankY = tankY;
   }
   public void setGunX(int gunX) {
      this.gunX = gunX;
   }
   public void setGunY(int gunY) {
      this.gunY = gunY;
   }
   public void setAngle(double angle) {
      this.angle = angle%360;
   }
   //sets angle based on mouse position
   public void setAngle(int mouseX, int mouseY) {
      angle = Math.atan2((mouseX-getTankX()),(mouseY-getTankY()))-Math.PI/2;
   }
   
   public int getTankX() {
      return tankX;
   }
   public int getTankY() {
      return tankY;
   }
   //gets gun X and Y position
   public int getGunX() {
      gunX = (int)(GUN_LENGTH*Math.cos(angle));
      return gunX;
   }
   public int getGunY() {
      gunY = (int)(GUN_LENGTH*Math.sin(angle));
      return gunY;
   }
   public double getAngle() {
      return angle;
   }
	//moves user based on WASD
   public void moveTank(String keyID, String[][] wall) {
      boolean okToGo = true;
      if(keyID.equals("w")) {
         try {
            for(int i = getTankX()-21; i<getTankX()+21; i++) {
               for(int n = getTankY()-24; n < getTankY()-20; n++) {
                  if(wall[i][n].equals("wall")){
                     okToGo = false;
                  }
               }
            }
         }
         catch(Exception exc) {
            okToGo = false;
         }
         if(okToGo)
            setTankY(getTankY()-3);
         okToGo = true;
      }
      if(keyID.equals("s")) {
         try {
            for(int i = getTankX()-21; i<getTankX()+21; i++) {
               for(int n = getTankY()+20; n < getTankY()+24; n++) {
                  if(wall[i][n].equals("wall")){
                     okToGo = false;
                  }
               }
            }
         }
         catch(Exception exc) {
            okToGo = false;
         }
         if(okToGo)
            setTankY(getTankY()+3);
         okToGo = true;
      }
      if(keyID.equals("a")) {
         try {
            for(int i = getTankX()-24; i<getTankX()-20; i++) {
               for(int n = getTankY()-21; n < getTankY()+21; n++) {
                  if(wall[i][n].equals("wall")){
                     okToGo = false;
                  }
               }
            }
         }
         catch(Exception exc) {
            okToGo = false;
         }
         if(okToGo)
            setTankX(getTankX()-3);
         okToGo = true;
      }
      if(keyID.equals("d")) {
         try {
            for(int i = getTankX()+20; i<getTankX()+24; i++) {
               for(int n = getTankY()-21; n < getTankY()+21; n++) {
                  if(wall[i][n].equals("wall")){
                     okToGo = false;
                  }
               }
            }
         }
         catch(Exception exc){
            okToGo = false;
         }
         if(okToGo)
            setTankX(getTankX()+3);
         okToGo = true;
      }
   }
}
