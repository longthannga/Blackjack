import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.Timer;

public class Table extends JPanel implements ActionListener, LineListener {

    private ArrayList<Card> deck;
    private ArrayList<Card> player;
    private ArrayList<Card> dealer;
    private BufferedImage back;
    private JButton hitB, standB, rpB;
    private boolean playerStatus, gameStatus, drawS, soundFinished;
    private int playerScore, dealerScore;
    private int playerW, dealerW, winner;
    private AudioInputStream hit1, st2, lo, startAIS, standAIS;
    private Clip hitC, winC, lose, sClip, standC;
    private File hi1, s2, l, start, standF;
    private Timer soundDelayTimer;

	public Table() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        playerStatus = false;
        gameStatus = false;
        playerW = 0;
        dealerW = 0;
        winner = 0;
        soundFinished = false;

        deck = new ArrayList<Card>();
        int value;
        String suit = "";
        for (int i = 0; i < 52; i++){
            value = (i + 1) % 13;
            if (value == 0){
                value = 13;
            }
            if (i <= 13){
                suit = "hearts";
            } else if (13 < i && i <= 26){
                suit = "diamonds";
            } else if (26 < i && i <= 39){
                suit = "clubs";
            } else if (39 < i && i <= 52){
                suit = "spades";
            } 
            deck.add(new Card(value, suit));
        }

        shuffle();
        player = new ArrayList<Card>();
        dealer = new ArrayList<Card>();
        

        try{
            back = ImageIO.read(new File("backside.jpeg"));
        } catch (IOException e){
            System.out.println(e);
        }

        //buttons
        hitB = new JButton("Hit");
        
        hitB.setBounds(10, 10, 100, 20);
        hitB.addActionListener(this);
        this.add(hitB);
        

        standB = new JButton("Stand");
        standB.setBounds(130, 10, 100, 20);
        standB.addActionListener(this);
        this.add(standB);

        rpB = new JButton("Play");
        rpB.setBounds(240, 10, 100, 20);
        rpB.addActionListener(this);
        this.add(rpB);

        //audio
        hi1 = new File("flip.wav");
        hit1 = AudioSystem.getAudioInputStream(hi1);
        hitC = AudioSystem.getClip();
        hitC.open(hit1);
    
        s2 = new File("win1.wav");
        st2 = AudioSystem.getAudioInputStream(s2);
        winC = AudioSystem.getClip();
        winC.open(st2);

        l = new File("lose1.wav");
        lo = AudioSystem.getAudioInputStream(l);
        lose = AudioSystem.getClip();
        lose.open(lo);

        start = new File("start.wav");
        startAIS = AudioSystem.getAudioInputStream(start);
        sClip = AudioSystem.getClip();
        sClip.open(startAIS);

        standF = new File("Stand.wav");
        standAIS = AudioSystem.getAudioInputStream(standF);
        standC = AudioSystem.getClip();
        standC.open(standAIS);

        soundDelayTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Play win/lose sound after the delay
                endSound();  
                soundDelayTimer.stop(); 
            }
        });
       
    }
	
	public Dimension getPreferredSize() {
		//Sets the size of the panel
		return new Dimension(800,600);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(Color.green);
		g.fillRect(0,0,800,600);

        //draw the player's cards
        if (soundFinished) {
            // Draw the cards only when the sound has finished playing
            drawCards(g);
        }
	}
	


	public void shuffle(){
		//write code to shuffle your deck
        for (int i = 0; i < deck.size(); i++){
            int j = (int)(Math.random()*deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
	}


    public void actionPerformed(ActionEvent e){
        if (gameStatus == false){
            if (e.getSource() == rpB){
                gameStatus = true;
                playerStatus = true;
                drawS = true;
                newGame();
            }
        }
        if (playerScore == 21 || playerScore > 21){
            playerStatus = false;
        }
        if (playerStatus){
            if(e.getSource() == hitB){
                hitS();
                draw(player, deck);
                playerScore = getScore(player);
                if (playerScore > 21 || playerScore == 21){
                    playerStatus = false;
                }
            }
            if (e.getSource() == standB){
                standS();
                playerStatus = false;
            }
        }
        if (!playerStatus){
            dealer.get(0).toTrue();
            dealerScore = getScore(dealer);
            if (playerScore < 22){
                if (dealerScore < 22){
                    while (dealerScore < 18){
                        draw(dealer, deck);
                        dealerScore = getScore(dealer);
                    } 
                    if (dealerScore < 22) {
                        if (dealerScore > playerScore){
                            dealerW++;
                            winner = 2;
                        } else if (dealerScore < playerScore){
                            playerW++;
                            winner = 1;
                        } else {
                            winner = 3;
                        }
                    } else {
                        playerW++;
                        winner = 1;
                    }
                    gameStatus = false;
                }
            } else {
                dealerW++;
                winner = 2;
                gameStatus = false;
            }
        }
        
        
        repaint();   
        
    }

    public void draw(ArrayList<Card> toDeck, ArrayList<Card> fromDeck){
        toDeck.add(fromDeck.get(0));
        fromDeck.remove(0);
    }

    private int getScore(ArrayList<Card> a){
        int score = 0;
        for (int i = 0; i < a.size(); i++){
            if (a.get(i).getStatus()){
                if (a.get(i).getV() >= 10 && !a.get(i).getName().equals("A")){
                    score += 10;
                } else {
                    score += a.get(i).getV();
                }
            }
        }
        return score;
    }

    private void newGame(){
        int ps = player.size();
        int ds = dealer.size();
        soundFinished = false;
        winner = 0;
        startS();

        for (int i = 0; i < ps; i++){
            draw(deck, player);
        } 
        for (int i = 0; i < ds; i++){
            draw(deck, dealer);
        }
        shuffle();
        for (int i = 0; i < 2; i++){
            draw(player, deck);
            draw(dealer, deck);
        }
        dealer.get(0).toFalse();
        playerScore = getScore(player);
        dealerScore = getScore(dealer);
    }

    private void winS(){
        winC.start();
        winC.setMicrosecondPosition(0);
    }
 
    private void hitS(){
        hitC.addLineListener(this);
        hitC.start();
        hitC.setMicrosecondPosition(0);
    }
 
    private void loseS(){
        lose.start();
        lose.setMicrosecondPosition(0);
    }
 
    private void startS(){
        sClip.addLineListener(this);
        sClip.start();
        sClip.setMicrosecondPosition(0);
    }

    public void update(LineEvent event) {
        if (event.getSource() == sClip && event.getType() == LineEvent.Type.STOP) {
            soundFinished = true;
            repaint(); // Repaint after the sound delay
        } 
        if ((event.getSource() == standC || event.getSource() == hitC) && event.getType() == LineEvent.Type.STOP){
            if (!soundDelayTimer.isRunning())
                soundDelayTimer.start();
        }
    }

    private void standS(){
        standC.addLineListener(this);
        standC.start();
        standC.setMicrosecondPosition(0);
    }

    private void drawCards(Graphics g){
        if(drawS){
            for (int i = 0; i < player.size(); i++){
                player.get(i).drawMe(g, 50 + 80*i, 95);
            }
            for (int i = 0; i < dealer.size(); i++){
                if (i == 0){
                    if (dealer.get(i).getStatus()){
                        dealer.get(i).drawMe(g, 50 + 80*i, 375);
                    } else {
                        g.setColor(Color.black);
                        g.drawRect(50 + 80*i, 375,100,150);
                        g.drawImage(back, 50 + 80*i, 375, 100, 150, null);
                    }
                } else
                    dealer.get(i).drawMe(g, 50 + 80*i, 375);
            }
            playerScore = getScore(player);
            dealerScore = getScore(dealer);

            Font font = new Font ("Time New Roman", Font.PLAIN, 20);
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.drawString("Player's card values: " + playerScore, 50, 50);
            g.drawString("Won: " + playerW, 50, 75);
            g.drawString("Dealer's card values: " + dealerScore, 50, 325);
            g.drawString("Won: " + dealerW, 50, 350);
            if (winner == 1){
                g.drawString("You Win!", 600, 50);
            } else if (winner == 2){
                g.drawString("You Lose!", 600, 50);
            } else if (winner == 3){
                g.drawString("Draw!", 600, 50);
            }
        }
    }

    public void endSound(){
        if (winner == 1)
            winS();
        else if (winner == 2)
            loseS();
    }
 

}