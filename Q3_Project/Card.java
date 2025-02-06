import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card {

    private int value;
    private String name;
    private String suit;
    private BufferedImage suitImage; 
    private boolean status;

    public Card(int v, String suit){
        status = true;
		value = v;
        this.suit = suit;
		if (v == 1){
            name = "A";
            value = 11;
        } else if (v == 11){
            name = "J";
        } else if (v == 12){
            name = "Q";
        } else if (v == 13){
            name = "K";
        } else {
            name = value + "";
        }
        try{
            suitImage = ImageIO.read(new File(suit + ".png"));
        } catch (IOException e){
            System.out.println(e);
        }
	}

	public int getValue(){
        return this.value;
    }

	public void drawMe(Graphics g, int x, int y){
        g.setColor(Color.white);
        g.fillRect(x, y,100,150);
        g.setColor(Color.black);
        g.drawRect(x, y,100,150);
        
        //draw suit
        g.drawImage(suitImage, x+2, y, 40, 50, null);
        g.drawImage(suitImage, x + 55, y + 105, 40, 50, null);
        
        //Set Font to use with drawString   
        Font font = new Font("Arial", Font.PLAIN, 50);
        g.setFont(font);

		if(suit.equals("hearts") || suit.equals("diamonds")){
      	   g.setColor(Color.red);
		} else {
            g.setColor(Color.BLACK);
        }
      
        if (value == 10){
            g.drawString(this.name + "", x + 15, y + 95);
        }
        else{
            g.drawString(this.name + "", x + 30, y + 95);  
        }
    }

    public boolean getStatus(){
        return status;
    }

    public void toFalse(){
        status = false;
    }

    public void toTrue(){
        status = true;
    }

    public int getV(){
        return value;
    }

    public String getName(){
        return name;
    }
   
}