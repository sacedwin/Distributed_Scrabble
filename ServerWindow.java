import javax.swing.*;
import java.awt.*;

public class ServerWindow extends JFrame{

    private JPanel mainPanel;
    public JTextArea txtAreaStatus;
    private JScrollPane scroll;
    private JLabel lblStatus;
    private JLabel lblUserCount;
    private JLabel lblGameCount;

    public ServerWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 700);
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(234, 242,255));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "SCRABBLE   SERVER", 2,0,
                new Font("Zapfino", Font.BOLD, 30), Color.BLACK));
        setContentPane(mainPanel);
        GridBagLayout gbl_contentPaneUserLogin = new GridBagLayout();
        gbl_contentPaneUserLogin.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPaneUserLogin.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPaneUserLogin.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPaneUserLogin.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        mainPanel.setLayout(gbl_contentPaneUserLogin);


        lblUserCount = new JLabel("User Count: ");
        lblUserCount.setFont(new Font("Lucida Calligraphy", Font.ITALIC, 22));
        GridBagConstraints gbc_lblUserCount = new GridBagConstraints();
        gbc_lblUserCount.fill = GridBagConstraints.WEST;
        gbc_lblUserCount.gridwidth = 3;
        gbc_lblUserCount.insets = new Insets(0, 50, 5, 0);
        gbc_lblUserCount.gridx = 1;
        gbc_lblUserCount.gridy = 3;
        mainPanel.add(lblUserCount, gbc_lblUserCount);

        lblGameCount = new JLabel("Game Count: ");
        lblGameCount.setFont(new Font("Lucida Calligraphy", Font.ITALIC, 22));
        GridBagConstraints gbc_lblGameCount = new GridBagConstraints();
        gbc_lblGameCount.fill = GridBagConstraints.EAST;
        gbc_lblGameCount.gridwidth = 3;
        gbc_lblGameCount.insets = new Insets(0, 0, 5, 0);
        gbc_lblGameCount.gridx = 10;
        gbc_lblGameCount.gridy = 3;
        mainPanel.add(lblGameCount, gbc_lblGameCount);

        lblStatus = new JLabel("Status..");
        lblStatus.setFont(new Font("Lucida Calligraphy", Font.ITALIC, 20));
        GridBagConstraints gbc_lblStatus = new GridBagConstraints();
        gbc_lblStatus.fill = GridBagConstraints.WEST;
        gbc_lblStatus.gridwidth = 3;
        gbc_lblStatus.insets = new Insets(0, 50, 5, 0);
        gbc_lblStatus.gridx = 1;
        gbc_lblStatus.gridy = 4;
        mainPanel.add(lblStatus, gbc_lblStatus);

        txtAreaStatus = new JTextArea("(づ｡◕‿‿◕｡)づ Welcome Administrator~");
        txtAreaStatus.setFont(new Font("Palatino Linotype",0,22));
        scroll = new JScrollPane(txtAreaStatus);
        GridBagConstraints gbc_scroll= new GridBagConstraints();
        gbc_scroll.fill = GridBagConstraints.BOTH;
        gbc_scroll.gridwidth = GridBagConstraints.REMAINDER;
        gbc_scroll.gridheight = GridBagConstraints.REMAINDER;
        gbc_scroll.insets = new Insets(0, 50, 20, 50);
        gbc_scroll.gridx = 3;
        gbc_scroll.gridy = 5;
        mainPanel.add(scroll, gbc_scroll);

    }

    public void setLblUserCount(String userCount) {
        lblUserCount.setText("User Count: "+ userCount);
    }

    public void setLblGameCount(String gameCount) {
        lblGameCount.setText("Game Count: "+ gameCount);
    }

    public void setTxtAreaStatus(String status) {
        txtAreaStatus.append("\r\n"+ status);
    }


//    public static void main(String arg[]){
//
//        ServerWindow serverWindow = new ServerWindow();
//        serverWindow.setVisible(true);
//    }



}
