package GUI;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow() {

        //Setting background picture
        ImageIcon backgroundImage = new ImageIcon("images/paperBackground.png");
        JLabel backgroundLabel = new JLabel(backgroundImage) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(550, 700);
            }
        };
        setContentPane(backgroundLabel);
        setLayout(new FlowLayout());

        //Setting main window
        setTitle("My Tasks✅");
        Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.png");
        setIconImage(icon);

        //Setting main label
        JPanel mainLabel = new JPanel();
        mainLabel.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel("My Tasks");
        Font font = new Font("SEGOE SCRIPT", Font.BOLD, 30);
        label.setFont(font);
        Color color = new Color(220, 20, 60);
        label.setForeground(color);

        //Invisible background
        UIManager.put("Panel.background", new Color(0, 0, 0, 0));

        mainLabel.add(label);

        //Slogan
        JPanel slogan = new JPanel();
        JLabel label1 = new JLabel("Your tasks anywhere, anytime!");
        font = new Font("SEGOE SCRIPT", Font.ITALIC, 19);
        label1.setFont(font);
        slogan.add(label1);

        //Setting main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(mainLabel);
        panel.add(slogan);

        getContentPane().add(panel);
        pack();

        setResizable(false);
        Dimension dimension = new Dimension(550, 700);
        setPreferredSize(dimension);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

