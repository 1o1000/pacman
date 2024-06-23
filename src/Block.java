import javax.swing.*;

public class Block {
    JLabel sprite;
    String id;
    int x;
    int y;

    public Block(String id,int x, int y) {
        this.sprite = new JLabel();
        this.id = id;
        this.x = x;
        this.y = y;
        switch (id){
            case "block"-> sprite.setIcon(new ImageIcon("Images/block.png"));
            case "strawberry"-> sprite.setIcon(new ImageIcon("Images/strawberry.png"));
            case "dot"-> sprite.setIcon(new ImageIcon("Images/dot.png"));
        }
    }
}
