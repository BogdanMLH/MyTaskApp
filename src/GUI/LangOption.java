package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import static GUI.Buttons.properties;

public class LangOption {
    public static void addLangOption(Connection conn) throws SQLException, IOException {

        Statement statement = conn.createStatement();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "lang", null);

        //Check if table exist
        if (!resultSet.next()) {
            statement.executeQuery("CREATE TABLE lang(rem VARCHAR(2))");
            statement.executeQuery("INSERT INTO lang(rem) VALUE (\"NO\")");
        }

        //Founding properties
        FileInputStream fisRU = null;
        try {
            fisRU = new FileInputStream("C:\\Users\\mrmon\\Documents\\IT_Projects\\Java\\MyTaskApp\\src\\Properties\\MyTasksAppRU.properties");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        FileInputStream fisEN = null;
        try {
            fisEN = new FileInputStream("C:\\Users\\mrmon\\Documents\\IT_Projects\\Java\\MyTaskApp\\src\\Properties\\MyTasksAppEN.properties");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        FileInputStream fisUR = null;
        try {
            fisUR = new FileInputStream("C:\\Users\\mrmon\\Documents\\IT_Projects\\Java\\MyTaskApp\\src\\Properties\\MyTasksAppUR.properties");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        resultSet = statement.executeQuery("SELECT * FROM lang");
        resultSet.next();
        String result = resultSet.getString(1);

        //Checking if priority lang is saved
        if(result.equals("NO")) {

            //Main font
            Font font1 = new Font("SEGOE SCRIPT", Font.BOLD, 20);
            Font font2 = new Font("SEGOE SCRIPT", Font.BOLD, 15);

            //CheckBox settings
            JCheckBox checkBox = new JCheckBox("Save my choise");
            checkBox.setBackground(new Color(255, 221, 117));
            checkBox.setFont(font2);

            //Label Settings
            JLabel label = new JLabel("Choose language: ");
            label.setFont(font1);
            label.setBorder(new EmptyBorder(0,50,0,0));

            //Buttons settings
            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton ENButton = new JButton("English");
            ENButton.setFont(new Font("Segoe print", Font.BOLD, 15));
            ENButton.setBackground(new Color(255, 183, 0));

            JButton RUButton = new JButton("Russian");
            RUButton.setFont(new Font("Segoe print", Font.BOLD, 15));
            RUButton.setBackground(new Color(255, 183, 0));

            JButton URButton = new JButton("Ukrainian");
            URButton.setFont(new Font("Segoe print", Font.BOLD, 15));
            URButton.setBackground(new Color(255, 183, 0));

            buttonPanel.add(ENButton);
            buttonPanel.add(RUButton);
            buttonPanel.add(URButton);

            //Main dialog
            JDialog dialog = new JDialog();
            dialog.setResizable(false);
            Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.png");
            dialog.setBackground(new Color(255, 221, 117));
            dialog.setIconImage(icon);
            dialog.setTitle("Language");
            dialog.setModal(true);
            dialog.add(label, BorderLayout.NORTH);
            dialog.add(buttonPanel, BorderLayout.CENTER);
            dialog.add(checkBox, BorderLayout.SOUTH);
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(null);

            FileInputStream finalFisEN = fisEN;
            FileInputStream finalFisRU = fisRU;
            FileInputStream finalFisUR = fisUR;

            //Actions for English option
            ActionListener ENAction = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            properties.load(finalFisEN);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        //Checking and saving choise
                        if(checkBox.isSelected()){
                            String sqlLang = "UPDATE lang SET rem = \"EN\"";
                            try {
                                statement.executeUpdate(sqlLang);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        dialog.dispose();
                    }
                };
            ENButton.addActionListener(ENAction);

            //Actions for Russian option
            RUButton.addActionListener(e -> {
                try {
                    properties.load(finalFisRU);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                if(checkBox.isSelected()){
                    String sqlLang = "UPDATE lang SET rem = \"RU\"";
                    try {
                        statement.executeUpdate(sqlLang);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                dialog.dispose();
            });

            //Actions for Ukrainian option
            URButton.addActionListener(e -> {
                try {
                    properties.load(finalFisUR);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                if(checkBox.isSelected()){
                    String sqlLang = "UPDATE lang SET rem = \"UR\"";
                    try {
                        statement.executeUpdate(sqlLang);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                dialog.dispose();
            });

            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        ENAction.actionPerformed(null);
                    }
            });

            dialog.setVisible(true);
        }else{
            if(result.equals("RU"))
                properties.load(fisRU);
            if(result.equals("EN"))
                properties.load(fisEN);
            if(result.equals("UR"))
                properties.load(fisUR);
        }
    }
}
