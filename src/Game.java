import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;

enum Scene {
    HOME,
    GAME,
    SELECT_MAP,
    PAUSE,
    ADD_SCORE,
    SHOW_SCORES,
    GAME_OVER,
    GAME_WON,
}

public class Game extends Thread {
    final private int width;
    final private int height;

    private int map_length;

    private  JFrame frame;
    Scene scene;

    public Pacman pacman;
    public  Ghost blinky;
    public  Ghost inky;
    public  Ghost pinky;
    public  Ghost clyde;
    private JPanel panel;
    private JLabel point;
    private JLabel lives;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean paused;
    private String path;

    private int input = 0;
    private List<Block> blocks;

    public Game(int w, int h) {
        width = w;
        height = h;

        frame = new JFrame();
        blocks = new ArrayList<>();
        panel = new JPanel();
        pacman = new Pacman(0,0);
        pacman.StartThread();
        pacman.start();

        scene = Scene.HOME;
        frame.getContentPane().setBackground(Color.black);
    }
    public void SelectMap(){
        ChangeScene(Scene.SELECT_MAP,width);

        JButton map1 = new JButton("Map 1");
        map1.addActionListener(e -> {
            path = "Levels/Level1.txt";
            map_length = 30 * 16;
            InitGame();
        });
        JButton map2 = new JButton("Map 2 ");
        map2.addActionListener(e -> {
            map_length = 20 * 16;
            path = "Levels/Level2.txt";
            InitGame();
        });
        JButton map3 = new JButton("Map 3");
        map3.addActionListener(e -> {
            map_length = 29 * 16;
            path = "Levels/Level3.txt";
            InitGame();
        });
        JButton map4 = new JButton("Map 4");
        map4.addActionListener(e -> {
            map_length = 21 * 16;
            path = "Levels/Level4.txt";
            InitGame();
        });
        JButton map5 = new JButton("Map 5");
        map5.addActionListener(e -> {
            map_length = 23 * 16;
            path = "Levels/Level5.txt";
            InitGame();
        });

        frame.add(map1);
        frame.add(map2);
        frame.add(map3);
        frame.add(map4);
        frame.add(map5);

        frame.setLayout(new GridLayout(5,1, 0, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void InitGame(){
        panel = new JPanel();
        panel.setSize(width,height);
        panel.setBackground(Color.black);
        panel.setLayout(null);

        LoadMap(path);

        int x0 = 0;
        int y0 = 0;
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        int x3 = 0;
        int y3 = 0;
        int pacmanX = 0;
        int pacmanY = 0;

        for (Block block: blocks){
            block.sprite.setBounds(block.x,block.y,16,16);
            switch (block.id){
                case "inky" -> {x1 = block.x ; y1 = block.y;}
                case "pinky" -> {x2 = block.x ; y2 = block.y;}
                case "blinky" -> {x0 = block.x ; y0 = block.y;}
                case "clyde" -> {x3 = block.x ; y3 = block.y;}
                case "pacman" -> {pacmanX = block.x;pacmanY = block.y;}
            }
            panel.add(block.sprite);
        }

        pacman.StartThread();

        pacman = new Pacman(pacmanX,pacmanY);
        blinky = new Ghost(x0,y0,GhostType.BLINKY);
        inky = new Ghost(x1,y1,GhostType.INKY);
        pinky = new Ghost(x2,y2,GhostType.PINKY);
        clyde = new Ghost(x3,y3,GhostType.CLYDE);

        pacman.StartThread();
        pacman.start();
        blinky.start();
        inky.start();
        clyde.start();
        pinky.start();

        StartGame();
    }

    public void DisplayHome(){
        frame.setSize(width,height);
        frame.setTitle("Pacman");

        JLabel label1 = new JLabel("");
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> SelectMap());
        JButton highScores = new JButton("High Scores");
        highScores.addActionListener(e -> DisplayBestScores());
        JButton quitButton = new JButton("QUIT");
        quitButton.addActionListener(e -> System.exit(0));
        JLabel label2 = new JLabel("");

        frame.add(label1);
        frame.add(startButton);
        frame.add(highScores);
        frame.add(quitButton);
        frame.add(label2);

        paused = false;

        point = new JLabel("");
        lives = new JLabel("");

        point.setBounds(width + 10,10,100,15);
        point.setForeground(Color.WHITE);
        lives.setBounds(width + 10,30,100,15);
        lives.setForeground(Color.WHITE);

        frame.setLayout(new GridLayout(5,1, 0, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void DisplayBestScores(){
        try {
            ChangeScene(Scene.SHOW_SCORES,width);
            frame.setLayout(new GridBagLayout());

            JPanel topRow = new JPanel(new GridLayout(1, 3));
            JButton goBack = new JButton("Go Back");
            goBack.addActionListener(e -> {
                ChangeScene(Scene.HOME, width);
                DisplayHome();
            });
            topRow.add(goBack);

            List<Score> scores = Score.Load();
            scores.sort((s1, s2) -> Integer.compare(s2.score, s1.score));
            String[] ss = new String[scores.size()];

            for(int i = 0; i < scores.size(); i++)
                ss[i] = "Name: " + scores.get(i).name + " - " + "Points: " + scores.get(i).score;
            JList<String> scoreList = new JList<>(ss);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            frame.add(topRow, gbc);

            gbc.gridy = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;

            frame.add(new JScrollPane(scoreList), gbc);
            frame.setSize(width,height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "IO Exception. Please Try To Restart The game.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void DisplayAddScore(){
        ChangeScene(Scene.ADD_SCORE,width);

        JTextField name = new JTextField(10);
        name.setBounds(50,50,100,30);
        JButton addScore = new JButton("Add Score");
        addScore.setBounds(50,80,100,30);
        addScore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Score s = new Score(name.getText(), pacman.points);
                    Score.Write(s);
                    ChangeScene(Scene.HOME, width);
                    DisplayHome();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "An error has ben occured", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );


        frame.add(name);
        frame.add(addScore);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void ChangeScene(Scene newScene, int width){
        scene = newScene;

        frame.dispose();
        frame = new JFrame();
        frame.setSize(width,height);
        frame.setBackground(Color.black);
        frame.getContentPane().setBackground(Color.black);
    }

    private void StartGame(){
        ChangeScene(Scene.GAME, width + 100);
        panel.setSize(width,height);
        panel.setBackground(Color.black);
        panel.setLayout(null);

        for (Block block: blocks){
            if(block.id.equals("pacman")){
                pacman.x = block.x;
                pacman.y = block.y;
            }
        }

        inky.Reset();
        pinky.Reset();
        clyde.Reset();
        blinky.Reset();

        SetKeyListener();

        panel.add(pacman.sprite);
        panel.add(pinky.sprite);
        panel.add(blinky.sprite);
        panel.add(inky.sprite);
        panel.add(clyde.sprite);

        frame.add(panel);
        frame.add(point);
        frame.add(lives);

        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void SetKeyListener(){
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_W)
                    input = 2;
                if(e.getKeyCode() == KeyEvent.VK_A)
                    input = -1;
                if(e.getKeyCode() == KeyEvent.VK_S)
                    input = -2;
                if(e.getKeyCode() == KeyEvent.VK_D)
                    input = 1;
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    PauseGame();
            }
        });
    }

    private void ResetGame(){
        pacman.lives--;
        StartGame();
        pinky.Reset();
        blinky.Reset();
        inky.Reset();
        clyde.Reset();
        pinky.Move(map_length, blocks, pacman, panel);
        blinky.Move(map_length, blocks, pacman, panel);
        inky.Move(map_length,blocks, pacman, panel);
        clyde.Move(map_length,blocks, pacman, panel);
        pacman.isDead = false;
    }

    private void GameOver(){
        gameOver = true;
        ChangeScene(Scene.GAME_OVER, width);

        JLabel gameOverLabel = new JLabel("Game Over");

        JButton saveScore = new JButton("Save score");
        saveScore.setBounds(width / 2 - 125, height / 2 - 10,250,30);
        saveScore.addActionListener(e -> DisplayAddScore());

        JButton quit = new JButton("HOME");
        quit.setBounds(width / 2 - 125, height / 2 + 30,250,30);
        quit.addActionListener(e -> {
            ChangeScene(Scene.HOME, width);
            gameOver = false;
            DisplayHome();
        });

        gameOverLabel.setBounds(width / 2 - 40, height / 2 - 50,100,10);
        gameOverLabel.setForeground(Color.WHITE);

        frame.add(gameOverLabel);
        frame.add(saveScore);
        frame.add(quit);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void GameWon(){
        gameWon = true;
        ChangeScene(Scene.GAME_WON, width);

        JLabel gameOver = new JLabel("Game WON");

        JButton saveScore = new JButton("Save score");
        saveScore.setBounds(width / 2 - 125, height / 2 - 10,250,30);
        saveScore.addActionListener(e -> DisplayAddScore());

        JButton quit = new JButton("HOME");
        quit.setBounds(width / 2 - 125, height / 2 + 30,250,30);
        quit.addActionListener(e -> {
            ChangeScene(Scene.HOME, width);
            DisplayHome();
            gameWon = false;
        });

        gameOver.setBounds(width / 2 - 40, height / 2 - 50,100,10);
        gameOver.setForeground(Color.WHITE);

        frame.add(gameOver);
        frame.add(saveScore);
        frame.add(quit);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void DisplayGame() {
        if(gameOver) return;
        if(gameWon) return;

        for(Block block: blocks)
            if(block.id.equals("dot"))
                break;

        if(pacman.killActivated){
            inky.Reset();
            blinky.Reset();
            pinky.Reset();
            clyde.Reset();
            pacman.killActivated = false;
        }
        if(pacman.isDead && pacman.lives > 0) {
            ResetGame();
            return;
        }
        if(pacman.lives <= 0 && !gameOver) GameOver();

        point.setText("Points: " + pacman.points);
        lives.setText("Lives: " + pacman.lives);
        if(!paused){
            if(pinky.blockAdded || clyde.blockAdded || blinky.blockAdded || inky.blockAdded) {
                int count = 0;
                panel.removeAll();
                for (Block block : blocks) {
                    panel.add(block.sprite);
                    if(block.id.equals("dot")) count++;
                }
                panel.add(pacman.sprite);
                panel.add(pinky.sprite);
                panel.add(inky.sprite);
                panel.add(clyde.sprite);
                panel.add(blinky.sprite);
                panel.revalidate();
                panel.repaint();
                pinky.blockAdded = false;
                inky.blockAdded = false;
                blinky.blockAdded = false;
                clyde.blockAdded = false;
            }
            int count = 0;
            for (Block block : blocks) {
                panel.add(block.sprite);
                if(block.id.equals("dot")) count++;
            }
            if(count == 0)
                GameWon();
            pacman.Move(input,blocks,panel,map_length,height);
            if(pacman.started && !pacman.paused){
                pinky.Move(map_length, blocks, pacman,panel);
                inky.Move(map_length,blocks, pacman,panel);
                blinky.Move(map_length,blocks, pacman,panel);
                clyde.Move(map_length,blocks, pacman,panel);
            }
        }

        input = 0;
    }
    private void PauseGame(){
        if(!paused){
            ChangeScene(Scene.PAUSE, width);
            paused = true;
            JButton continueButton = new JButton("CONTINUE");
            continueButton.setBounds(100,50,100,30);
            continueButton.addActionListener(e -> ContinueGame());
            JButton quitButton = new JButton("HOME");
            quitButton.setBounds(100,100,100,30);
            quitButton.addActionListener(e -> {
                ChangeScene(Scene.HOME, width);
                inky.StopThread();
                blinky.StopThread();
                pinky.StopThread();
                clyde.StopThread();
                pacman.StopThread();
                DisplayHome();
            });

            frame.add(continueButton);
            frame.add(quitButton);
            frame.setBackground(Color.BLACK);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);
            frame.setVisible(true);
        }
    }
    private void ContinueGame(){
        ChangeScene(Scene.GAME,width + 100);

        panel.add(pacman.sprite);
        panel.add(pinky.sprite);
        panel.add(blinky.sprite);
        panel.add(inky.sprite);
        panel.add(clyde.sprite);
        panel.add(point);
        panel.add(lives);

        for(Block block: blocks)
            panel.add(block.sprite);

        SetKeyListener();

        paused = false;
        DisplayGame();
        frame.add(panel);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public void run() {
        try{
            while(true){
                sleep(20);
                switch (scene){
                    case GAME -> DisplayGame();
                    case PAUSE -> PauseGame();
                }
            }
        }catch(InterruptedException e){
            JOptionPane.showMessageDialog(null, "Interrupted", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void LoadMap(String mapPath){
        try{
            blocks = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(mapPath));
            String line;
            int y = 0;
            while((line = reader.readLine()) != null){
                for(int i = 0; i< line.length(); i++){
                    if(line.charAt(i) == '#'){
                        Block block = new Block("block",i * 16,y * 16);
                        blocks.add(block);
                    }if(line.charAt(i) == '.'){
                        Block block = new Block("dot",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == 'S'){
                        Block block = new Block("pacman",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == '0'){
                        Block block = new Block("blinky",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == '1'){
                        Block block = new Block("pinky",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == '2'){
                        Block block = new Block("clyde",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == '3'){
                        Block block = new Block("inky",i * 16,y * 16);
                        blocks.add(block);
                    }
                    if(line.charAt(i) == 'o'){
                        Block block = new Block("strawberry",i * 16,y * 16);
                        blocks.add(block);
                    }
                }
                y++;
            }
            reader.close();
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "File Not Found", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(null, "IO Exception", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
