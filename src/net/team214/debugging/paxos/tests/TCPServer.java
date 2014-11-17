package net.team214.debugging.paxos.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.team214.debugging.paxos.NodeInfo;
import net.team214.debugging.paxos.PacketHandler;
import net.team214.debugging.paxos.packet.AcceptRequest;
import net.team214.debugging.paxos.packet.Packet;

public class TCPServer implements Runnable, CallBack {
	private NodeInfo me;
	private ServerSocket server = null;
	private Socket sk = null;
	private BufferedReader rdr = null;
	private PrintWriter wtr = null;
	private PacketHandler handler = null;
	private boolean switcher;
	private int maxThreadCount;
	private volatile int threadCount = 0;
	private int timeout = 7000;
	private Lock countLock;

	public TCPServer(NodeInfo me, PacketHandler handler, int MTC) {
		this.me = me;
		this.handler = handler;
		this.maxThreadCount = MTC;
		countLock = new ReentrantLock();
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
		if (!switcher) {
			return;
		}
		this.switcher = false;
		try {
			System.out.println("+Stopping...");
			server.close();
			server = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		if (server == null) {
			System.out.println("+Error Listenning...");
			return;
		}
		turnOn();
		System.out.println("+Listenning...");
		while (switcher) {
			try {
				sk = server.accept();
				System.out.println("+Connected:" + sk.getInetAddress() + ":"
						+ sk.getPort());

				countLock.lock();
				threadCount++;
				countLock.unlock();
				while (threadCount > maxThreadCount) {
					System.out.println("+wait0..." + threadCount);
					synchronized (this) {
						System.out.println("+wait1...");
						this.wait(timeout);
						System.out.println("+wait2...");
					}
					System.out.println("+wait3...");
				}
				SocketHandler sh = new SocketHandler(sk, this);
				sh.start();
			} catch (Exception e) {
				if (!switcher) {
					System.out.println("+Stopped...");
					return;
				}
				e.printStackTrace();
			}
		}
	}

	class SocketHandler extends Thread {

		CallBack c = null;
		Socket sk = null;

		public SocketHandler(Socket sk, CallBack c) {
			this.sk = sk;
			this.c = c;
		}

		public void run() {
			try {
				wtr = new PrintWriter(sk.getOutputStream());
				rdr = new BufferedReader(new InputStreamReader(
						sk.getInputStream()));
				String line = rdr.readLine();
				System.out.println("*Server reads£º" + line);
				try {
					if (Integer.parseInt(line) > 1)
						Thread.sleep(1000);
					else
						Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wtr.println("bye\n");
				wtr.flush();
				sk.close();

				Packet p = new AcceptRequest(line);
				handler.handle(p);
				sk.close();
				c.done();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws InterruptedException {
		Scanner sc = new Scanner(System.in);

		NodeInfo node = new NodeInfo("10.1.15.0:1234");

		TCPServer s = new TCPServer(node, new MyHandler(), 2);
		Thread server = new Thread(s);
		server.start();

		System.out.println("::Server start");
		int i = 0;
		while (true) {
			String cmd = "";
			if (i > 12) {
				cmd = sc.nextLine();
				if ("stop".equals(cmd)) {
					break;
				}
			} else {
				cmd = ++i + "";
				Thread.sleep(2000);
			}
			System.out.println("::cmd:" + cmd);
			new Thread(new TCPClient(node.getIp(), cmd)).start();
		}
		s.turnOff();
		sc.close();
		server.join();
		System.out.println("::Server down");
	}

	static class MyHandler implements PacketHandler {

		@Override
		public void handle(Packet p) {
			System.out.println("-->" + p);
		}

	}

	@Override
	public void done() {
		countLock.lock();
		this.threadCount--;
		countLock.unlock();
		synchronized (this) {
			this.notify();
			System.out.println("+notify..." + threadCount);
		}
	}

}
