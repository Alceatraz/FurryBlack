package studio.blacktech.coolqbot.furryblack;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;


public class SocketServer {

	private ServerSocket server;

	@Test
	void server() throws IOException {
		server = new ServerSocket(57781);
		while (true) {
			try {
				Socket socket = server.accept();
				DataInputStream read = new DataInputStream(socket.getInputStream());
				DataOutputStream send = new DataOutputStream(socket.getOutputStream());
				String temp = read.readUTF();
				System.out.println("Server - " + temp);
				send.writeUTF(temp);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
}
