import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

enum GhostType{
    INKY,
    PINKY,
    CLYDE,
    BLINKY
};

public class Ghost extends Thread{
    JLabel sprite;
    String name;

    int x;
    int y;
    int startX;
    int startY;
    boolean blockAdded = false;

    boolean running = true;

    GhostType type;

    Direction direction;
    Direction comingFrom;

    int distanceTravelled;
    final int velocity = 3;

    boolean shouldDrop;
    private ImageIcon texture;

    public Ghost(int x, int y,GhostType type){
        startX = x;
        startY = y;
        this.x = startX;
        this.y = startY;
        this.type = type;

        switch (type){
            case INKY -> name = "inky";
            case PINKY -> name = "pinky";
            case CLYDE -> name = "clyde";
            case BLINKY -> name = "blinky";
        }

        direction = Direction.NONE;

        shouldDrop = false;
        distanceTravelled = 0;

        ImageIcon pinky = new ImageIcon("Images/ghosts/pinky.png");
        ImageIcon inky = new ImageIcon("Images/ghosts/inky.png");
        ImageIcon clyde = new ImageIcon("Images/ghosts/clyde.png");
        ImageIcon blinky = new ImageIcon("Images/ghosts/blinky.png");

        switch (type){
            case GhostType.PINKY -> texture = pinky;
            case GhostType.INKY -> texture = inky;
            case GhostType.CLYDE -> texture = clyde;
            case GhostType.BLINKY -> texture = blinky;
        }

        sprite = new JLabel(texture);
        sprite.setBounds(x,y,16,16);
    }

    public void Reset(){
        this.x = startX;
        this.y = startY;
    }

    public void run(){
        try{
        while(true){
            if(running){
                sleep(5000);
                int random = (int)(Math.random() * 4 + 1);
                if(random == 3) shouldDrop = true;
            }
        }}
        catch(InterruptedException e){
            JOptionPane.showMessageDialog(null, "Interrupted", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void StopThread() {
        running = false;
    }
    public void StartThread() {
        running = true;
    }

    public void DropUpgrade(List<Block> blocks, JPanel panel){
        if(shouldDrop){
            int random = (int)(Math.random() * 5 + 1);
            switch (random){
                case 1 -> {
                    Upgrade upgrade = new Upgrade(x,y,Upgrades.HEALTH);
                    Block block = new Block("health",x +  Math.floorMod(x,16), y + Math.floorMod(y,16));
                    block.sprite = upgrade.texture;
                    blocks.add(block);
                    blockAdded = true;
                }
                case 2 -> {
                    Upgrade upgrade = new Upgrade(x,y,Upgrades.KILL);
                    Block block = new Block("kill",x +  Math.floorMod(x,16), y + Math.floorMod(y,16));
                    block.sprite = upgrade.texture;
                    blocks.add(block);
                    blockAdded = true;
                }
                case 3 -> {
                    Upgrade upgrade = new Upgrade(x,y,Upgrades.STRAWBERRY);
                    Block block = new Block("strawberry",x +  Math.floorMod(x,16), y + Math.floorMod(y,16));
                    block.sprite = upgrade.texture;
                    blocks.add(block);
                    blockAdded = true;
                }
                case 4 -> {
                    Upgrade upgrade = new Upgrade(x,y,Upgrades.PAUSE);
                    Block block = new Block("pause", x + Math.floorMod(x,16), y + Math.floorMod(y,16));
                    block.sprite = upgrade.texture;
                    blocks.add(block);
                    blockAdded = true;
                }
                case 5 -> {
                    Upgrade upgrade = new Upgrade(x,y,Upgrades.LIGHTNING);
                    Block block = new Block("lightning",x +  Math.floorMod(x,16), y + Math.floorMod(y,16));
                    block.sprite = upgrade.texture;
                    blocks.add(block);
                    blockAdded = true;
                }
            }
            shouldDrop = false;
        }
    }

    public void Move(int width,List<Block> blocks, Pacman pacman,JPanel panel){
        sprite.setBounds(x,y,16,16);
        sprite.setIcon(texture);

        switch (direction){
            case RIGHT -> comingFrom = Direction.LEFT;
            case LEFT -> comingFrom = Direction.RIGHT;
            case TOP -> comingFrom = Direction.BOTTOM;
            case BOTTOM -> comingFrom = Direction.TOP;
        }

        if(distanceTravelled == velocity * 5)
            direction = GetDirection(blocks,width);
        DropUpgrade(blocks,panel);

        switch (direction){
            case RIGHT -> {
                x += velocity;
                if (CheckRight(blocks,width))
                    x -= Math.floorMod(x, 16);
            }
            case LEFT -> {
                x -= velocity;
                if (CheckLeft(blocks, width))
                    x += 16 - Math.floorMod(x, 16);
            }
            case TOP -> {
                y -= velocity;
                if (CheckTop(blocks, width))
                    y += 16 - Math.floorMod(y, 16);
            }
            case BOTTOM -> {
                y += velocity;
                if (CheckBottom(blocks, width))
                    y -= Math.floorMod(y, 16);
            }
        }

        for (Block block : blocks) {
            if (block.id.equals(name)) {
                block.x = x;
                block.y = y;
            }
        }

        distanceTravelled += velocity;
    }

    private Direction GetDirection(List<Block> blocks, int width){
            distanceTravelled = 0;
            if(CheckLeft(blocks,width) && CheckTop(blocks,width) && CheckRight(blocks,width))
                return Direction.BOTTOM;
            if(CheckLeft(blocks,width) && CheckBottom(blocks,width) && CheckRight(blocks,width))
                return Direction.TOP;
            if(CheckRight(blocks,width) && CheckTop(blocks,width) && CheckBottom(blocks,width))
                return Direction.LEFT;
            if(CheckLeft(blocks,width) && CheckTop(blocks,width) && CheckBottom(blocks,width))
                return Direction.RIGHT;

            int direction_random = (int)(Math.random() * 4 + 1);

            if(direction_random == 1 && direction != Direction.LEFT && !CheckRight(blocks,width))
                return Direction.RIGHT;
            if(direction_random == 2 && direction != Direction.RIGHT && !CheckLeft(blocks,width))
                return Direction.LEFT;
            if(direction_random == 3 && direction != Direction.TOP && !CheckBottom(blocks,width))
                return Direction.BOTTOM;
            if(direction_random == 4 && direction != Direction.BOTTOM && !CheckTop(blocks,width))
                return Direction.TOP;
            return GetDirection(blocks,width);
    }

    public boolean CheckRight(List<Block> blocks, int width){
        return (HasBlock(x + 17, y + 1, width,blocks) || HasBlock(x + 17, y + 15, width,blocks));
    }
    public boolean CheckLeft(List<Block> blocks, int width){
        return (HasBlock(x - 1 , y + 1,width, blocks) || HasBlock(x - 1, y + 15,width, blocks));
    }
    public boolean CheckTop(List<Block> blocks, int width){
        return (HasBlock(x + 1, y - 1 , width,blocks) || HasBlock(x + 15, y - 1 ,width,blocks));
    }
    public boolean CheckBottom(List<Block> blocks, int width){
        return (HasBlock(x + 1, y + 17,width,blocks) || HasBlock(x + 15, y + 17,width,blocks));
    }

    public boolean HasBlock(int x, int y,int width, List<Block> blocks){
        for (Block block: blocks)
            if (((x > block.x && x < block.x + 16) && (y > block.y && y < block.y + 16))) {
                if (block.id.equals("block"))
                    return true;
                if ((x < 0 || x > width))
                    return true;
            }
        return false;
    }
}
