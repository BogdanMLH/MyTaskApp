package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Link {
    public static void addLink(MainWindow mainWindow){
        JPanel panel = new JPanel();

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(new EmptyBorder(0,0,80,0));

        JLabel label = new JLabel("<html><u>©BohdanMLH</u></html>");
        label.setToolTipText(Buttons.getProperties().getProperty("My_link"));
        label.setOpaque(true);
        Font font = new Font("SEGOE SCRIPT", Font.PLAIN, 15);
        label.setFont(font);
        Color linkForeground = new Color(238,239, 233);
        label.setForeground(linkForeground);
        label.setForeground(Color.GRAY);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                //Color enterLinkColor = new Color(255, 183, 0);
                //label.setForeground(enterLinkColor);
                panel.setCursor(cursor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.GRAY);
                panel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/BogdanMLH"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        };

        label.addMouseListener(mouseListener);

        panel.add(label);

        mainWindow.getContentPane().add(emptyPanel);
        mainWindow.getContentPane().add(panel);
        mainWindow.pack();
    }
}
