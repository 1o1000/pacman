import javax.swing.*;

public class Upgrade {
    String id;
    Upgrades upgrade;
    JLabel texture;

    public Upgrade(int x, int y, Upgrades upgrade) {
        this.upgrade = upgrade;
        texture = new JLabel();
        switch (upgrade){
            case STRAWBERRY -> {
                texture.setIcon(new ImageIcon("Images/strawberry.png"));
                id = "strawberry";
            }
            case HEALTH -> {
                texture.setIcon(new ImageIcon("Images/health.png"));
                id = "health";
            }
            case KILL -> {
                texture.setIcon(new ImageIcon("Images/kill.png"));
                id = "kill";
            }
            case PAUSE -> {
                texture.setIcon(new ImageIcon("Images/pause.png"));
                id = "pause";
            }
            case LIGHTNING -> {
                texture.setIcon(new ImageIcon("Images/lightning.png"));
                id = "lightning";
            }
        }
        texture.setBounds(x,y,16,16);
        Block block = new Block(id,x,y);
        block.sprite = texture;
    }
}

enum Upgrades{
    STRAWBERRY,
    HEALTH,
    LIGHTNING,
    KILL,
    PAUSE
}
