package net.team214.debugging.paxos.tests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient implements Runnable {
	private InetSocketAddress address;
	private String msg;

	public TCPClient(InetSocketAddress address,String msg) {
		this.address = address;
		this.msg = msg;
	}

	public void send() {

		try {
			Socket socket = new Socket(address.getAddress(), address.getPort());
			InputStream ips = socket.getInputStream();
			OutputStream ops = socket.getOutputStream();

			DataOutputStream dos = new DataOutputStream(ops);
			BufferedReader brNet = new BufferedReader(
					new InputStreamReader(ips));
			dos.writeBytes(msg + System.getProperty("line.separator"));
			System.out.println("-Client reads:" + brNet.readLine());
			dos.close();
			brNet.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		send();
	}
}
