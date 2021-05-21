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

import GUI.ClientArea.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
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
import java.awt.FlowLayout;
import javax.swing.JTextField;

public class ClientArea extends JFrame implements ActionListener {	

	// Socket Related
	private static Socket clientSocket;
	private static int PORT;
	private PrintWriter out;

	// JFrame related
	private JPanel contentPane;
	private JTextArea txtAreaLogs;
	private JButton btnCONNECT;
	private JButton btnStranger;
	private JPanel panelNorth;
	private JLabel lblChatClient;
	private JPanel panelNorthSouth;
	private JLabel lblPort;
	private JLabel lblName;
	private JPanel panelSouth;
	private JButton btnSend;
	private JTextField txtMessage;
	private JTextField txtUsername;
	private JTextField txtPort;
	private String clientName;
	private String strangername= "Stranger";
	private JLabel lblOR;
	private JLabel lblNewLabel;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientArea frame = new ClientArea();
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
	public ClientArea() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 570, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelNorth = new JPanel();
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BorderLayout(0, 0));

		lblChatClient = new JLabel("Chat Corner");
		lblChatClient.setIcon(new ImageIcon(getClass().getResource("/images/chat.png")));
		lblChatClient.setBackground(Color.ORANGE);
		lblChatClient.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatClient.setFont(new Font("Poor Richard", Font.BOLD | Font.ITALIC, 35));
		panelNorth.add(lblChatClient, BorderLayout.NORTH);

		panelNorthSouth = new JPanel();
		panelNorth.add(panelNorthSouth, BorderLayout.SOUTH);
		panelNorthSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
				lblPort = new JLabel("Port");
				lblPort.setHorizontalAlignment(SwingConstants.LEFT);
				lblPort.setFont(new Font("Times New Roman", Font.BOLD, 12));
				panelNorthSouth.add(lblPort);
		
				txtPort = new JTextField();
				txtPort.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						if (txtPort.getText().equals("*Required*"))
						{
							txtPort.setText("");
							txtPort.setForeground(Color.BLACK);
							
						}
					}
					@Override
					public void focusLost(FocusEvent e) {
						if (txtPort.getText().equals(""))
						{
							txtPort.setText("*Required*");
							txtPort.setForeground(new Color(153, 153, 153));
						}
					}
				});
				
				txtPort.setForeground(Color.GRAY);
				txtPort.setBackground(Color.WHITE);
				txtPort.setText("*Required*");
				panelNorthSouth.add(txtPort);
				txtPort.setColumns(7);
				
				lblNewLabel = new JLabel("|");
				lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
				lblNewLabel.setBackground(Color.GRAY);
				lblNewLabel.setForeground(Color.RED);
				lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
				panelNorthSouth.add(lblNewLabel);
				
				lblName = new JLabel("Username");
				lblName.setFont(new Font("Times New Roman", Font.BOLD, 12));
				panelNorthSouth.add(lblName);

		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		panelNorthSouth.add(txtUsername);

		btnCONNECT = new JButton("CONNECT");
		btnCONNECT.setIcon(new ImageIcon(getClass().getResource("/images/connect.png")));
		panelNorthSouth.add(btnCONNECT);
		btnCONNECT.addActionListener(this);
		btnCONNECT.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 8));
		
		lblOR = new JLabel("OR");
		lblOR.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelNorthSouth.add(lblOR);
		
		btnStranger = new JButton("Ghost Login");
		btnStranger.setIcon(new ImageIcon(getClass().getResource("/images/ghost.png")));
		panelNorthSouth.add(btnStranger);
		btnStranger.addActionListener(this);
		btnStranger.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 8));
		

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		txtAreaLogs = new JTextArea();
		txtAreaLogs.setEditable(false);
		txtAreaLogs.setFont(new Font("Goudy Old Style", Font.PLAIN, 13));
		txtAreaLogs.setBackground(new Color(250, 250, 210));
		txtAreaLogs.setForeground(Color.BLACK);
		txtAreaLogs.setLineWrap(true);
		scrollPane.setViewportView(txtAreaLogs);

		panelSouth = new JPanel();
		FlowLayout fl_panelSouth = (FlowLayout) panelSouth.getLayout();
		fl_panelSouth.setAlignment(FlowLayout.RIGHT);
		contentPane.add(panelSouth, BorderLayout.SOUTH);

		txtMessage = new JTextField();
		txtMessage.setBackground(new Color(255, 255, 240));
		txtMessage.setForeground(new Color(0, 0, 0));
		panelSouth.add(txtMessage);
		txtMessage.setColumns(50);

		btnSend = new JButton("SEND");
		btnSend.setIcon(new ImageIcon(getClass().getResource("/images/send.png")));
		btnSend.addActionListener(this);
		btnSend.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 8));
		panelSouth.add(btnSend);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		if(e.getSource() == btnCONNECT) {
			if(btnCONNECT.getText().equals("CONNECT")) {
				btnCONNECT.setIcon(new ImageIcon(getClass().getResource("/images/disconnect.png")));
				btnCONNECT.setText("Disconnect");
				CONNECT();
				
			}
			
			else {
				btnCONNECT.setIcon(new ImageIcon(getClass().getResource("/images/connect.png")));
				btnCONNECT.setText("CONNECT");
				Disconnect();
				String Username=txtUsername.getText().trim();
				String Portname=txtPort.getText().trim();
				if(!Username.isEmpty()) {
					out.println();
					txtUsername.setText("");
				}
				if(!Portname.isEmpty()) {
					out.println();
					txtPort.setText("");
				}
			}
		}
		
		if(e.getSource() == btnStranger) {
			if(btnStranger.getText().equals("Ghost Login")) {
				btnStranger.setIcon(new ImageIcon(getClass().getResource("/images/disconnect.png")));
				btnStranger.setText("Disconnect");
				STRANGER();
			}else {
				btnStranger.setIcon(new ImageIcon(getClass().getResource("/images/ghost.png")));
				btnStranger.setText("Ghost Login");
				Disconnect();
			}
		}
		
		else if(e.getSource() == btnSend) {
			String message = txtMessage.getText().trim();
			if(!message.isEmpty()) {
				out.println(message);
				txtMessage.setText("");
			}
		}
		
		
		//Refresh UI
		refreshUIComponents();
	}

	public void refreshUIComponents() {
		lblChatClient.setText("User" + ( ": "+clientName+""));
		lblChatClient.setForeground(new Color(0, 191, 255));
	}

	public void CONNECT() {
		try {
			PORT = Integer.parseInt(txtPort.getText().trim());
			clientName = txtUsername.getText().trim();
			clientSocket = new Socket("localhost", PORT);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			new Thread(new Listener()).start();
			//send name
			out.println(clientName);
		} catch (Exception err) {
			addToLogs("[ERROR] "+err.getLocalizedMessage());
		}
	}
	

	public void STRANGER() {
		try {
			PORT = Integer.parseInt(txtPort.getText().trim());
			clientName = strangername.trim();
			clientSocket = new Socket("localhost", PORT);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			new Thread(new Listener()).start();
			//send name
			out.println(clientName);
		} catch (Exception err) {
			addToLogs("[ERROR] "+err.getLocalizedMessage());
		}
	}
	
	
	public void Disconnect(){
		if(!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e1) {}
		}
	}

	public static void addToLogs(String message) {
		System.out.printf("%s %s\n", ServerArea.formatter.format(new Date()), message);
	}

	public JTextArea getTxtAreaLogs() {
		return txtAreaLogs;
	}

	public void setTxtAreaLogs(JTextArea txtAreaLogs) {
		this.txtAreaLogs = txtAreaLogs;
	}

	public static class Listener implements Runnable {
		private BufferedReader in;
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String read;
				for(;;) {
					read = in.readLine();
					if (read != null && !(read.isEmpty())) addToLogs(read);
				}
			} catch (IOException e) {
				return;
			}
		}

	}
}