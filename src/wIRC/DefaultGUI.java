package wIRC;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import SortedListModel.*;
import wIRC.interfaces.UserInput;

/**
 * Default GUI object
 * <br><br>
 * This class handles the input and output from the user 
 * via the java swing class set and is not critical to 
 * the core operation.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class DefaultGUI implements UserInput, ActionListener, MouseListener
{
    protected static final String INPUTBOX = "INPUTBOX";
    protected static final String OUTPUTBOX = "OUTPUTBOX";
    protected static final String SENDBUTTON = "SENDBUTTON";
    
    private final DefaultGUI me = this;
    private Manager m;
    
    private ImageIcon icon;
    private String title;
    
    private JFrame frame;
    private Container mainPane;
    private JTabbedPane tabs;
    private JButton sendButton;
    private JTextField txtOut;
    private JPanel inputPane;
    
    private TreeMap<String, JEditorPane> tabList = new TreeMap<String, JEditorPane>();
    private TreeMap<String, SortedListModel> usrList = new TreeMap<String, SortedListModel>();
    
    private boolean isReading = false;
    private static boolean constInit = false;
	
    public DefaultGUI(String server, Manager m)
	{	
    	if (!constInit)
    		initResources();
    	
    	this.m = m;
		
		this.title = "wIRC - " + server;
		
		SwingUtilities.invokeLater(new Runnable()
		{
		    public void run()
		    {
		        createAndShowGUI();
		    }
		});
	}
    
	public void createAndShowGUI()
	{	
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("wIRC/img/main_icon_16.png"));
		
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(325, 280));
        frame.setIconImage(icon.getImage());
        frame.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	frame.dispose();
				
				m.sendData("QUIT :program terminated");
				
				m.disconnect("user termination");
		    }
	    });
        
        txtOut = new JTextField();
        txtOut.setActionCommand(OUTPUTBOX);
        txtOut.addActionListener(this);
        
        sendButton = new JButton();
		sendButton.setMaximumSize(new Dimension(75, 25));
		sendButton.setMinimumSize(new Dimension(75, 25));
		sendButton.setText("SEND");
		sendButton.setActionCommand(SENDBUTTON);
		sendButton.addActionListener(this);
        
        inputPane = new JPanel();
        inputPane.setLayout(new BorderLayout());
        inputPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        inputPane.add(txtOut, BorderLayout.CENTER);
		inputPane.add(sendButton, BorderLayout.LINE_END);
        
		tabs = new JTabbedPane();
		tabs.setPreferredSize(new Dimension(600, 400));
		tabs.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getModifiers() & 4) != 0)
				{
					JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
					int tab = tabbedPane.indexAtLocation(e.getX(), e.getY());
					
					if (tab > 0)
						m.closeChat(tabs.getTitleAt(tab));
				}
			}
		});
		
		mainPane = frame.getContentPane();
		mainPane.add(tabs, BorderLayout.CENTER);
		mainPane.add(inputPane, BorderLayout.PAGE_END);
		
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
        frame.pack();
        frame.setLocation((screen.width / 2 - frame.getWidth() / 2), (screen.height / 2 - frame.getHeight() / 2));
        frame.setVisible(true);
        frame.toFront();
        
        txtOut.requestFocus();
	}
	
	public synchronized String askQuestion(String query, String defaultAnswer)
	{
		return JOptionPane.showInputDialog(query, defaultAnswer);
	}
	
	public void setServerInfo(String newServer)
	{
		frame.setTitle("wIRC - " + newServer);
	}
	
	public String getFocusedChat()
	{
		return tabs.getTitleAt(tabs.getSelectedIndex());
	}
	
	public synchronized void focusChat(final String title)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
		    public void run()
		    {
		    	tabs.setSelectedIndex(tabs.indexOfTab(title));
		    }
		});
	}
	
	public synchronized Object[] addChat(final String title)
	{
		if (tabList.size() < 11)
		{
			final JEditorPane t1 = new JEditorPane();
			final SortedListModel t3 = new SortedListModel();
			
			SwingUtilities.invokeLater(new Runnable()
			{
			    public void run()
			    {
					t1.setContentType("text/rtf");
					t1.setFont(new Font("Arial", Font.PLAIN, 10));
					t1.setEditable(false);
					t1.addMouseListener(me);
						
			        JScrollPane t2 = new JScrollPane(t1);
			        t2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			        t2.getVerticalScrollBar().addMouseListener(me);
			        
			        if (title.charAt(0) == '#')
			        {
			            JList t4 = new JList(t3);
			            t4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			            t4.setSelectedIndex(0);
			            t4.addMouseListener(new MouseAdapter()
			    		{
			    			public void mouseClicked(MouseEvent e)
			    			{
			    	            if (e.getClickCount() == 2)
			    	            {
			    	            	JList list = (JList)e.getSource();
			    	            	
			    	            	String t = list.getSelectedValue().toString();
			    	            	//t = list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
			    	            	
			    	            	char firstChar = t.charAt(0);
			    	            	
			    	            	if (firstChar == '@' || firstChar == '+' || firstChar == '%')
			    	            		t = t.substring(1);
			    	            	
			    	            	Object[] temp = addChat(t);
			    	    			
			    	    			if (temp == null)
			    	    				return;
			    	    			
			    	    			JEditorPane tp = (JEditorPane)temp[0];
			    	    			
			    	    			tabList.put(t.toLowerCase(), tp);
			    	    			
			    	    			if (temp.length > 1)
			    	    			{
			    	    				SortedListModel tl = (SortedListModel)temp[1];
			    	    				
			    	    				tl.update();
			    	    				
			    	    				usrList.put(t.toLowerCase(), tl);
			    	    			}
			    	                
			    	                focusChat(t);
			    	            }
			    			}
			    		});
			            
			            JScrollPane t5 = new JScrollPane(t4);
			            t5.setPreferredSize(new Dimension(100, -1));
			            t5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			            
			            JPanel t6 = new JPanel();
			            t6.setLayout(new BorderLayout());
			            t6.add(t2, BorderLayout.CENTER);
			        	t6.add(t5, BorderLayout.LINE_END);
			        	t6.setBorder(BorderFactory.createCompoundBorder(
			                	BorderFactory.createCompoundBorder(
			                			BorderFactory.createTitledBorder("Data IN"),
			                		BorderFactory.createEmptyBorder(0, 5, 5, 5)),
			                		t6.getBorder()));
			        	
			        	tabs.addTab(title, t6);     	
			        }
			        else
			        {
			        	t2.setBorder(BorderFactory.createCompoundBorder(
			                	BorderFactory.createCompoundBorder(
			                			BorderFactory.createTitledBorder("Data IN"),
			                		BorderFactory.createEmptyBorder(0, 5, 5, 5)),
			                		t2.getBorder()));
			        	
			        	tabs.addTab(title, t2);
			        }
			    }
			});
	        
	        if (title.charAt(0) == '#')
	        {
	        	Object[] x = {t1, t3};
	        	
	        	return x;
	        }
	        else
	        {
	        	Object[] x = {t1};
	        	
	        	return x;
	        }
		}
        
		return null;
	}
	
	public synchronized boolean removeChat(final String title)
	{	
		final int x = tabs.indexOfTab(title);
		
		if (x > 0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
			    public void run()
			    {
					tabList.remove(title.toLowerCase());
					usrList.remove(title.toLowerCase());
					tabs.remove(x);
			    }
			});
			
			return true;
		}
		
		if (x != 0)
			System.err.println("Tab does not exist.");
		
		return false;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
        if (OUTPUTBOX.equals(e.getActionCommand())) 
        {
        	if (txtOut.getText().length() > 0)
        	{
        		m.sendMsg(txtOut.getText(), tabs.getTitleAt(tabs.getSelectedIndex()));
            	
                txtOut.setText("");
        	}
        }
        else if (SENDBUTTON.equals(e.getActionCommand())) 
        {   
        	if (txtOut.getText().length() > 0)
        	{
        		m.sendMsg(txtOut.getText(), tabs.getTitleAt(tabs.getSelectedIndex()));
            	
                txtOut.setText("");
        	}
        }
    }
	
//	OUTPUT CONSTANTS/METHODS
	
	protected static SimpleAttributeSet BLACK = new SimpleAttributeSet();
	protected static SimpleAttributeSet GRAY = new SimpleAttributeSet();
	protected static SimpleAttributeSet RED = new SimpleAttributeSet();
	protected static SimpleAttributeSet ORANGE = new SimpleAttributeSet();
	protected static SimpleAttributeSet YELLOW = new SimpleAttributeSet();
	protected static SimpleAttributeSet GREEN = new SimpleAttributeSet();
	protected static SimpleAttributeSet BLUE = new SimpleAttributeSet();
	protected static SimpleAttributeSet BLUEGRAY = new SimpleAttributeSet();
	protected static SimpleAttributeSet VIOLET = new SimpleAttributeSet();
	
	protected static SimpleAttributeSet BLACK_BOLD = new SimpleAttributeSet();
	protected static SimpleAttributeSet BLUE_BOLD = new SimpleAttributeSet();
	
	public static void initResources()
	{
		constInit = true;
		
		StyleConstants.setFontFamily(BLACK, "Monospace");
		StyleConstants.setFontSize(BLACK, 11);
		StyleConstants.setBold(BLACK_BOLD, true);
		
		GRAY.addAttributes(BLACK);
		RED.addAttributes(BLACK);
		ORANGE.addAttributes(BLACK);
		YELLOW.addAttributes(BLACK);
		GREEN.addAttributes(BLACK);
		BLUE.addAttributes(BLACK);
		BLUEGRAY.addAttributes(BLACK);
		VIOLET.addAttributes(BLACK);
		
		BLACK_BOLD.addAttributes(BLACK);
		BLUE_BOLD.addAttributes(BLACK_BOLD);
		
		StyleConstants.setForeground(BLACK,		Color.getHSBColor(new Float(0.000), new Float(0.000), new Float(0.000)));
		StyleConstants.setForeground(GRAY,		Color.getHSBColor(new Float(0.000), new Float(0.000), new Float(0.666)));
		StyleConstants.setForeground(RED,		Color.getHSBColor(new Float(0.000), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(ORANGE,	Color.getHSBColor(new Float(0.111), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(YELLOW,	Color.getHSBColor(new Float(0.222), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(GREEN,		Color.getHSBColor(new Float(0.333), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(BLUE,		Color.getHSBColor(new Float(0.666), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(BLUEGRAY,	Color.getHSBColor(new Float(0.666), new Float(0.333), new Float(0.777)));
		StyleConstants.setForeground(VIOLET,	Color.getHSBColor(new Float(0.888), new Float(0.666), new Float(0.666)));
		
		StyleConstants.setForeground(BLUE_BOLD,	Color.getHSBColor(new Float(0.666), new Float(0.666), new Float(0.666)));
	}
	
	public synchronized void println(String input, int style)
	{
		print("\n" + input, "Console", style);
	}
	
	public synchronized void println(String input, String channel, int style)
	{
		print("\n" + input, channel, style);
	}
	
	public synchronized void print(final String input, String channel, int style)
	{	
		if (tabList.containsKey(channel.toLowerCase()) == false)
		{
			Object[] temp = addChat(channel);
			
			if (temp == null)
				return;
			
			JEditorPane tp = (JEditorPane)temp[0];
			
			tabList.put(channel.toLowerCase(), tp);
			
			if (temp.length > 1)
			{
				SortedListModel tl = (SortedListModel)temp[1];
				
				tl.update();
				
				usrList.put(channel.toLowerCase(), tl);
			}
		}
					
		final JEditorPane p = tabList.get(channel.toLowerCase());
		
		final SimpleAttributeSet styling;
		
		switch(style)
		{
			case C.BLACK: styling = BLACK; break;
			case C.BLACK_BOLD: styling = BLACK_BOLD; break;
			case C.GRAY: styling = GRAY; break;
			case C.RED: styling = RED; break;
			case C.ORANGE: styling = ORANGE; break;
			case C.YELLOW: styling = YELLOW; break;
			case C.GREEN: styling = GREEN; break;
			case C.BLUE: styling = BLUE; break;
			case C.BLUE_BOLD: styling = BLUE_BOLD; break;
			case C.BLUEGRAY: styling = BLUEGRAY; break;
			case C.VIOLET: styling = VIOLET; break;
			
 			default: styling = GRAY;
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
		    public void run()
		    {
		    	try
				{
					p.getDocument().insertString(p.getDocument().getLength(), input, styling);
				}
				catch (Exception e)
				{
					System.err.println(e.toString());
				}
				
				if (!isReading)
				{
					p.setCaretPosition(p.getDocument().getLength());
				}
		    }
		});
	}
	
//	NICK LIST METHODS
	
	public synchronized void addNicks(String channel, String... nicks)
	{
		SortedListModel l = usrList.get(channel.toLowerCase());
		
		if (l != null)
		{
			for (int x = 0; x < nicks.length; ++x)
			{
				if (!l.contains(nicks[x]))
					l.add(nicks[x]);
			}
		}
		else
			System.err.println("ListModel not found to add nick: " + channel);
	}
	
	public synchronized void removeNicks(String channel, String... nicks)
	{
		SortedListModel l = usrList.get(channel.toLowerCase());
		
		if (l != null)
		{
			int i;
			
			for (int x = 0; x < nicks.length; ++x)
			{
				i = l.indexOf(nicks[x]);
				
				if (i > -1)
					l.remove(i);
				else
					System.err.println(nicks[x] + " not found in ListModel.");
			}
		}
		else
			System.err.println("Chan not found to remove nick: " + channel);
	}
	
	public synchronized void removeNick(String nick)
	{
		replaceNick(nick, null);
	}
	
	public synchronized void replaceNick(String oldNick, String newNick)
	{
		Iterator<SortedListModel> iter = usrList.values().iterator();
		
		while (iter.hasNext())
		{
			SortedListModel l = iter.next();
			
			if (l != null)
			{
				int i = l.indexOf(oldNick);
				
				if (i > -1)
				{
					l.remove(i);
					
					if (newNick != null) l.add(newNick);
				}
				else
					System.err.println(oldNick + " not found in ListModel.");
			}
		}
	}
	
	public SortedListModel getNickList(String channel)
	{
		return usrList.get(channel);
	}
	
//	MOUSELISTENER METHODS
	
	public void mouseClicked(MouseEvent e)
	{
		if (frame.isActive())
		{
			isReading = true;
			frame.setTitle(title + " (reading)");
		}
	}
	
	public void mouseEntered(MouseEvent e)
	{
		if (frame.isActive())
		{
			isReading = true;
			frame.setTitle(title + " (reading)");
		}
	}
	
	public void mouseExited(MouseEvent e)
	{
		isReading = false;
		frame.setTitle(title);
	}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}
}