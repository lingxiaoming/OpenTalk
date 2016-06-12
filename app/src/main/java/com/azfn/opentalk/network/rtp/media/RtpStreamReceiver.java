/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
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

package com.azfn.opentalk.network.rtp.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.azfn.opentalk.TalkApplication;
import com.azfn.opentalk.network.rtp.CallState;
import com.azfn.opentalk.network.rtp.Codecs.Codecs;
import com.azfn.opentalk.network.rtp.OpenTalkSocket;
import com.azfn.opentalk.network.rtp.RtpPacket;
import com.azfn.opentalk.network.rtp.RtpSocket;

import java.io.IOException;
import java.net.SocketException;

/**
 * RtpStreamReceiver is a generic stream receiver. It receives packets from RTP
 * and writes them into an OutputStream.
 */
public class RtpStreamReceiver extends Thread {

	/** Whether working in debug mode. */
	public static boolean DEBUG = true;

	/** Payload type */
	Codecs codecs;

	static String codec = "";//编解码的title

	/** Size of the read buffer */
	public static final int BUFFER_SIZE = 1024;

	/** Maximum blocking time, spent waiting for reading new bytes [milliseconds] */
	public static final int SO_TIMEOUT = 1000;

	/** The RtpSocket */
	RtpSocket rtp_socket = null;

	/** Whether it is running */
	boolean running;
	AudioManager am;
	public static int speakermode = -1;
	CallRecorder call_recorder = null;
	
	/**
	 * Constructs a RtpStreamReceiver.
	 * 
	 * @param socket
	 *            the local receiver OpenTalkSocket
	 */
	public RtpStreamReceiver(OpenTalkSocket socket, Codecs codecs, CallRecorder rec) {
		init(socket);
		this.codecs = codecs;
		call_recorder = rec;
	}

	/** Inits the RtpStreamReceiver */
	private void init(OpenTalkSocket socket) {
		if (socket != null)
			rtp_socket = new RtpSocket(socket);
	}

	/** Whether is running */
	public boolean isRunning() {
		return running;
	}

	/** Stops running */
	public void halt() {
		running = false;
	}

	public int speaker(int mode) {
		int old = speakermode;
		
		if (mode == old)
			return old;
		setCodec();
		return old;
	}

	static int oldvol = -1;
	
	static int stream() {
		return speakermode == AudioManager.MODE_IN_CALL?AudioManager.STREAM_VOICE_CALL:AudioManager.STREAM_MUSIC;
	}
	

	double smin = 200,s;
	public static int nearend;
	
	void calc(short[] lin,int off,int len) {
		int i,j;
		double sm = 30000,r;
		
		for (i = 0; i < len; i += 5) {
			j = lin[i+off];
			s = 0.03*Math.abs(j) + 0.97*s;
			if (s < sm) sm = s;
			if (s > smin) nearend = 6000*mu/5;
			else if (nearend > 0) nearend--;
		}
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			if (j > 6550)
				lin[i+off] = 6550*5;
			else if (j < -6550)
				lin[i+off] = -6550*5;
			else
				lin[i+off] = (short)(j*5);
		}
		r = (double)len/(100000*mu);
		if (sm > 2*smin || sm < smin/2)
			smin = sm*r + smin*(1-r);
	}
	
	void calc2(short[] lin,int off,int len) {
		int i,j;
		
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			if (j > 16350)
				lin[i+off] = 16350<<1;
			else if (j < -16350)
				lin[i+off] = -16350<<1;
			else
				lin[i+off] = (short)(j<<1);
		}
	}
	
	static long down_time;
	
	public static void adjust(int keyCode,boolean down) {
        AudioManager mAudioManager = (AudioManager) TalkApplication.getInstance().getSystemService(
                Context.AUDIO_SERVICE);
        
		if (RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL)
			if (down ^ mAudioManager.getStreamVolume(stream()) == 0)
				mAudioManager.setStreamMute(stream(), down);
		if (down && down_time == 0)
			down_time = SystemClock.elapsedRealtime();
		if (!down ^ RtpStreamReceiver.speakermode != AudioManager.MODE_NORMAL)
			if (SystemClock.elapsedRealtime()-down_time < 500) {
				if (!down)
					down_time = 0;
				if (ogain > 1)
					if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
						if (gain != ogain) {
							gain = ogain;
							return;
						}
						if (mAudioManager.getStreamVolume(stream()) ==
							mAudioManager.getStreamMaxVolume(stream())) return;
						gain = ogain/2;
					} else {
						if (gain == ogain) {
							gain = ogain/2;
							return;
						}
						if (mAudioManager.getStreamVolume(stream()) == 0) return;
						gain = ogain;
					}
		        mAudioManager.adjustStreamVolume(
		                    stream(),
		                    keyCode == KeyEvent.KEYCODE_VOLUME_UP
		                            ? AudioManager.ADJUST_RAISE
		                            : AudioManager.ADJUST_LOWER,
		                    AudioManager.FLAG_SHOW_UI);
			}
		if (!down)
			down_time = 0;
	}

	static void setStreamVolume(final int stream,final int vol,final int flags) {
        (new Thread() {
			public void run() {
				AudioManager am = (AudioManager) TalkApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
				am.setStreamVolume(stream, vol, flags);
				if (stream == stream()) restored = true;
			}
        }).start();
	}
	
	static boolean restored;
	static float gain,ogain;

	public static int getMode() {
		AudioManager am = (AudioManager) TalkApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
		if (Integer.parseInt(Build.VERSION.SDK) >= 5)
			return am.isSpeakerphoneOn()?AudioManager.MODE_NORMAL:AudioManager.MODE_IN_CALL;
		else
			return am.getMode();
	}

	public static float good, late, lost, loss, loss2;
	double avgheadroom,devheadroom;
	int avgcnt;
	public static int timeout;
	int seq;
	
	void empty() {
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(1);
			for (;;)
				rtp_socket.receive(rtp_packet);
		} catch (SocketException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
		}
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(SO_TIMEOUT);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		seq = 0;
	}
	
	RtpPacket rtp_packet;
	AudioTrack track;
	int maxjitter,minjitter,minjitteradjust;
	int cnt,cnt2,user,luser,luser2,lserver;
	public static int jitter,mu;
	
	void setCodec() {
		synchronized (this) {
			AudioTrack oldtrack;

			codecs.codec.init();
			codec = codecs.codec.getTitle();
			mu = codecs.codec.samp_rate()/8000;
			maxjitter = AudioTrack.getMinBufferSize(codecs.codec.samp_rate(),
					AudioFormat.CHANNEL_CONFIGURATION_MONO, 
					AudioFormat.ENCODING_PCM_16BIT);
			if (maxjitter < 2*2*BUFFER_SIZE*6*mu)
				maxjitter = 2*2*BUFFER_SIZE*6*mu;
			oldtrack = track;
			track = new AudioTrack(stream(), codecs.codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
					maxjitter*2, AudioTrack.MODE_STREAM);
			maxjitter /= 2*2;
			minjitter = minjitteradjust = 500*mu;
			jitter = 875*mu;
			devheadroom = Math.pow(jitter/5, 2);
			timeout = 1;
			luser = luser2 = -8000*mu;
			cnt = cnt2 = user = lserver = 0;
			if (oldtrack != null) {
				oldtrack.stop();
				oldtrack.release();
			}
		}
	}
	
	void write(short a[],int b,int c) {
		synchronized (this) {
			user += track.write(a,b,c);
		}
	}

	void newjitter(boolean inc) {
		 if (good == 0 || lost/good > 0.01 || call_recorder != null)
			 return;
		 int newjitter = (int)Math.sqrt(devheadroom)*7 + (inc?minjitteradjust:0);
		 if (newjitter < minjitter)
			 newjitter = minjitter;
		 if (newjitter > maxjitter)
			 newjitter = maxjitter;
		 if (!inc && (Math.abs(jitter-newjitter) < minjitteradjust || newjitter >= jitter))
			 return;
		 if (inc && newjitter <= jitter)
			 return;
		 jitter = newjitter;
		 late = 0;
		 avgcnt = 0;
		 luser2 = user;
	}
	
	/** Runs it in a new Thread. */
	public void run() {

		if (rtp_socket == null) {
			if (DEBUG)
				println("ERROR: RTP socket is null");
			return;
		}

		byte[] buffer = new byte[BUFFER_SIZE+12];
		rtp_packet = new RtpPacket(buffer, 0);

		if (DEBUG)
			println("Reading blocks of max " + buffer.length + " bytes");

		running = true;
		restored = false;

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		am = (AudioManager) TalkApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
		if (oldvol == -1) oldvol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		setCodec();
		short lin[] = new short[BUFFER_SIZE];
		short lin2[] = new short[BUFFER_SIZE];
		int server, headroom, todo, len = 0, m = 1, expseq, getseq, vm = 1, gap, gseq;
		track.play();
		System.gc();
		empty();
		while (running) {
			if (CallState.call_state == CallState.UA_STATE_HOLD) {
				track.pause();
				while (running && CallState.call_state == CallState.UA_STATE_HOLD) {
					try {
						sleep(1000);
					} catch (InterruptedException e1) {
					}
				}
				track.play();
				System.gc();
				timeout = 1;
				luser = luser2 = -8000*mu;
			}
			try {
				rtp_socket.receive(rtp_packet);
				if (timeout != 0) {
					track.pause();
					for (int i = maxjitter*4; i > 0; i -= BUFFER_SIZE)
						write(lin2,0,i>BUFFER_SIZE?BUFFER_SIZE:i);
					cnt += maxjitter*2;
					track.play();
					empty();
				}
				timeout = 0;
			} catch (IOException e) {
				rtp_socket.getDatagramSocket().disconnect();
				if (++timeout > 60) {
//					Receiver.engine(Receiver.mContext).rejectcall();
					//TODO 关闭，拒绝来电
					break;
				}
			}
			if (running && timeout == 0) {		
				 gseq = rtp_packet.getSequenceNumber();
				 if (seq == gseq) {
					 m++;
					 continue;
				 }
				 gap = (gseq - seq) & 0xff;
				 if (gap > 240)
					 continue;
				 server = track.getPlaybackHeadPosition();
				 headroom = user-server;
				 
				 if (headroom > 2*jitter)
					 cnt += len;
				 else
					 cnt = 0;
				 
				 if (lserver == server)
					 cnt2++;
				 else
					 cnt2 = 0;

				 if (cnt <= 500*mu || cnt2 >= 2 || headroom - jitter < len ||
						 codecs.codec.number() != 8 || codecs.codec.number() != 0) {
					 len = codecs.codec.decode(buffer, lin, rtp_packet.getPayloadLength());
					 
					 // Call recording: Save incoming.
					 // Data is in buffer lin, from 0 to len.
					 if (call_recorder != null)
					 	call_recorder.writeIncoming(lin, 0, len);
					 
		 			 if (speakermode == AudioManager.MODE_NORMAL)
		 				 calc(lin,0,len);
		 			 else if (gain > 1)
		 				 calc2(lin,0,len);
				 }
				 
				 if (cnt == 0)
					 avgheadroom = avgheadroom * 0.99 + (double)headroom * 0.01;
				 if (avgcnt++ > 300)
					 devheadroom = devheadroom * 0.999 + Math.pow(Math.abs(headroom - avgheadroom),2) * 0.001;

				 if (headroom < 250*mu) { 
	 				 late++;
	 				 avgcnt += 10;
	 				 if (avgcnt > 400)
	 					 newjitter(true);
					 todo = jitter - headroom;
					 write(lin2,0,todo>BUFFER_SIZE?BUFFER_SIZE:todo);
				 }

				 if (cnt > 500*mu && cnt2 < 2) {
					 todo = headroom - jitter;
					 if (todo < len)
						 write(lin,todo,len-todo);
				 } else
					 write(lin,0,len);
				 				 
				 if (seq != 0) {
					 getseq = gseq&0xff;
					 expseq = ++seq&0xff;
					 if (m == RtpStreamSender.m) vm = m;
					 gap = (getseq - expseq) & 0xff;
					 if (gap > 0) {
						 if (gap > 100) gap = 1;
						 loss += gap;
						 System.out.println("debug packet lost");
						 lost += gap;
						 good += gap - 1;
						 loss2++;
					 } else {
						 if (m < vm) {
							 loss++;
							 loss2++;
						 }
					 }
					 good++;
					 if (good > 110) {
						 good *= 0.99;
						 lost *= 0.99;
						 loss *= 0.99;
						 loss2 *= 0.99;
						 late *= 0.99;
					 }
				 }
				 m = 1;
				 seq = gseq;

				 if (user >= luser + 8000*mu && (
						 CallState.call_state == CallState.UA_STATE_INCALL ||
								 CallState.call_state == CallState.UA_STATE_OUTGOING_CALL)) {
					 if (luser == -8000*mu || getMode() != speakermode) {
					 }
					 luser = user;
					 if (user >= luser2 + 160000*mu)
						 newjitter(false);
				 }
				 lserver = server;
			}
		}
		track.stop();
		track.release();
		am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
		oldvol = -1;
		codecs.codec.close();
		rtp_socket.close();
		rtp_socket = null;
		codec = "";
		
		// Call recording: stop incoming receive.
		if (call_recorder != null)
		{
			call_recorder.stopIncoming();
			call_recorder = null;
		}

		if (DEBUG)
			println("rtp receiver terminated");

	}

	/** Debug output */
	private static void println(String str) {
		System.out.println("RtpStreamReceiver: " + str);
	}

	public static int byte2int(byte b) { // return (b>=0)? b : -((b^0xFF)+1);
		// return (b>=0)? b : b+0x100;
		return (b + 0x100) % 0x100;
	}

	public static int byte2int(byte b1, byte b2) {
		return (((b1 + 0x100) % 0x100) << 8) + (b2 + 0x100) % 0x100;
	}

	public static String getCodec() {
		return codec;
	}
}
