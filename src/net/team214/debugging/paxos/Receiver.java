package net.team214.debugging.paxos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import net.team214.debugging.paxos.packet.Packet;

public class Receiver implements Runnable {
	private NodeInfo me;
	ServerSocket server = null;
	Socket sk = null;
	BufferedReader rdr = null;
	PrintWriter wtr = null;
	PacketHandler handler = null;
	private Boolean switcher;

	public Receiver(NodeInfo me, PacketHandler handler) {
		this.me = me;
		this.handler = handler;
		try {
			server = new ServerSocket(this.me.getIp().getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void turnOn() {
		this.switcher = true;
	}

	public synchronized void turnOff() {
		this.switcher = false;
	}

	public void run() {
		turnOn();
		while (switcher) {
			System.out.println("Listenning...");
			try {
				sk = server.accept();
				System.out.println("Connected:" + sk.getInetAddress() + ":"
						+ sk.getPort());

				SocketHandler sh = new SocketHandler(sk);
				sh.start();
				// sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	class SocketHandler extends Thread {

		Socket sk = null;

		public SocketHandler(Socket sk) {
			this.sk = sk;
		}

		public void run() {
			try {
				wtr = new PrintWriter(sk.getOutputStream());
				rdr = new BufferedReader(new InputStreamReader(
						sk.getInputStream()));
				String line = rdr.readLine();
				System.out.println("从客户端来的信息：" + line);
				// 特别，下面这句得加上 “\n”,
				wtr.println("你好，服务器已经收到您的信息！'" + line + "'\n");
				wtr.flush();
				System.out.println("已经返回给客户端！");

				Packet p = new Packet() {

					@Override
					public Type getType() {
						// TODO Auto-generated method stub
						return null;
					}
				};
				handler.handle(p);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}