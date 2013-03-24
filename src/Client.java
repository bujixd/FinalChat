import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class Client extends JFrame {

	private PrintWriter ps;
	private BufferedReader br;
	private String line = null;
	// Text area for entering server text
	  private JTextArea jtaServer = new JTextArea();

	  // Text area for displaying client text
	  private JTextArea jtaClient = new JTextArea();

	public static void main(String[] args) {
		Client client = new Client();
		client.communication();
	}

	public Client(){
	    getContentPane().setLayout(new GridLayout(2, 1));
	    JScrollPane jScrollPane1 = new JScrollPane(jtaServer);
	    JScrollPane jScrollPane2 = new JScrollPane(jtaClient);
	    jScrollPane1.setBorder(new TitledBorder("Server"));
	    jScrollPane2.setBorder(new TitledBorder("Client"));
	    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
	    getContentPane().add(jScrollPane2, BorderLayout.CENTER);

	    jtaServer.setWrapStyleWord(true);
	    jtaServer.setLineWrap(true);
	    jtaClient.setWrapStyleWord(true);
	    jtaClient.setLineWrap(true);
//	    jtaClient.setEditable(false);
	    setTitle("Client Chat");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!
	}
	
	
	public void communication() {
		try {
			Socket socket = new Socket("localhost", 9999);

			Runnable writeThreadHandler = new WriteThreadHandler(socket);
			Thread writeThread = new Thread(writeThreadHandler);
			writeThread.start();

			Runnable readThreadHandler = new ReadThreadHandler(socket);
			Thread readThread = new Thread(readThreadHandler);
			readThread.start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	class WriteThreadHandler implements Runnable {
		Socket socket = null;

		public WriteThreadHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			
			jtaClient.addKeyListener(new KeyAdapter() {
			      public void keyPressed(KeyEvent e) {
			        if (e.getKeyCode() == e.VK_ENTER) {
			         line = jtaClient.getText().trim();
			     
			         
							try {
								ps = new PrintWriter(socket.getOutputStream());
								if(!"".equals(line)){
								ps.println(line);
								ps.flush();
								jtaServer.append("Client: "+ new Date()+"\n"+line+"\n");
								jtaClient.setText("");
								}
								if("bye".equals(line))
									System.exit(0);
							
								}
							 catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							
			        }
			      
			} 
		});
	}
}


	class ReadThreadHandler implements Runnable {
		private Socket socket = null;

		public ReadThreadHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				String output = "";
				while (!"bye".equals(output)) {
					InputStreamReader isr = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(isr);
					output = br.readLine();
					
					jtaServer.append("Server: "+new Date()+"\n"+output+"\n");	
					
				}
				JOptionPane.showMessageDialog(null,
						"server have been quit");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}