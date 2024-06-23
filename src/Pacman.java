import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pacman extends Thread {
    public int x;
    public int y;
    public Direction direction;
    private Direction nextDirection;

    public int lives;

    public JLabel sprite;

    private ImageIcon currentTexture;
    private  JPanel panel;

    private int animation_index = 0;

    public int points;
    private double timer;
    private int multiplier = 1;

    private boolean ateStrawberry;

    boolean running;

    private boolean ateLightning;
    private double lightningTimer;

    private boolean reverse;
    public boolean isDead;
    public boolean started;

    public boolean killActivated;
    public boolean paused;

    public double pause_timer = 0.0;

    public int velocity = 3;

    final private List<ImageIcon> ra; // right animation
    final private List<ImageIcon> la; // left animation
    final private List<ImageIcon> ta; // top animation
    final private List<ImageIcon> ba; // bottom animation

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public Pacman(int x,int y){
        this.x = x;
        this.y = y;

        isDead = false;
        started = true;
        killActivated = false;

        points = 0;
        lives = 3;

        reverse = false;
        direction = Direction.NONE;
        nextDirection = Direction.NONE;

        ra = new ArrayList<ImageIcon>();
        ra.add(new ImageIcon("Images/pr/1.png"));
        ra.add(new ImageIcon("Images/pr/2.png"));
        ra.add(new ImageIcon("Images/pr/3.png"));

        la = new ArrayList<ImageIcon>();
        la.add(new ImageIcon("Images/pl/1.png"));
        la.add(new ImageIcon("Images/pl/2.png"));
        la.add(new ImageIcon("Images/pl/3.png"));

        ta = new ArrayList<ImageIcon>();
        ta.add(new ImageIcon("Images/pu/1.png"));
        ta.add(new ImageIcon("Images/pu/2.png"));
        ta.add(new ImageIcon("Images/pu/3.png"));

        ba = new ArrayList<ImageIcon>();
        ba.add(new ImageIcon("Images/pd/1.png"));
        ba.add(new ImageIcon("Images/pd/2.png"));
        ba.add(new ImageIcon("Images/pd/3.png"));


        currentTexture = ra.getLast();
        sprite = new JLabel(currentTexture);
        sprite.setBounds(x,y,16,16);
    }

    public void StopThread() {
        running = false;
    }
    public void StartThread() {
        running = true;
    }

    public void Move(int input,List<Block> blocks,JPanel panel, int width, int height){
        this.panel = panel;
        if(input != 0)
            started = true;

        if(x  > width) x = 0;
        else if(x - velocity < -16) x = width - 16;

        switch (input){
            case 1 -> nextDirection = Direction.RIGHT;
            case 2 -> nextDirection = Direction.TOP;
            case -1 -> nextDirection = Direction.LEFT;
            case -2 -> nextDirection = Direction.BOTTOM;
        }

        switch (nextDirection){
            case RIGHT -> {
                if (!HasBlock(x + 17 , y + 1, blocks) && !HasBlock(x + 17 , y + 15, blocks)){
                    direction = Direction.RIGHT;
                    nextDirection = Direction.NONE;
                }
            }
            case LEFT -> {
                if (!HasBlock(x - 1  , y + 1, blocks) && !HasBlock(x - 1  , y + 15, blocks)) {
                    direction = Direction.LEFT;
                    nextDirection = Direction.NONE;
                }
            }
            case TOP -> {
                if (!HasBlock(x + 1, y - 1  ,blocks) && !HasBlock(x + 15, y - 1  ,blocks)) {
                    direction = Direction.TOP;
                    nextDirection = Direction.NONE;
                }
            }
            case BOTTOM -> {
                if (!HasBlock(x + 1, y + 17,blocks) && !HasBlock(x + 15, y + 17,blocks)) {
                    direction = Direction.BOTTOM;
                    nextDirection = Direction.NONE;
                }
            }
        }

        switch (direction){
            case RIGHT -> {
                x += velocity;
                if (HasBlock(x + 17 - velocity, y + 1, blocks) || HasBlock(x + 17 - velocity, y + 15, blocks))
                    x -= Math.floorMod(x, 16);
            }
            case LEFT -> {
                x -= velocity;
                if (HasBlock(x - 1 + velocity , y + 1, blocks) || HasBlock(x - 1 + velocity , y + 15, blocks)) {
                    x += 16 - Math.floorMod(x, 16);
                }
            }
            case TOP -> {
                y -= velocity;
                if (HasBlock(x + 1, y - 1 + velocity ,blocks) || HasBlock(x + 15, y - 1 + velocity ,blocks))
                    y += 16 - Math.floorMod(y, 16);
            }
            case BOTTOM -> {
                y += velocity;
                if (HasBlock(x + 1, y + 17 - velocity,blocks) || HasBlock(x + 15, y + 17 - velocity,blocks))
                    y -= Math.floorMod(y, 16);
            }
        }
        CheckEntities(blocks);
        sprite.setBounds(x,y,16,16);
        sprite.setIcon(currentTexture);
    }

    public void CheckEntities(List<Block> blocks){
            HasBlock(x + 16, y + 1 , blocks);HasBlock(x + 16, y + 15, blocks);
            HasBlock(x - 1 , y + 1 , blocks);HasBlock(x - 1 , y + 15, blocks);
            HasBlock(x + 1 , y + 17, blocks);HasBlock(x + 15, y + 17, blocks);
            HasBlock(x + 1 , y - 1 , blocks);HasBlock(x + 15, y - 1 , blocks);
    }

    public void run() {
        try{
            while(true){
                sleep(50);
                if(running){
                    if(paused) pause_timer += 50;
                    if(pause_timer > 5000) {
                        paused = false;
                        pause_timer = 0;
                    }
                    if(timer >= 5){
                        ateStrawberry = false;
                        multiplier = 1;
                        timer = 0;
                    }
                    if(lightningTimer >= 3){
                        ateLightning = false;
                        lightningTimer = 0;
                        velocity -= 1;
                    }
                    if(ateStrawberry) timer += 0.05;
                    if(ateLightning) lightningTimer += 0.05;
                    switch(direction){
                        case RIGHT -> animate(ra);
                        case LEFT -> animate(la);
                        case TOP -> animate(ta);
                        case BOTTOM -> animate(ba);
                        case NONE -> animate(ra);
                    }
                }
            }
        }catch(InterruptedException e){
            JOptionPane.showMessageDialog(null, "Interrupted", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void EatStrawberry(){
        ateStrawberry = true;
        multiplier = 10;
    }
    private void EatLightning(){
        if(ateLightning) return;
        ateLightning = true;
        velocity += 1;
    }
    private void EatHealth(){
        lives++;
    }
    private void CollectPoints(){
        points += multiplier * 10;
    }

    private void animate(List<ImageIcon> animation){
        if(animation_index == animation.size() - 1)
            reverse = true;
        if(!reverse && animation_index < animation.size())
            animation_index++;
        if(reverse && animation_index > 0)
            animation_index--;
        else
            reverse = false;
        currentTexture = animation.get(animation_index);
    }

    private void Die(){
        isDead = true;
        started = false;
        direction = Direction.NONE;
    }

    public boolean HasBlock(int x, int y, List<Block> blocks){
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            if ((x > block.x && x < block.x + 16) && (y > block.y && y < block.y + 16)) {
                switch (block.id){
                    case "block": return true;
                    case "pinky":
                    case "blinky":
                    case "inky":
                    case "clyde":
                        Die();
                        break;
                    case "strawberry":
                        EatStrawberry();
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                    case "kill":
                        killActivated = true;
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                    case "pause":
                        paused = true;
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                    case "lightning":
                        EatLightning();
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                    case "health":
                        EatHealth();
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                    case "dot":
                        CollectPoints();
                        panel.remove(block.sprite);
                        iterator.remove();
                        break;
                }
            }
        }
        return false;
    }
}

enum Direction {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    NONE
}