package studio.blacktech.coolqbot.furryblack;


import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class SocketClient {

	@Test
	void test() throws IOException {

		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 57781);

		Socket socket = new Socket();
		socket.connect(address);

		DataInputStream read = new DataInputStream(socket.getInputStream());
		DataOutputStream send = new DataOutputStream(socket.getOutputStream());

		send.writeUTF("Hello, Socket!");
		System.out.println("Client - " + read.readUTF());

		socket.close();

	}

}
