/*
Jacob Schuh
CSC 191
Final Project
5-6-20
This java file makes the game "Rescue the Queens"
The buttons all appear as "*" at first and when clicked on
they are shown to be queens or blanks. If they are blanks
the all blanks around them are reveal and so on in a cascading effect.
You win if all queens are revealed before all of the blanks are.
 */
package jacobschuhfinal;
import static jacobschuhfinal.Board.buttons;
import static jacobschuhfinal.Board.nums;
import static jacobschuhfinal.Queens.addToQ;
import javax.swing.*;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Scanner;

class Board extends JFrame{
    //creates variables for GUI
    public static JPanel windowContent;
    public static JTextField displayField;
    public static JButton buttons[][];
    public static JPanel pl;
    //creates additional variables for the game
    public static int[] nums;
    public static int r;
    public static int blank=0;
    public static int q=0;
    
    public Board(int r){//constructor
        this.r=r;
    }
    
    public static void createNums(){//fills the num[] array in order
        nums = new int[r*r];
        for (int i = 0; i < r*r; i++) {
            nums[i]=i+1;
        }
        
        Rand(nums);//shuffles the num[] array
        //every num divisible by 4 is a queen
    }
        
    public static int[] Rand(int[] a){//method that shuffles num array
        Random rgen = new Random();//creates random num	

        for (int i=0; i<a.length; i++) {//goes through array switching nums
            int rand = rgen.nextInt(a.length);
            int temp = a[i];
            a[i] = a[rand];
            a[rand] = temp;
        }
        return a;
    }
    
    public int getR(){
        return r;
    }
    
    public void setR(int r){
        this.r=r;
    }
    
}

class Queens extends Board{
    public Queens(int r){
        super(r);
    }
    
    public static void addToQ(){//adds to queen counter
        q+=1;
    }
    
    //recursively checks all buttons around the button clicked
    //and all buttons around those buttons until stopped by queens
    public static void recurQueen(int f, int g){
        if(f>=0&&g>=0&&f<=r-1&&g<=r-1){//checks if the button is still in bounds
            if(nums[(r*f)+g]%4!=0){//checks that the current button isn't a queen(not divisible by 4)
                blank++;
                buttons[f][g].setText("");//changes text on button
                nums[(r*f)+g]=0;//sets button equal to 0 so it isn't changed again
                
                //these check buttons in every direction of the current button
                recurQueen(f-1, g);
                recurQueen(f-1, g-1);
                recurQueen(f-1, g+1);
                recurQueen(f+1, g);
                recurQueen(f+1, g-1);
                recurQueen(f+1, g+1);
                recurQueen(f, g-1);
                recurQueen(f, g+1);
                
                
            }
        }
    }
    
    //this method continuously checks the buttons until 
    //the current method ends
    public static void check(){
        for (int i = 0; i < r; i++) {//for loop to go through each button
            for (int j = 0; j < r; j++) {
                
                int left = i;//changes the variables so they can be used by inner classes/loops
                int right = j;
                
                //this if() checks if the button is divisible by 4 and greater than 0
                if(nums[(r*i)+j]%4==0&&nums[(r*i)+j]>0){
                    buttons[i][j].addActionListener(
                        new ActionListener(){
                            public void actionPerformed(ActionEvent event){//checks if the button has been clicked
                                
                                if(nums[(r*left)+right]>0){//checks if it is greater than 0
                                    nums[(r*left)+right]=-4;//sets it equal to a negative number
                                    buttons[left][right].setText("\u2655");//changes text to a queen
                                    addToQ();
                                }
                            }
                        }
                    );
                }
                if(nums[(r*i)+j]%4!=0&&nums[(r*i)+j]>0){//checks if the button isn't divisible by 4 and greater than 0
                    buttons[i][j].addActionListener(
                        new ActionListener(){
                            public void actionPerformed(ActionEvent event){//checks if it has been clicked
                                if(nums[(r*left)+right]>0){//checks if it is greater than 0
                                    recurQueen(left,right);//passes to the recursive loop
                                    nums[(r*left)+right]=-1;//sets num equal to -1
                                }
                            }
                        }
                    );
                }
            }
        }
    }
}
       

public class JacobSchuhFinal extends JFrame{

    public JacobSchuhFinal(){
        int r = Board.r; //size of the button array (r*r)
        boolean game=true;
        Board.createNums();

        
        Board.windowContent = new JPanel();
        Board.buttons = new JButton[r][r];
        
        //setup our layout manager for this panel
        BorderLayout bl = new BorderLayout();
        Board.windowContent.setLayout(bl);
        
        //create our displayfield and place it in the North area of window
        Board.displayField = new JTextField(30);
        Board.displayField.setText("Score: "+Board.q+"  Queens Left: "+(((r*r)/4)-Board.q)+"  Blanks Left: "+Board.blank);
        Board.displayField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        Board.displayField.setEditable(false);
        Board.windowContent.add("North", Board.displayField);
        
        for(int i=0; i<Board.buttons.length; i++){
            for(int j=0; j<Board.buttons[i].length; j++){
                //we can set the text of the button using this JButton constructor
                Board.buttons[i][j] = new JButton(String.valueOf('*'));
                Board.buttons[i][j].setFont(new Font("SansSerif",Font.PLAIN, 24));
            }
        }
        

        //create a panel with a GridLayout that will contain our 16 buttons
        Board.pl = new JPanel();
        GridLayout gl = new GridLayout(r,r); //row, cols
        Board.pl.setLayout(gl);
              
        

        
        //add the buttons to the panel pl
        for (int i = 0; i < Board.buttons.length; i++) {
            for (int j = 0; j < Board.buttons[i].length; j++) {
                Board.pl.add(Board.buttons[i][j]);
            }
        }
        // add the pl panel to the center of the borderlayout window
        Board.windowContent.add("Center",Board.pl);
        
        
        
        //create the frame and set its content pane
        JFrame frame = new JFrame("Grid of Buttons");
        frame.setContentPane(Board.windowContent);
        //set the size of the window to be just big enough to fit our controls
        frame.pack();
        //display the window
        frame.setVisible(true);
        
        
        do{
            Queens.check(); //runs through the button array looking for action events
            
            //updates the score at the top of the screen
            Board.displayField.setText("Score: "+Board.q+"  Queens Left: "+(((r*r)/4)-Board.q)+"  Blanks Left: "+Board.blank);

            if((r*r)-Board.blank==(r*r)/4)//game ends
                game=false;
            if(Board.q==(r*r)/4)//game ends
                game = false;
        }while(game);
        if((r*r)-Board.blank==(r*r)/4)//checks if user lost
            Board.displayField.setText("You Lose");
        
        if(Board.q==(r*r)/4)//checks if loser won
            Board.displayField.setText("You Win");
        
    }
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter an integer for the board size (n*n): ");
        int n= input.nextInt();//n=size of the array (n*n)
        Queens q1 = new Queens(n);//calls queen class and creates the nums[] array and the button[][] array
        JacobSchuhFinal board = new JacobSchuhFinal(); //begins main class to run the game
    }
    
}
