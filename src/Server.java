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
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;



public class Server extends JFrame {
	ServerSocket serverSocket ;
	Socket clientSocket ;
	Socket socket ;
	private PrintWriter ps;
	private BufferedReader br;
	private String line = null ;
	
	private JTextArea jtaServer = new JTextArea();
	private JTextArea jtaClient = new JTextArea();



	public static void main(String[] args) {

		Server server = new Server();
		server.communicate();
	}

	public Server(){
		 // Place text area on the frame
	    getContentPane().setLayout(new GridLayout(2, 1));
	    JScrollPane jScrollPane1 = new JScrollPane(jtaServer);
	    JScrollPane jScrollPane2 = new JScrollPane(jtaClient);
	    jScrollPane1.setBorder(new TitledBorder("Server"));
	    jScrollPane2.setBorder(new TitledBorder("Client"));
	    getContentPane().add(jScrollPane2, BorderLayout.CENTER);
	    getContentPane().add(jScrollPane1, BorderLayout.CENTER);

	    jtaServer.setWrapStyleWord(true);
	    jtaServer.setLineWrap(true);
	    jtaClient.setWrapStyleWord(true);
	    jtaClient.setLineWrap(true);

	    setTitle("Server Chat");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!
	}
	
	
	public void communicate() {
		try {
			serverSocket = new ServerSocket(9999);
			while (true) {
				socket = serverSocket.accept();
				Runnable threadHandler = new ThreadHandler(socket);
				Thread thread = new Thread(threadHandler);
				thread.start();

				Runnable readThreadHandler = new ReadThreadHandler(socket);
				Thread readThread = new Thread(readThreadHandler);
				readThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	class ThreadHandler implements Runnable {
		private Socket socket;

		public ThreadHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
				
					jtaServer.addKeyListener(new KeyAdapter() {
					      public void keyPressed(KeyEvent e) {
					        if (e.getKeyCode() == e.VK_ENTER) {
					         line = jtaServer.getText().trim();
			
					          
									try {
										
										ps = new PrintWriter(socket.getOutputStream());
										if(!"".equals(line)){
										ps.println(line);
										ps.flush();
										jtaClient.append("Server: "+ new Date()+"\n"+line+"\n");
										jtaServer.setText("");
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
				while(!"bye".equals(output))
				{
					InputStreamReader isr = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(isr);
					output = br.readLine();
					
					jtaClient.append("Client: "+new Date()+"\n"+output+"\n");	
	
				}
				JOptionPane.showMessageDialog(null,
						"client have been quit");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
	}
	
}
	