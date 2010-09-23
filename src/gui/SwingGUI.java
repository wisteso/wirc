package gui;
import static data.Constants.*;
import core.Manager;
import data.ServerChannel;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Default GUI object
 * <br><br>
 * This class handles the input and output from the user 
 * via the java swing class set and is not critical to 
 * the core operation.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class SwingGUI implements ActionListener, MouseListener
{
	private static final String OUTPUT = "OUTPUT_TXT";
	private static final String SEND = "SEND_BUTTON";

	private final Map<ServerChannel, ChannelNode> chanTabs;

	private final SwingGUI me = this;
	private final Manager mgr;

	private JFrame frame;
	private Container mainPane;
	private JTabbedPane tabs;
	private JButton sendButton;
	private JTextField txtOut;
	private JPanel inputPane;
	
	private boolean isReading;

	private String title;
	private ImageIcon icon;
	
	public SwingGUI(Manager mgr)
	{
		this.mgr = mgr;
		
		this.title = "wIRC";
		
		this.chanTabs = new HashMap<ServerChannel, ChannelNode>();

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
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("assets/main_icon_16.png"));
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	
		frame.setMinimumSize(new Dimension(325, 280));
		frame.setIconImage(icon.getImage());
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				frame.dispose();

				mgr.disconnectAll("user termination");

				mgr.running = false;
			}
		});
		
		txtOut = new JTextField();
		txtOut.setActionCommand(OUTPUT);
		txtOut.addActionListener(this);
		
		sendButton = new JButton();
		sendButton.setMaximumSize(new Dimension(75, 25));
		sendButton.setMinimumSize(new Dimension(75, 25));
		sendButton.setText("SEND");
		sendButton.setActionCommand(SEND);
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
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getModifiers() & 4) != 0)
				{
					JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
					int tab = tabbedPane.indexAtLocation(e.getX(), e.getY());
					
					if (tab > 0)
					{
						ServerChannel target = ServerChannel.parseServerChan(tabs.getTitleAt(tab));
						// TODO: target should be server only, not a serverchannel

						mgr.sendMessage("/PART " + target.channel, target);
					}
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
	
	public static String askQuestion(final String query, final String defaultAnswer)
	{
		final StringBuilder answer = new StringBuilder();
		
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					String ans = JOptionPane.showInputDialog(query, defaultAnswer);
					
					if (ans != null) 
						answer.append(ans);
				}
			});
		}
		catch (Exception e)
		{
			//e.printStackTrace(System.err);
			
			return askQuestion(query, defaultAnswer);
		}
		
		if (answer.length() > 0)
			return answer.toString();
		
		return null;
	}
	
	public ServerChannel getFocusedChat()
	{
		return ServerChannel.parseServerChan(tabs.getTitleAt(tabs.getSelectedIndex()));
	}
	
	public void setFocusedChat(final ServerChannel sChan)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				int index = tabs.indexOfTab(sChan.toString());
				
				if (index > -1)
					tabs.setSelectedIndex(index);
				else
					mgr.printDebugMsg("Cannot find channel to focus: " + sChan);
			}
		});
	}
	
	public void focusInput()
	{
		// TODO: Make me work!! Java bug maybe?

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Focus: " + txtOut.requestFocusInWindow());
			}
		});
	}

	private static class ChannelNode
	{
		public ChannelNode(ServerChannel sChan)
		{
			this(sChan.server, sChan.channel);
		}

		public ChannelNode(String server, String channel)
		{
			serverName = server;
			channelName = channel;

			if (channel.startsWith(("#")))
				isPublicChat = true;
			else
				isPublicChat = false;
		}

		public final boolean isPublicChat;

		public final String channelName;
		public final String serverName;
		
		public JTextField chatAddress;
		public JEditorPane chatPane;
		public SortedListModel chatMembers;
	}
	
	public boolean removeChat(ServerChannel sChan)
	{
		final int index = tabs.indexOfTab(sChan.toString());

		if (index > 0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					chanTabs.remove(title.toLowerCase());
					tabs.remove(index);
				}
			});

			return true;
		}

		if (index != 0)
			mgr.printDebugMsg("Tab does not exist.");

		return false;
	}

	public void addChat(final ServerChannel sChan) throws Exception
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				addChatUnsafely(sChan);
			}
		});
	}

	private void addChatUnsafely(ServerChannel sChan)
	{
		ChannelNode node = new ChannelNode(sChan.server, sChan.channel);
		node.chatAddress = new JTextField("irc://" + node.serverName + "/" + node.channelName);
		node.chatAddress.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(0, 0, 5, 0), node.chatAddress.getBorder()));
		node.chatPane = new JEditorPane();
		node.chatPane.setContentType("text/rtf");
		node.chatPane.setFont(new Font("Arial", Font.PLAIN, 10));
		node.chatPane.setEditable(false);
		node.chatPane.addMouseListener(me);

		JScrollPane t2 = new JScrollPane(node.chatPane);
		t2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		t2.getVerticalScrollBar().addMouseListener(me);

		JPanel t6 = new JPanel();
		t6.setLayout(new BorderLayout());
		t6.add(node.chatAddress, BorderLayout.PAGE_START);
		t6.add(t2, BorderLayout.CENTER);
			t6.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(""),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)),
					t6.getBorder()));

		if (node.isPublicChat)
		{
			node.chatMembers = new SortedListModel();
			node.chatMembers.addListDataListener(new ListDataListener()
			{
				public void intervalRemoved(ListDataEvent e) { }

				public void intervalAdded(ListDataEvent e) { }

				public void contentsChanged(ListDataEvent e)
				{
					frame.repaint();
				}
			});

			JList t4 = new JList(node.chatMembers);
			t4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			t4.setSelectedIndex(0);
			t4.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent evt)
				{
					if (evt.getClickCount() > 1)
					{
						// TODO: make this work with multiserver

//						String selNick = ((JList)evt.getSource()).getSelectedValue().toString();
//
//						char firstChar = selNick.charAt(0);
//
//						if (firstChar == '@' || firstChar == '+' || firstChar == '%')
//							selNick = selNick.substring(1);
//
//						addChatUnsafely(selNick);
//						setFocusedChat(selNick);
//						focusInput();
					}
				}
			});

			JScrollPane t5 = new JScrollPane(t4);
			t5.setPreferredSize(new Dimension(100, -1));
			t5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			t6.add(t5, BorderLayout.LINE_END);
		}

		tabs.addTab(sChan.toString(), t6);

		chanTabs.put(sChan, node);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		String action = e.getActionCommand();

		if (action.equals(OUTPUT) || action.equals(SEND))
		{
			if (txtOut.getText().length() > 0)
			{
				ServerChannel target = ServerChannel.parseServerChan(tabs.getTitleAt(tabs.getSelectedIndex()));
				
				mgr.sendMessage(txtOut.getText(), target);
				
				txtOut.setText("");
			}
		}
	}
	
	/* * * * * * * * * * * * * * * *
	 * OUTPUT METHODS & CONSTANTS  *
	 * * * * * * * * * * * * * * * */

	/* normal attributes */
	private static SimpleAttributeSet BLACK = new SimpleAttributeSet();
	private static SimpleAttributeSet GRAY = new SimpleAttributeSet();
	private static SimpleAttributeSet RED = new SimpleAttributeSet();
	private static SimpleAttributeSet ORANGE = new SimpleAttributeSet();
	private static SimpleAttributeSet YELLOW = new SimpleAttributeSet();
	private static SimpleAttributeSet GREEN = new SimpleAttributeSet();
	private static SimpleAttributeSet BLUE = new SimpleAttributeSet();
	private static SimpleAttributeSet BLUEGRAY = new SimpleAttributeSet();
	private static SimpleAttributeSet VIOLET = new SimpleAttributeSet();
	/* bold attributes */
	private static SimpleAttributeSet BLACK_BOLD = new SimpleAttributeSet();
	private static SimpleAttributeSet BLUE_BOLD = new SimpleAttributeSet();
	
	static
	{
		StyleConstants.setFontFamily(BLACK, "Monospace");
		StyleConstants.setFontSize(BLACK, 11);
		StyleConstants.setFontFamily(BLACK_BOLD, "Monospace");
		StyleConstants.setFontSize(BLACK_BOLD, 11);
		StyleConstants.setBold(BLACK_BOLD, true);
		
		GRAY.addAttributes(BLACK);
		RED.addAttributes(BLACK);
		ORANGE.addAttributes(BLACK);
		YELLOW.addAttributes(BLACK);
		GREEN.addAttributes(BLACK);
		BLUE.addAttributes(BLACK);
		BLUEGRAY.addAttributes(BLACK);
		VIOLET.addAttributes(BLACK);
		
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
	
	public void println(String input, TextColor style)
	{
		print("\n" + input, CONSOLE, style);
	}
	
	public void println(String input, ServerChannel channel, TextColor style)
	{
		print("\n" + input, channel, style);
	}

	public void print(String input, TextColor style)
	{
		print(input, CONSOLE, style);
	}
	
	public void print(final String input, final ServerChannel sChan, final TextColor style)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (!chanTabs.containsKey(sChan))
				{
					addChatUnsafely(sChan);
				}

				JEditorPane textLog = chanTabs.get(sChan).chatPane;
				Document doc = textLog.getDocument();

				try
				{
					doc.insertString(doc.getLength(), input, textColorToSwing(style));
				}
				catch (Exception e)
				{
					mgr.printDebugMsg("Error printing: " + e.toString());
				}
				
				if (!isReading)
				{
					textLog.setCaretPosition(doc.getLength());
				}
			}
		});
	}

	public SimpleAttributeSet textColorToSwing(TextColor color)
	{
		switch(color)
		{
			case BLACK: return BLACK;
			case BLACK_BOLD: return BLACK_BOLD;
			case GRAY: return GRAY;
			case RED: return RED;
			case ORANGE: return ORANGE;
			case YELLOW: return YELLOW;
			case GREEN: return GREEN;
			case BLUE: return BLUE;
			case BLUE_BOLD: return BLUE_BOLD;
			case BLUEGRAY: return BLUEGRAY;
			case VIOLET: return VIOLET;

 			default: return GRAY;
		}
	}
	
	/* * * * * * * * * * * * *
	 * NICKNAME LIST METHODS *
	 * * * * * * * * * * * * */
	
	public void addNicks(final ServerChannel sChan, final String... nicks)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					SortedListModel nickList = chanTabs.get(sChan).chatMembers;

					for (int x = 0; x < nicks.length; ++x)
					{
						//assert(nickList.contains(nicks[x]) == false);

						nickList.addElement(nicks[x]);
					}
				}
				catch (Exception ex)
				{
					mgr.printDebugMsg("ListModel not found to add nick: " + sChan);
				}
			}
		});
	}
	
	public void removeNicks(final ServerChannel sChan, final String nick)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				SortedListModel list = chanTabs.get(sChan).chatMembers;

				try
				{
					int index = list.indexOf(nick);

					list.remove(index);
				}
				catch (Exception ex)
				{
					mgr.printDebugMsg("Chan " + sChan + " not found to remove nick: " + nick);
				}
			}
		});
	}
	
	public ServerChannel[] replaceNick(final String oldNick, final String newNick)
	{
		final ArrayList<ServerChannel> channels = new ArrayList<ServerChannel>();

		SwingUtilities.invokeLater(new Runnable()	// should be invoke and wait?
		{
			public void run()
			{
				ChannelNode node;

				for (ServerChannel key : chanTabs.keySet())
				{
					node = chanTabs.get(key);
					if (node.isPublicChat)
					{
						SortedListModel list = node.chatMembers;

						int index = list.indexOf(oldNick);

						if (index > -1)
						{
							list.remove(index);

							if (newNick != null)
								list.addElement(newNick);

							channels.add(key);
						}
					}
				}
			}
		});

		return channels.toArray(new ServerChannel[channels.size()]);
	}

	public ServerChannel[] removeNick(String nick)
	{
		return replaceNick(nick, null);
	}
	
	public SortedListModel getNickList(ServerChannel channel)
	{
		return chanTabs.get(channel).chatMembers;
	}
	
	/* * * * * * * * * * * * * *
	 * MOUSE LISTENING METHODS *
	 * * * * * * * * * * * * * */
	
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