/**
 *
 * @author SHADAAN HASSAN
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import GUI.FreePortFinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;

public class ServerArea extends JFrame implements ActionListener {	

	// Socket Related
//	public static SimpleDateFormat formatter = new SimpleDateFormat("[MM/hh/yy hh:mm a]");
	public static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm a]");
	private static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
	private static final int MAX_CONNECTED = 50;
	private static int PORT;
	private static ServerSocket server;
	private static volatile boolean exit = false;

	// JFrame related
	private JPanel contentPane;
	private JTextArea txtAreaLogs;
	private JButton btnStart;
	private JLabel lblChatServer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerArea frame = new ServerArea();
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(frame);
					//Logs
					System.setOut(new PrintStream(new TextAreaOutputStream(frame.getTxtAreaLogs())));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerArea() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 470, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		lblChatServer = new JLabel("Chat Server");
		lblChatServer.setIcon(new ImageIcon(getClass().getResource("/images/server.png")));
		lblChatServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatServer.setFont(new Font("Poor Richard", Font.BOLD | Font.ITALIC, 25));
		contentPane.add(lblChatServer, BorderLayout.NORTH);

		btnStart = new JButton(" ");
		btnStart.setIcon(new ImageIcon(getClass().getResource("/images/start.png")));
		btnStart.setBackground(Color.RED);
		btnStart.addActionListener(this);
		btnStart.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 12));
		contentPane.add(btnStart, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		setTxtAreaLogs(new JTextArea());
		txtAreaLogs.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN | Font.ITALIC, 11));
		getTxtAreaLogs().setBackground(Color.BLACK);
		getTxtAreaLogs().setForeground(Color.RED);
		getTxtAreaLogs().setLineWrap(true);
		scrollPane.setViewportView(getTxtAreaLogs());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnStart) {
			if(btnStart.getText().equals(" ")) {
				exit = false;
				getRandomPort();
				start();
				btnStart.setIcon(new ImageIcon(getClass().getResource("/images/stop.png")));
				btnStart.setText("");
			}else {
				addToLogs("Chat Server has been stopped.");
				exit = true;
				btnStart.setIcon(new ImageIcon(getClass().getResource("/images/start.png")));
				btnStart.setText("");
			}
		}
		
		//Refresh UI
		refreshUIComponents();
	}
	
	public void refreshUIComponents() {
		lblChatServer.setText("CHAT SERVER" + (!exit ? ": "+PORT:""));
	}

	public static void start() {
		new Thread(new ServerHandler()).start();
	}

	public static void stop() throws IOException {
		if (!server.isClosed()) server.close();
	}

	private static void broadcastMessage(String message) {
		for (PrintWriter p: connectedClients.values()) {
			p.println(message);
		}
	}
	
	public static void addToLogs(String message) {
		System.out.printf("%s %s\n", formatter.format(new Date()), message);
	}

	private static int getRandomPort() {
		int port = FreePortFinder.findFreeLocalPort();
		PORT = port;
		return port;
	}
	
	public JTextArea getTxtAreaLogs() {
		return txtAreaLogs;
	}

	public void setTxtAreaLogs(JTextArea txtAreaLogs) {
		this.txtAreaLogs = txtAreaLogs;
	}

	private static class ServerHandler implements Runnable{
		@Override
		public void run() {
			try {
				server = new ServerSocket(PORT);
				addToLogs("Server started on port: " + PORT);
				addToLogs("Looking for users...");
				while(!exit) {
					if (connectedClients.size() <= MAX_CONNECTED){
						new Thread(new ClientHandler(server.accept())).start();
					}
				}
			}
			catch (Exception e) {
				addToLogs("\nError occured: \n");
				addToLogs(Arrays.toString(e.getStackTrace()));
				addToLogs("\nExiting...");
			}
		}
	}
	
	// Start of Client Handler
	public static class ClientHandler implements Runnable {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
		private String name;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run(){
			addToLogs("Client connected: " + socket.getInetAddress());
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				for(;;) {
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (connectedClients) {
						if (!name.isEmpty() && !connectedClients.keySet().contains(name)) break;
						else out.println("INVALID NAME");
					}
				}
				out.println("Welcome to the chat group, " + name.toUpperCase() + "!");
				addToLogs(name.toUpperCase() + " has joined.");
				broadcastMessage("[SYSTEM] " + name.toUpperCase() + " has joined.");
				connectedClients.put(name, out);
				String message;
				out.println("You may join the chat now...");
				while ((message = in.readLine()) != null && !exit) {
					if (!message.isEmpty()) {
						if (message.toLowerCase().equals("/quit")) break;
						broadcastMessage(String.format("[%s] %s", name, message));
					}
				}
			} catch (Exception e) {
				addToLogs(e.getMessage());
			} finally {
				if (name != null) {
					addToLogs(name.toUpperCase() + " has left");
					connectedClients.remove(name);
					broadcastMessage(name.toUpperCase() + " has left");
				}
			}
		}
	}

}