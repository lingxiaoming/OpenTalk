/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.azfn.opentalk.network.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class OpenTalkSocket extends DatagramSocket {
	public OpenTalkSocket(int port) throws SocketException, UnknownHostException {
		super(port);
	}

	public void close() {
		super.close();
	}

	public void setSoTimeout(int val) throws SocketException {
		super.setSoTimeout(val);
	}

	public void receive(DatagramPacket pack) throws IOException {
		super.receive(pack);
	}

	public void send(DatagramPacket pack) throws IOException {
		super.send(pack);
	}

	public boolean isConnected() {
		return super.isConnected();
	}

	public void disconnect() {
		super.disconnect();
	}

	public void connect(InetAddress addr,int port) {
		super.connect(addr,port);
	}
}
