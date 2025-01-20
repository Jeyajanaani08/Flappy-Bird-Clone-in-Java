import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeigth = 640;

    //image
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeigth/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;    
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //movement of pipes to the left
    int velocityY = 0; //movement of bird to the up/down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeigth));
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();


        //gamer timer
        gameLoop = new javax.swing.Timer(1000/60, this); //1000/60 = 16.6
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeigth/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        //draw(j);
    }
    public void draw(Graphics g){
        //bg
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeigth, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            String gameOverText = "Game Over";
            String scoreText = "Score: " + String.valueOf((int) score);
        
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int textHeight = metrics.getHeight();
            int ascent = metrics.getAscent(); 
            int descent = metrics.getDescent(); 
        
            int gameOverWidth = metrics.stringWidth(gameOverText);   
            int centerXGameOver = (getWidth() - gameOverWidth) / 2;
            int centerY = getHeight() / 2 - textHeight; 
            int scoreWidth = metrics.stringWidth(scoreText);
            int centerXScore = (getWidth() - scoreWidth) / 2;
        
            g.setColor(Color.YELLOW);
            g.fillRect(centerXGameOver - 10, centerY - ascent + descent - 10, gameOverWidth + 20, textHeight);
            g.fillRect(centerXScore - 10, centerY + ascent + 10, scoreWidth + 20, textHeight);
        
            g.setColor(Color.BLACK);
            g.drawString(gameOverText, centerXGameOver, centerY);
            g.drawString(scoreText, centerXScore, centerY + ascent + textHeight);

            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Created by Jeyajanaani K G",220,630);
        }
        else {
            g.drawString("Score : " + String.valueOf((int)score), 10,35);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Created by Jeyajanaani K G",220,630);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)){
                gameOver = true;   
            }
        }

        if (bird.y > 540) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

   

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            if (gameOver){
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }  
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) { 
        if (e.getKeyCode() == KeyEvent.VK_UP){
        velocityY = -9;
        }
    }
}