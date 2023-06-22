package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class Buttons {
    final static Properties properties = new Properties();
    static class Task{
        String name;
        int importance;
    }
    private static Task task;
    private static String order = "number";

    public static Properties getProperties() {
        return properties;
    }

    public static void addButtons(MainWindow mainWindow, Connection conn) throws SQLException {

        //Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Setting task menu
        CustomTableModel model = new CustomTableModel();
        model.addColumn(properties.getProperty("Number"));
        model.addColumn(properties.getProperty("Task"));
        model.addColumn(properties.getProperty("Importance"));
        model.addColumn("id");

        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setPreferredScrollableViewportSize(new Dimension(380, 200));
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        Color tableColor = new Color(250, 235, 215);
        table.setBackground(tableColor);

        setTable(conn, model, table);

        //Making id column invisible
        TableColumn columnId = table.getColumnModel().getColumn(3);
        columnId.setMinWidth(0);
        columnId.setMaxWidth(0);
        columnId.setWidth(0);
        columnId.setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panelPane = new JPanel();

        //Setting text field
        JTextField enterText = new JTextField();
        enterText.setToolTipText(properties.getProperty("Type_task"));
        enterText.setPreferredSize(new Dimension(320, 30));
        enterText.setMaximumSize(new Dimension(320, 30));
        enterText.setMinimumSize(new Dimension(320, 30));
        enterText.setBorder(BorderFactory.createLineBorder(
                new Color(152, 203, 255), 2, true));

        //Helping text in text field
        enterText.setUI(new BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                super.paintSafely(g);
                if (enterText.getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.drawString(properties.getProperty("Enter_task"), 5, 20);
                    g2.dispose();
                }
            }
        });

        //Adding radio buttons
        JRadioButton urgent = new JRadioButton(properties.getProperty("Urgent"));
        urgent.setFont(new Font("Segoe print", Font.BOLD, 15));
        JRadioButton important = new JRadioButton(properties.getProperty("Important"));
        important.setFont(new Font("Segoe print", Font.BOLD, 15));
        JRadioButton optional = new JRadioButton(properties.getProperty("Optional"));
        optional.setFont(new Font("Segoe print", Font.BOLD, 15));

        //Radio buttons comparing
        JPanel radioButtons = new JPanel();

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(urgent);
        buttonGroup.add(important);
        buttonGroup.add(optional);

        radioButtons.add(urgent);
        radioButtons.add(important);
        radioButtons.add(optional);

        //Adding button
        JButton addTask = new JButton(properties.getProperty("AddTask"));
        Dimension addTaskSize = new Dimension(130, 45);
        addTask.setPreferredSize(addTaskSize);
        addTask.setToolTipText(properties.getProperty("Click_for_add"));
        addTask.setFont(new Font("Segoe print", Font.BOLD, 15));
        addTask.setOpaque(true);
        Color backColorAdd = new Color(255, 183, 0);
        addTask.setBackground(backColorAdd);
        addTask.setForeground(Color.WHITE);
        addTask.addActionListener(e -> {

            //Checking input
            if(enterText.getText().isEmpty()){
                JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningText"),
                        properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(enterText.getText().length() > 60){
                JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningTextLength"),
                        properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(!(urgent.isSelected()||important.isSelected()||optional.isSelected())){
                JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningImp"),
                        properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Creating a task object
            task = new Task();
            task.name = enterText.getText();
            enterText.setText("");
            if(urgent.isSelected()){
                task.importance = 2;
                urgent.setSelected(false);
            }else if(important.isSelected()){
                task.importance = 1;
                important.setSelected(false);
            }else if(optional.isSelected()){
                task.importance = 0;
                optional.setSelected(false);
            }

            //Working with database
            try {

                //Sending task to database
                String sql = "INSERT INTO tasks (name, importance) Values (?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, task.name);
                preparedStatement.setInt(2, task.importance);
                preparedStatement.executeUpdate();

                //Requesting data from database
                model.setRowCount(0);
                setTable(conn, model, table);

            } catch(Exception ex){
                System.out.println("Connection failed...");
            }
        });
        
        //Clicking on button when ENTER
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                0), "enterPressed");
        panel.getActionMap().put("enterPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask.doClick();
            }
        });

        //Delete-All button
        JButton deleteAllTasks = new JButton(properties.getProperty("DeleteAll"));
        deleteAllTasks.setToolTipText(properties.getProperty("Click_for_delete_all"));
        deleteAllTasks.setFont(new Font("Segoe print", Font.BOLD, 14));
        deleteAllTasks.setOpaque(true);
        deleteAllTasks.setBackground(Color.RED);
        deleteAllTasks.setForeground(Color.WHITE);
        deleteAllTasks.addActionListener(e -> {
            int result2 = JOptionPane.showOptionDialog(null,
                    properties.getProperty("You_sure?"), properties.getProperty("Delete_all_tasks"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new String[]{properties.getProperty("Yes"), properties.getProperty("Back")},
                    properties.getProperty("Back"));
            if(result2 == 0){
                try {
                    Statement statement = conn.createStatement();
                    statement.executeUpdate("DROP TABLE tasks");
                    statement.executeUpdate("CREATE TABLE tasks(id INT " +
                            "PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(20) NOT NULL, importance INT NOT NULL)");
                    model.setRowCount(0);
                    setTable(conn, model, table);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Delete task button
        JButton deleteTask = new JButton(properties.getProperty("DeleteTask"));
        Dimension deleteTaskSize = new Dimension(130, 45);
        deleteTask.setPreferredSize(deleteTaskSize);
        deleteTask.setToolTipText(properties.getProperty("Click_for_delete"));
        deleteTask.setFont(new Font("Segoe print", Font.BOLD, 15));
        deleteTask.setOpaque(true);
        Color backColorDelete = new Color(252, 66, 66);
        deleteTask.setBackground(backColorDelete);
        deleteTask.setForeground(Color.WHITE);
        deleteTask.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length != 0) {
                try {
                    int result = JOptionPane.showOptionDialog(null,
                            properties.getProperty("Delete_this_task?"), properties.getProperty("Delete_task"),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{properties.getProperty("Yes"), properties.getProperty("Back")},
                            properties.getProperty("Back"));

                    for (int selectedRow : selectedRows) {
                        int id = Integer.parseInt(table.getValueAt(selectedRow, 3).toString());
                        if (result == 0) {
                            String sql = "DELETE FROM tasks WHERE id = ?";
                            PreparedStatement preparedStatement = conn.prepareStatement(sql);
                            preparedStatement.setInt(1, id);
                            preparedStatement.executeUpdate();
                        }
                    }
                    model.setRowCount(0);
                    setTable(conn, model, table);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                JOptionPane.showMessageDialog(null,
                        properties.getProperty("Choose_task_you_want_to_delete"),
                        properties.getProperty("DeleteTask"),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        //Order button
        JButton orderTask = new JButton(properties.getProperty("Sort_by_importance"));
        Dimension orderTaskSize = new Dimension(200, 40);
        orderTask.setPreferredSize(orderTaskSize);
        orderTask.setToolTipText(properties.getProperty("Click_for_order"));
        orderTask.setFont(new Font("Segoe print", Font.BOLD, 14));
        orderTask.setOpaque(true);
        Color backColorOrder = new Color(119, 136, 153);
        orderTask.setBackground(backColorOrder);
        orderTask.setForeground(Color.WHITE);
        orderTask.addActionListener(e ->{
            JButton button = (JButton) e.getSource();
            if(Objects.equals(order, "id")){
                order = "number";
                model.setRowCount(0);
                try {
                    setTable(conn, model, table);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                button.setText(properties.getProperty("Sort_by_importance"));
            }else{
                order = "id";
                model.setRowCount(0);
                try {
                    setTable(conn, model, table);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                button.setText(properties.getProperty("Sort_by_number"));
            }

        });

        //Edit button
        JButton changeTask = new JButton(properties.getProperty("Change_task"));
        Dimension changeTaskSize = new Dimension(130, 45);
        changeTask.setPreferredSize(changeTaskSize);
        changeTask.setToolTipText(properties.getProperty("Click_for_change"));
        changeTask.setFont(new Font("Segoe print", Font.BOLD, 15));
        changeTask.setOpaque(true);
        Color backColorChange = new Color(100, 149, 237);
        changeTask.setBackground(backColorChange);
        changeTask.setForeground(Color.WHITE);
        changeTask.addActionListener(e -> {

            //Check if row got selected
            int[] task = table.getSelectedRows();
            if(task.length == 1){

                //General variables
                JDialog dialog = new JDialog();
                Color colorChangeWindow = new Color(255, 221, 117);

                //New text field
                JTextField enterChangeText = new JTextField();
                enterChangeText.setColumns(20);
                enterChangeText.setText(table.getValueAt(task[0], 1).toString());
                enterChangeText.setToolTipText(properties.getProperty("Type_task"));
                enterChangeText.setPreferredSize(new Dimension(240, 30));
                enterChangeText.setMaximumSize(new Dimension(240, 30));
                enterChangeText.setMinimumSize(new Dimension(240, 30));
                enterChangeText.setBorder(BorderFactory.createLineBorder(
                        new Color(152, 203, 255), 2, true));

                //Adding new radio buttons
                JRadioButton urgentChange = new JRadioButton(properties.getProperty("Urgent"));
                urgentChange.setFont(new Font("Segoe print", Font.BOLD, 15));
                urgentChange.setBackground(colorChangeWindow);

                JRadioButton importantChange = new JRadioButton(properties.getProperty("Important"));
                importantChange.setFont(new Font("Segoe print", Font.BOLD, 15));
                importantChange.setBackground(colorChangeWindow);

                JRadioButton optionalChange = new JRadioButton(properties.getProperty("Optional"));
                optionalChange.setFont(new Font("Segoe print", Font.BOLD, 15));
                optionalChange.setBackground(colorChangeWindow);

                //Radio buttons comparing
                JPanel radioButtonsChange = new JPanel();

                ButtonGroup buttonGroupChange = new ButtonGroup();
                buttonGroupChange.add(urgentChange);
                buttonGroupChange.add(importantChange);
                buttonGroupChange.add(optionalChange);

                radioButtonsChange.add(urgentChange);
                radioButtonsChange.add(importantChange);
                radioButtonsChange.add(optionalChange);

                //Confirm change button
                JButton confirmButton = new JButton(properties.getProperty("Change"));
                confirmButton.setFont(new Font("Segoe print", Font.BOLD, 15));
                confirmButton.setBackground(new Color(255, 183, 0));
                confirmButton.addActionListener(e1 -> {

                    //Checking input
                    if(enterChangeText.getText().isEmpty()){
                        JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningText"),
                                properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if(enterChangeText.getText().length() > 60){
                        JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningTextLength"),
                                properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if(!(urgentChange.isSelected()||importantChange.isSelected()||optionalChange.isSelected())){
                        JOptionPane.showMessageDialog(mainWindow, properties.getProperty("warningImp"),
                                properties.getProperty("Warning"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    //Creating a task object
                    Task taskChange = new Task();
                    taskChange.name = enterChangeText.getText();
                    enterChangeText.setText("");
                    if(urgentChange.isSelected()){
                        taskChange.importance = 2;
                        urgentChange.setSelected(false);
                    }else if(importantChange.isSelected()){
                        taskChange.importance = 1;
                        importantChange.setSelected(false);
                    }else if(optionalChange.isSelected()){
                        taskChange.importance = 0;
                        optionalChange.setSelected(false);
                    }

                    //Working with database
                    try {

                        //Sending task to database
                        String sql = "UPDATE tasks SET name = (?), importance = (?) WHERE  id = (?)";
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1, taskChange.name);
                        preparedStatement.setInt(2, taskChange.importance);
                        int id = Integer.parseInt(table.getValueAt(task[0], 3).toString());
                        preparedStatement.setInt(3, id);
                        preparedStatement.executeUpdate();

                        //Requesting data from database
                        model.setRowCount(0);
                        setTable(conn, model, table);
                        dialog.dispose();

                    } catch(Exception ex){
                        System.out.println("Connection failed...");
                    }

                });

                //Main dialog window
                dialog.setResizable(false);
                Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.png");
                dialog.setBackground(colorChangeWindow);
                dialog.setIconImage(icon);
                dialog.setTitle(properties.getProperty("Change"));
                dialog.setModal(true);
                dialog.add(enterChangeText, BorderLayout.NORTH);
                dialog.add(radioButtonsChange, BorderLayout.CENTER);
                dialog.add(confirmButton, BorderLayout.SOUTH);
                dialog.setSize(400, 250);
                dialog.setLocationRelativeTo(null);

                dialog.setVisible(true);
            }else{
                JOptionPane.showMessageDialog(null,
                        properties.getProperty("Choose_task_you_want_to_edit"),
                        properties.getProperty("Change_task"), JOptionPane.WARNING_MESSAGE);
            }
        });

        //Lang button
        JButton langButton = new JButton();

        //Changing button title
        //Checking what language do we use by checking if some lable equal to its translate
        String language = properties.getProperty("AddTask");
        if(language.equals("Add"))
            langButton.setText(properties.getProperty("Lang_EN"));
        if(language.equals("Додати"))
            langButton.setText(properties.getProperty("Lang_UR"));
        if(language.equals("Добавить"))
            langButton.setText(properties.getProperty("Lang_RU"));

        langButton.setToolTipText(properties.getProperty("Click_for_lang"));
        langButton.setFont(new Font("Segoe print", Font.BOLD, 12));
        Dimension langSize = new Dimension(120, 40);
        langButton.setPreferredSize(langSize);
        langButton.setOpaque(true);
        langButton.setBackground(Color.GRAY);
        langButton.setForeground(Color.WHITE);
        langButton.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(null,
                    properties.getProperty("Change_lang?"), properties.getProperty("Lang_change"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new String[]{properties.getProperty("Yes"), properties.getProperty("Back")},
                    properties.getProperty("Back"));

            if(result == 0){
                String sql = "UPDATE lang SET rem = \"NO\"";
                Statement statement = null;
                try {
                    statement = conn.createStatement();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    statement.executeUpdate(sql);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                JOptionPane.showMessageDialog(null,
                        properties.getProperty("Lang_change_after_restart"),
                        properties.getProperty("Lang_change"), JOptionPane.WARNING_MESSAGE);
            }
        });

        //Buttons panels
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addTask);
        buttons.add(deleteTask);
        buttons.add(changeTask);

        JPanel buttons2 = new JPanel(new FlowLayout());
        buttons2.add(orderTask);
        buttons2.add(deleteAllTasks);

        JPanel buttons3 = new JPanel(new FlowLayout());
        buttons3.add(langButton);

        //Adding all panels to the main panel
        panelPane.add(scrollPane);
        panel.add(enterText);
        panel.add(radioButtons);
        panel.add(buttons);
        panel.add(buttons2);
        panel.add(buttons3);
        panel.setBorder(new EmptyBorder(0,100,0,100));

        //Remove selection by mouse click on empty area
        mainWindow.getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length != 0) {
                    table.removeRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length-1]);
                }
            }
        });

        //Adding all main panels to main window
        mainWindow.getContentPane().add(panelPane);
        mainWindow.getContentPane().add(panel);
        mainWindow.pack();
    }

    //Filling table with the newest data
    private static void setTable(Connection conn, DefaultTableModel model, JTable table) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM tasks");
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count == 0) {
            statement.executeUpdate("DROP TABLE tasks");
            statement.executeUpdate("CREATE TABLE tasks(id INT " +
                    "PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(60) NOT NULL, importance INT NOT NULL)");
        }

        if(Objects.equals(order, "id")){
            resultSet = statement.executeQuery("SELECT * FROM tasks ORDER BY id");
        }else{
            resultSet = statement.executeQuery("SELECT * FROM tasks ORDER BY importance DESC");
        }
        int i = 1;
        while(resultSet.next()){
            if(resultSet.getInt(3) == 0){
                model.addRow(new Object[]{i, resultSet.getString(2),
                        properties.getProperty("Optional"), resultSet.getString(1)});
                i++;
            }
            else if(resultSet.getInt(3) == 1){
                model.addRow(new Object[]{i, resultSet.getString(2),
                        properties.getProperty("Important"), resultSet.getString(1)});
                i++;
            }
            else{
                model.addRow(new Object[]{i, resultSet.getString(2),
                        properties.getProperty("Urgent"), resultSet.getString(1)});
                i++;
            }
        }

        //Setting row height
        for(i = 0; i < table.getRowCount(); i++)
            table.setRowHeight(i, 30);
    }

    static class CustomTableModel extends DefaultTableModel {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Отключаем редактирование всех ячеек в таблице
        }
    }
}
