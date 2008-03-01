package wIRC;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <b>GUI Launcher</b>
 * <br><br>
 * This class gives the user a GUI based form for 
 * entering initial connection/nick/etc data.
 * 
 * @author wisteso@gmail.com
 * @deprecated
 */
public class Launcher implements ActionListener
{
	private JFrame frame;
	
	private JTextField txtHost;
	private JTextField txtNick;
	private JTextField txtReal;
	private JButton connect;
	
	public void actionPerformed(ActionEvent e) 
	{
        if ("SEND".equals(e.getActionCommand())) 
        {
        	String[] opts = {txtHost.getText() , txtNick.getText(), txtReal.getText()};
    		
        	if (opts[0].length() < 3)
        		txtHost.requestFocus();
        	else if (opts[1].length() < 1)
        		txtNick.requestFocus();
        	else if (opts[2].length() < 1)
        		txtReal.requestFocus();
        	else
	        	try
	    		{
	    			// Enter code to run wIRC.jar + opts
	        		System.exit(0);
	    		}
	    		catch (Exception x)
	    		{
	    			System.exit(1);
	    		}
        }
    }
	
	public Launcher()
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
		catch (Exception ex)
		{
            System.out.println("Cannot find resources for default style interface.");
        }
		
		txtHost = new JTextField("chat.freenode.net");
		txtNick = new JTextField("wIRC-Newbie");
		txtReal = new JTextField("Anonymous");
		connect = new JButton("Connect");
		
		frame = new JFrame("Open connection...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300, 100));
        
        txtHost.setPreferredSize(new Dimension(180, 20));
        txtNick.setPreferredSize(new Dimension(180, 20));
        txtReal.setPreferredSize(new Dimension(180, 20));
        
        JPanel hostPane = new JPanel();
        hostPane.setLayout(new BorderLayout());
        hostPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        hostPane.add(new JLabel("Host name: "), BorderLayout.LINE_START);
        hostPane.add(txtHost, BorderLayout.CENTER);
        
        JPanel nickPane = new JPanel();
        nickPane.setLayout(new BorderLayout());
        nickPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        nickPane.add(new JLabel("Nick name: "), BorderLayout.LINE_START);
        nickPane.add(txtNick, BorderLayout.CENTER);
        
        JPanel realPane = new JPanel();
        realPane.setLayout(new BorderLayout());
        realPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        realPane.add(new JLabel("Real name: "), BorderLayout.LINE_START);
        realPane.add(txtReal, BorderLayout.CENTER);
        
        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.add(hostPane, BorderLayout.PAGE_START);
        input.add(nickPane, BorderLayout.CENTER);
        input.add(realPane, BorderLayout.PAGE_END);
        input.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        
        connect.addActionListener(this);
        connect.setActionCommand("SEND");

        JPanel connectPane = new JPanel();
        connectPane.setBorder(BorderFactory.createEmptyBorder(0,50,10,50));
        connectPane.add(connect);

        content.add(input, BorderLayout.PAGE_START);
        content.add(connectPane, BorderLayout.PAGE_END);

        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        
        frame.pack();
        frame.setResizable(false);
        frame.setLocation((screen.width / 2 - frame.getWidth() / 2), (screen.height / 2 - frame.getHeight() / 2));
        frame.setVisible(true);
        frame.toFront();
        
        connect.requestFocus();
	}
	
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Launcher();
            }
        });
	}
}
