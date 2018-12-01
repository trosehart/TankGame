public class Shot {
   private int shotStartX;
   private double shotM, shotB, shotX, shotY, shotDirection;
   private double distance;
   private boolean isShot = false;
   //constructors
   public Shot() {
      shotStartX = 0;
      shotX = 0;
      shotY = 0;
      shotDirection = 0;
      shotM = 0;
      shotB = 0;
      isShot = false;
   }
   //calculates slope, starting position, etc
   public Shot(EnemyTank e) {
      shotStartX = e.getTankX();
      shotX = shotStartX;
      shotY = e.getTankY();
      shotM = (e.getGunX()+0.0)/(e.getGunY());
      shotB = e.getTankY()-shotM*e.getTankX();
      isShot = true;
      
      if(e.getGunX()>=0) {
         shotM = (e.getGunY()+0.0)/(e.getGunY());
         shotB = e.getTankY()-shotM*e.getTankX();
         shotDirection = 5;
      }
      else if(e.getGunX()<0){
         shotM = (-e.getGunY()+0.0)/(-e.getGunX());
         shotB = e.getTankY()-shotM*e.getTankX();
         shotDirection = -5;
      }
      optimizeShot();
      if(e.getType().equals("bunker")) {
         shotDirection = shotDirection*4;
      }
   }
   //calculates slope, starting position, etc
   public Shot(Tank t) {
      setStartX(t.getTankX());
      setShotX(t.getTankX());
      setShotY(t.getTankY());
      isShot = true;
      
      if(t.getGunX()>=0) {
         shotDirection = 5;
      }
      else if(t.getGunX()<0) {
         shotDirection = -5;
      }
      shotM = -(t.getGunY()+0.0)/(t.getGunX());
      optimizeShot();
      shotB = t.getTankY()-shotM*t.getTankX();
      isShot = true;
   
   }
   //gets and sets
   public void setStartX(int shotStartX) {
      this.shotStartX = shotStartX;
   }
   public void setShotX(double shotX) {
      this.shotX = shotX;
   }
   public void setShotY(double shotY) {
      this.shotY = shotY;
   }
   public void setIsShot(boolean isShot) {
      this.isShot = isShot;
   }
   public void setM(double shotM) {
      this.shotM = shotM;
   }
   public void setB(double shotB) {
      this.shotB = shotB;
   }
   public int getStartX() {
      return shotStartX;
   }
   public double getShotX() {
      return shotX;
   }
   public double getShotY() {
      return shotY;
   }
   public double getShotDirection() {
      return shotDirection;
   }
   public boolean isShot() {
      return isShot;
   }
   public double getM() {
      return shotM;
   }
   public double getB() {
      return shotB;
   }
   //stopping m = infinity issue when x of tank = x of gun, evens out speed that bullet moves at
   public void optimizeShot() {
      if(Double.isInfinite(Math.abs(shotM)) || Double.isNaN(shotM)) {
         shotM = 64*Math.signum(shotM); 
      }
      if(shotM < 1 && shotM > -1) {
         shotDirection = shotDirection/2;
      }
      else {
         shotDirection = shotDirection/Math.abs(shotM);
      }
   }
   //for the computer to decide if it wants to shoot
   public boolean checkShot(Tank t, EnemyTank e) {
      distance = Math.abs((shotM*t.getTankX()-t.getTankY()+shotB))/(Math.sqrt(Math.pow(shotM,2)+1));
      if(distance < 20 && ((e.getGunX() < 0 && t.getTankX() < e.getTankX()) || (e.getGunX() > 0 && t.getTankX() > e.getTankX()))) {
         return true;
      }
      else
         return false;
   }
   //checking if computer shot hit player
   public boolean checkHit(Tank t) {
      distance = (int)(Math.sqrt(Math.pow(shotX-t.getTankX(), 2)+Math.pow(shotY-t.getTankY(), 2)));
      if(distance < 20) {
         isShot = false;
         return true;
      }
      else {
         return false;
      }
   }
   //checking if player shot hit enemy
   public boolean checkHit(EnemyTank e) {
      distance = (int)(Math.sqrt(Math.pow(shotX-e.getTankX(), 2)+Math.pow(shotY-e.getTankY(), 2)));
      if(distance < 20) {
         isShot = false;
         return true;
      }
      else {
         return false;
      }
   }
   //finding position of shot
   public void calcPoint() {
      shotX = shotX+shotDirection;
      shotY = shotM*(shotX)+shotB;
   }
}

/*Exam stuff
-on paper :'(

-relatively short coding
   3 full things:
   -define class (1 instance variable)
      -get, set, private variable
      -something to do with grade 9 or 10 science, solving algebra
   -making a subclass
      -not on question 1
      -given entire class
   -making a GUI application
      -very simple, like 1st Chortle chapter almost
      -simple calculation
         -such as area of rectangle
      -user enters 2 numbers, prints out answer
-Unit 4 stuff
   -search algorithms
      -explain diff in select and binary
      -show steps
   
   -sorting algorithms
      -don't need to know code, just how it works
      -know basic ones, get to choose which you explain
   
   -recursion   
      -similar to practise questions
      -names ---> base case, reduction step
   
*/