/*********************************************************************************************************************
	File: TankGame.java 
	Purpose: Single player game based on Wii Play Tanks.  User controls a Tank using WASD and their mouse and travels,
      through multiple levels, defeating enemies.  There are two different types of enemies.  User can choose their 
      colour and play 2 different game modes - Campaign, completing levels, and Survival, fighting infinite enmies 
      for a high score.
	Author: Thomas Rosehart
	Date: January 25, 2017
	Based on: ICS4U Java Final Assignment
*********************************************************************************************************************/
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class TankGame extends JFrame implements KeyListener, MouseListener, Runnable, ActionListener, ChangeListener {
   //making game screen and graphics
   static TankGame frame;
   GraphicsPanel gp = new GraphicsPanel();
   //for keyboard controls
   JTextField input = new JTextField(10);
   //buttons to choose game mode
   JButton campaignModeButton = new JButton("Campaign");
   JButton survivalModeButton = new JButton("Survival");
   static boolean campaign;
   //sliders for choosing your colour
   JSlider rSlider, bSlider, gSlider;
   JLabel rLabel = new JLabel("r");
   JLabel gLabel = new JLabel("g");
   JLabel bLabel = new JLabel("b");
   //panel for sliders, panel to show colour
   JPanel sliderPanel = new JPanel();
   JPanel colourPanel = new JPanel();
   //different fonts for pause screen and game screen
   static Font f1 = new Font("Serif",Font.BOLD, 12);
   static Font f2 = new Font("Serif",Font.BOLD, 50);
   //booleans to pause/reset game
   static boolean gameOn = false;
   static boolean cont = true;
   static boolean gameStarted = false, pause = false;
   //your tank and colour
   static Tank p1 = new Tank(100,700, 0, 0);
   static Color pColour = new Color(0,0,255);
   //array of enemies that is given a number based on file
   static EnemyTank[] enemy;
   //information for enemies
   static int numEnemies, numDestroyed;
   static String tankType;
	//stuff for mouse position and gun
   static int mouseX = 0, mouseY = 0;
   static Point mouseP;
   static double angle;
	//stuff for shot
   static Shot[] pShot = new Shot[5];
	//stuff for enemies' shots
   static Shot eShot[];
   //level taht player is on and their number of lives
   static int level = 0, lives = 5;
	//stuff for moving computer player
   static int downCount = 20, upCount = 20, leftCount = 20, rightCount = 20;
	//stuff for walls
   static String wall[][] = new String [800][800];
   static boolean okToGo = true;
   static File file = new File("C:/Users/trose/eclipse-workspace/TankGame/files/TankGame Levels.txt");
   static Scanner scan;
   static int numWalls, firstX, firstY, secondX, secondY;
   //survival mode stuff
   static int score = 100;
   //sounds
   static MediaPlayer shotSound = new MediaPlayer("C:/Users/trose/eclipse-workspace/TankGame/files/Shot.wav",false);
   static MediaPlayer explodeSound = new MediaPlayer("C:/Users/trose/eclipse-workspace/TankGame/files/Explosion.wav",false);
   static MediaPlayer backgroundSound = new MediaPlayer("C:/Users/trose/eclipse-workspace/TankGame/files/BGMusic.wav",true);
   static String directory;

   TankGame(String name) throws IOException{
      super(name);
      //making new scanner and file for when user dies and restarts
      file = new File("C:/Users/trose/eclipse-workspace/TankGame/files/TankGame Levels.txt");
      scan = new Scanner(file);
      getContentPane().removeAll();
      dispose();
      setVisible(true);
      setResizable(false);
      setBounds(100,50,800,870);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //different stuff based on if they have started playing yet
      //start menu
      if(!gameStarted && level == 0) {
         setLayout(new FlowLayout());
         campaignModeButton.addActionListener(this);
         survivalModeButton.addActionListener(this);
         //colour sliders
         rSlider = new JSlider(JSlider.HORIZONTAL,0,255,0);
         rSlider.setMajorTickSpacing(85);
         rSlider.setMinorTickSpacing(15);
         rSlider.setPaintTicks(true);
         rSlider.setPaintLabels(true);
         rSlider.setSnapToTicks(true);
         rSlider.addChangeListener(this);
         
         gSlider = new JSlider(JSlider.HORIZONTAL,0,255,0);
         gSlider.setMajorTickSpacing(85);
         gSlider.setMinorTickSpacing(15);
         gSlider.setPaintTicks(true);
         gSlider.setPaintLabels(true);
         gSlider.setSnapToTicks(true);
         gSlider.addChangeListener(this);
         
         bSlider = new JSlider(JSlider.HORIZONTAL,0,255,255);
         bSlider.setMajorTickSpacing(85);
         bSlider.setMinorTickSpacing(15);
         bSlider.setPaintTicks(true);
         bSlider.setPaintLabels(true);
         bSlider.setSnapToTicks(true);
         bSlider.addChangeListener(this);
         //setting up start screen
         sliderPanel.setLayout(new FlowLayout());
         sliderPanel.add(rLabel); sliderPanel.add(rSlider);
         sliderPanel.add(gLabel); sliderPanel.add(gSlider);
         sliderPanel.add(bLabel); sliderPanel.add(bSlider);
         sliderPanel.setPreferredSize(new Dimension(200,420));
         colourPanel.setPreferredSize(new Dimension(200,420));
         add(campaignModeButton); add(survivalModeButton);
         add(sliderPanel);
         add(colourPanel);
         pack();
         colourPanel.setBackground(pColour);
      }
      //game screen
      else {
         input.addKeyListener(this);
         gp.addMouseListener(this);
         gp.setPreferredSize(new Dimension(800,800));
         setLayout(new FlowLayout());
         add(input);
         add(gp);
         //pack();
         scan.close();
         file = new File("C:/Users/trose/eclipse-workspace/TankGame/files/TankGame Levels.txt");
         scan = new Scanner(file);
         reset();
         Thread t = new Thread(frame);
         t.start();
      }
   }
   //resets positions of tanks/moves onto next level
   public void reset() throws IOException{
      p1.setTankX(100);
      p1.setTankY(700);
      for(int i = 0; i < numEnemies; i++) {
         eShot[i].setIsShot(false);
      }
      //if the user passed their level it sets up next level
      if(cont && lives > 0 && gameStarted) {
         level++;
         for(int i = 0; i < 800; i++) {
            for(int n = 0; n < 800; n++) {
               wall[i][n] = "empty";
            }
         }
         //reads positions of walls from a file and stores them in an array
         numWalls = scan.nextInt();
         for(int i = 0; i < numWalls; i++) {
            firstX = scan.nextInt();
            firstY = scan.nextInt();
            secondX = scan.nextInt();
            secondY = scan.nextInt();
            for(int j = firstX; j <= secondX; j++) {
               for(int k = firstY; k <= secondY; k++) {
                  for(int l = -2; l < 3; l++) {
                     for(int m = -2; m < 3; m++) {
                        wall[j+l][k+m]= "wall";
                     }
                  }
               }
            }
         }
         //reads number of enemies from file and creates EnemyTank objects based on the file info
         numEnemies = scan.nextInt();
         eShot = new Shot[numEnemies];
         enemy = new EnemyTank[numEnemies];
         for(int i = 0; i < numEnemies; i++) {
            firstX = scan.nextInt();
            firstY = scan.nextInt();
            tankType = scan.next();
            enemy[i] = new EnemyTank(firstX, firstY, tankType);
            eShot[i] = new Shot();
         }
         for(int s = 0; s < 5; s++) {
            pShot[s] = new Shot();
         }
         numDestroyed = 0;
      }
      //starts game
      gameOn = true;
      //draws screen
      repaint();
   }
   public void actionPerformed(ActionEvent evt) {
      //if they hit the button, the game starts
      if(!gameStarted) {
         gameStarted = true; 
      }
      //clears frame to make game screen
      frame.getContentPane().removeAll();
      frame.dispose();
      try {
         frame = new TankGame("Tank Game");
      }
      catch(Exception exc) {
      }
      if(evt.getSource().equals(campaignModeButton)) {
         campaign = true;
      }
      //setting up survival mode information
      else {
         lives = 1;
         numEnemies = 1;
         score = 0;
         numDestroyed = 0;
         campaign = false;
         enemy = new EnemyTank[200];
         eShot = new Shot[200];
         enemy[0] = new EnemyTank(700,100,"basic");
         eShot[0] = new Shot();
         for(int i = 1; i < 200; i++) {
            enemy[i] = new EnemyTank();
            enemy[i].setAlive(false);
            eShot[i] = new Shot();
         }
      }
   }
   //user can change their colour
   public void stateChanged(ChangeEvent evt) {
      pColour = new Color(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
      colourPanel.setBackground(pColour);
   }
   private class GraphicsPanel extends JPanel {
      public void paintComponent(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         super.paintComponent(g);
         if(gameOn && lives > 0 && !pause) {
            //changes thickness of lines for walls
            g2d.setStroke(new BasicStroke(1));
            //draws walls based on array
            g2d.setPaint(Color.gray);
            for(int i = 0; i < 800; i++) {
               for(int n = 0; n < 800; n++) {
                  if(wall[i][n].equals("wall")) {
                     g2d.fillOval(i, n, 5, 5);
                  }
               }
            }
            //changes thickness of lines for tanks and shots
            g2d.setStroke(new BasicStroke(4));
            //draws your tank and your shots
            g2d.setPaint(pColour);
            g2d.fillRect(p1.getTankX()-20, p1.getTankY()-20, 40, 40);
            g2d.drawLine(p1.getTankX(), p1.getTankY(), p1.getTankX()+p1.getGunX(), p1.getTankY()-p1.getGunY());
            int s = 0;
            while(s < 5) {
               if(pShot[s].isShot()) {
                  pShot[s].calcPoint();
                  g2d.fillOval((int)pShot[s].getShotX(), (int)pShot[s].getShotY(), 10, 10);
                  try {
                     if(pShot[s].getShotX() > 800 || pShot[s].getShotX() < 0 || pShot[s].getShotY() > 800 || pShot[s].getShotY() < 0 || !wall[(int)Math.round(pShot[s].getShotX())][(int)Math.round(pShot[s].getShotY())].equals("empty")) {
                        pShot[s].setIsShot(false);
                     }
                  }
                  catch(Exception exc) {
                     pShot[s].setIsShot(false);
                  }
                  //checks if any shots have hit enemies
                  for(int i = 0; i < numEnemies; i++) {
                     if(pShot[s].checkHit(enemy[i]) && enemy[i].getAlive()) {
                        //sound effect if enemy was hit
                        Thread e = new Thread(explodeSound);
                        e.start();
                        //enemy is dead, draws explosion
                        enemy[i].setAlive(false);
                        g2d.setPaint(Color.orange);
                        g2d.fillOval((int)pShot[s].getShotX()-20, (int)pShot[s].getShotY()-20, 40, 40);
                        g2d.setPaint(Color.red);
                        g2d.fillOval((int)pShot[s].getShotX()-10, (int)pShot[s].getShotY()-10, 10, 10);
                          
                        numDestroyed++;
                        score = numDestroyed*100;
                        if(numDestroyed == numEnemies && campaign) {
                           gameOn = false;
                           cont = true;
                        }
                        else if(!campaign) {
                           if(score/500 > numEnemies-1) {
                              numEnemies++;
                           }
                           spawnEnemy();
                        }
                     }
                  }
               }
               s++;
            }
            //draws enemies and their shots
            for(int i = 0; i < numEnemies; i++) {
               if(eShot[i].isShot()) {
                  eShot[i].calcPoint();
                  g2d.setPaint(Color.red);
                  g2d.fillOval((int)(eShot[i].getShotX()), (int)(eShot[i].getShotY()), 10, 10);
                  try {
                     if(eShot[i].getShotX() > 800 || eShot[i].getShotX() < 0 || eShot[i].getShotY() > 800 || eShot[i].getShotY() < 0 || !wall[(int)Math.round((eShot[i].getShotX()))][(int)Math.round((eShot[i].getShotY()))].equals("empty")) {
                        eShot[i].setIsShot(false);
                     }
                  }
                  catch(Exception exc) {
                     eShot[i].setIsShot(false);
                  }
                  //checks if any shots hit anything
                  if(eShot[i].checkHit(p1)) {
                     //sound effect
                     Thread e = new Thread(explodeSound);
                     e.start();
                     //draws explosion
                     g2d.setPaint(Color.orange);
                     g2d.fillOval((int)eShot[i].getShotX()-20, (int)eShot[i].getShotY()-20, 40, 40);
                     g2d.setPaint(Color.red);
                     g2d.fillOval((int)eShot[i].getShotX()-10, (int)eShot[i].getShotY()-10, 10, 10);
                     gameOn = false;
                     cont = false;
                     lives--;
                  }
               }
            }
            //draws enemies in a different colour if they have been destroyed
            for(int i = 0; i < numEnemies; i++) {
               if(enemy[i].getAlive()) {
                  g2d.setPaint(Color.red);
                  g2d.fillRect(enemy[i].getTankX()-20,enemy[i].getTankY()-20,40,40);
                  g2d.drawLine(enemy[i].getTankX(), enemy[i].getTankY(), enemy[i].getTankX()+enemy[i].getGunX(), enemy[i].getTankY()+enemy[i].getGunY());
               }
               else if(!enemy[i].getAlive() && campaign){
                  g2d.setPaint(new Color(128,0,0));
                  g2d.fillRect(enemy[i].getTankX()-20,enemy[i].getTankY()-20,40,40);
                  g2d.drawLine(enemy[i].getTankX(), enemy[i].getTankY(), enemy[i].getTankX()+enemy[i].getGunX(), enemy[i].getTankY()+enemy[i].getGunY());
               }
            }
            //draws level and life information
            g2d.setPaint(Color.black);
            g2d.setFont(f1);
            if(campaign) {
               g2d.drawString("Level: " + level, 20, 20);
               g2d.drawString("Lives: " + lives, 20, 40);
            }
            else {
               g2d.drawString("Destroyed: " + numDestroyed, 20, 20);
               g2d.drawString("Score: " + score, 20, 40);
            }
         }
         //draws pause screen in between levels/deaths
         if((!gameOn || pause) && campaign) {
            try {
               if(!gameOn) {
                  reset();
               }
            }
            catch(Exception exc) {
            }
            pause = true;
            g2d.setFont(f2);
            g2d.drawString("Level: "+level,200,200);
            g2d.drawString("Lives: "+lives,200,300);
            g2d.drawString("Number of enemies: "+(numEnemies-numDestroyed),100,400);
            g2d.setFont(f1);
            if(lives > 0) {
               g2d.drawString("Hit any key to continue",200,600);
            }
            else {
               g2d.drawString("Hit any key to return to main screen",200,600);
            }
            repaint();
         }
         else if((!gameOn || pause) && !campaign) {
            gameStarted = false;
            pause = true;
            g2d.setFont(f2);
            g2d.drawString("Score: "+score,200,200);
            g2d.drawString("Enemies defeated: "+((score-100)/100),200,300);
            g2d.setFont(f1);
            g2d.drawString("Hit any key to return to main screen",200,600);
            repaint();
         }
         else
            repaint();
      }
   }
   //user chooses how to move based on WASD keys
   public void keyPressed(KeyEvent e) {
      String id = input.getText();
      pause = false;
      if(gameOn && lives > 0) {
         p1.moveTank(id, wall);
      }
      //goes back to start screen if player lost
      else {
         try {
            reset();
         }
         catch(Exception exc) {
            endGame();
         }
      }
      input.setText("");
      //goes back to start screen if player lost
      if(lives == 0) {
         endGame();
      }
   }
   //needed for KeyListener
   public void keyReleased(KeyEvent e) {}
   public void keyTyped(KeyEvent e) {}
    
   public void mouseClicked(MouseEvent e) {
      //if player clicks, their tank shoots towards the cursor
      for(int m = 0; m < 5; m++) {
         if(!pShot[m].isShot()) {
            Thread s = new Thread(shotSound);
            s.start();
            pShot[m] = new Shot(p1);
            m = 5;
         }
      }
   }
   //needed for MouseListener
   public void mousePressed(MouseEvent e) {}
   public void mouseReleased(MouseEvent e) {}
   public void mouseEntered(MouseEvent e) {}
   public void mouseExited(MouseEvent e) {}
   //resets game back to start when player loses all lives/beats all levels
   public void endGame() { 
      gameStarted = false;
      level = 0;
      lives = 5;
      frame.getContentPane().removeAll();
      frame.dispose();
      try {
         frame = new TankGame("TankGame");
      }
      catch(Exception ex) {
      }
      try {
         reset();
      }
      catch(Exception exc) {
         
      } 
   }
   //creating new enemies in survival mode
   public void spawnEnemy() {
      for(int i = 0; i < numEnemies; i++) {
         if(!enemy[i].getAlive()) {
            enemy[i] = new EnemyTank((int)(Math.random()*800),(int)(Math.random()*800),"basic");
         }
      }
   }
   public static void main(String[] args) throws IOException{
      frame = new TankGame("TankGame");
      //getting directory for sounds
//      directory = System.getProperty("user.dir");
//      String holder = "";
//      for(int i = 0; i < directory.length(); i++){
//         if(directory.substring(i,i+1).equals("\\"))
//            holder = holder+"/";
//         else
//            holder = holder+directory.charAt(i);
//      }
//      //setting up sounds that should work in any folder
//      shotSound = new MediaPlayer(holder+"/Shot.wav",false);
//      explodeSound = new MediaPlayer(holder+"/Explosion.wav",false);
//      backgroundSound = new MediaPlayer(holder+"/BGMusic.wav",true);
      Thread bg = new Thread(backgroundSound);
      bg.start();
   }
   //run thread to start mouse/enemy threads
   public void run(){
      new Thread(
            new Runnable(){
               public void run(){
                  runMouse();
               }
            }
         ).start();
      new Thread(
            new Runnable(){
               public void run(){
                  runCPU();
               }
            }
         ).start();
   }
   //method for getting mouse position/gun position
   public void runMouse() {
      while(true) {
         while(gameOn) {
            mouseP = MouseInfo.getPointerInfo().getLocation();
         	//subtracting to match position on graphics panel to position on screen
            try {
               mouseX = mouseP.x-frame.getLocationOnScreen().x;
               mouseY = mouseP.y-frame.getLocationOnScreen().y-70;
            }
            catch(Exception exc) {
            }
            p1.setAngle(mouseX,mouseY);
         	//System.out.println(mouseX+","+mouseY);
            //frame.repaint();
            try {
               Thread.sleep(20);
            }
            catch(Exception exc) {
            }
         }
         try {
            Thread.sleep(100);
         }
         catch(Exception e) {
         }
      }
   }
   //method for moving enemies
   public void runCPU() {
      while(true) {
         while(gameOn) {
            for(int i = 0; i < numEnemies && campaign; i++) {
               //changes information if alive
               if(enemy[i].getAlive()) {
                  enemy[i].setAngle(enemy[i].getAngle()+1);
                  //different method for different enemy type
                  if(!enemy[i].getType().equals("bunker")) {
                     enemy[i].moveTank(wall);
                  }
                  else {
                     enemy[i].moveGun(p1);
                  }
                  //checks if computer should shoot at player
                  if(!eShot[i].isShot()) {
                     eShot[i] = new Shot(enemy[i]);
                     eShot[i].setIsShot(eShot[i].checkShot(p1,enemy[i]));
                     if(eShot[i].isShot()) {
                        try {
                           Thread.sleep(300);
                        }
                        catch(Exception exc) {
                        }
                     }
                  }
               }
            }
            //for survival mode
            if(!campaign) {
               for(int i = 0; i < numEnemies; i++) {
                  if(enemy[i].getAlive() && i <= score/500) {
                     enemy[i].setAlive(true);
                  }
                  if(enemy[i].getAlive()) {
                     enemy[i].setAngle(enemy[i].getAngle()+1);
                     enemy[i].moveTank(wall);
                     //checks if computer should shoot at player
                     if(!eShot[i].isShot()) {
                        eShot[i] = new Shot(enemy[i]);
                        eShot[i].setIsShot(eShot[i].checkShot(p1,enemy[i]));
                        if(eShot[i].isShot()) {
                           try {
                              Thread.sleep(30);
                           }
                           catch(Exception exc) {
                           }
                        }
                     }
                  }
               }
            }
            try {
               Thread.sleep(50);
            }
            catch(Exception exc) {
            }
         }
      }
   }
}