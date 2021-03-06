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
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;

import com.azfn.opentalk.TalkApplication;
import com.azfn.opentalk.network.rtp.CallState;
import com.azfn.opentalk.network.rtp.Codecs.Codecs;
import com.azfn.opentalk.network.rtp.Codecs.G711;
import com.azfn.opentalk.network.rtp.OpenTalkSocket;
import com.azfn.opentalk.network.rtp.RtpPacket;
import com.azfn.opentalk.network.rtp.RtpSocket;
import com.azfn.opentalk.tools.LogUtils;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;

/**
 * RtpStreamSender is a generic stream sender. It takes an InputStream and sends
 * it through RTP.
 */
public class RtpStreamSender extends Thread {
	private static final String TAG = "RtpStreamSender";
	/** Whether working in debug mode. */
	public static boolean DEBUG = true;

	/** The RtpSocket */
	RtpSocket rtp_socket = null;

	/** Payload type */
	Codecs codecs;

	/** Number of frame per second */
	int frame_rate;

	/** Number of bytes per frame */
	int frame_size;

	/**
	 * Whether it works synchronously with a local clock, or it it acts as slave
	 * of the InputStream
	 */
	boolean do_sync = true;

	/**
	 * Synchronization correction value, in milliseconds. It accellarates the
	 * sending rate respect to the nominal value, in order to compensate program
	 * latencies.
	 */
	int sync_adj = 0;

	/** Whether it is running */
	boolean running = false;
	boolean muted = false;
	
	//DTMF change
	String dtmf = "";
	int dtmf_payload_type = 101;
	
	private static HashMap<Character, Byte> rtpEventMap = new HashMap<Character,Byte>(){{
		put('0',(byte)0);
		put('1',(byte)1);
		put('2',(byte)2);
		put('3',(byte)3);
		put('4',(byte)4);
		put('5',(byte)5);
		put('6',(byte)6);
		put('7',(byte)7);
		put('8',(byte)8);
		put('9',(byte)9);
		put('*',(byte)10);
		put('#',(byte)11);
		put('A',(byte)12);
		put('B',(byte)13);
		put('C',(byte)14);
		put('D',(byte)15);
	}};
	//DTMF change 
	
	CallRecorder call_recorder = null;
	
	/**
	 * Constructs a RtpStreamSender.
	 * 
	 * @param do_sync
	 *            whether time synchronization must be performed by the
	 *            RtpStreamSender, or it is performed by the InputStream (e.g.
	 *            the system audio input)
	 * @param codecs
	 *            the payload type
	 * @param frame_rate
	 *            the frame rate, i.e. the number of frames that should be sent
	 *            per second; it is used to calculate the nominal packet time
	 *            and,in case of do_sync==true, the next departure time
	 * @param frame_size
	 *            the size of the payload
	 * @param src_socket
	 *            the socket used to send the RTP packet
	 * @param dest_addr
	 *            the destination address
	 * @param dest_port
	 *            the destination port
	 */
	public RtpStreamSender(boolean do_sync, Codecs codecs,
						   long frame_rate, int frame_size,
						   OpenTalkSocket src_socket, String dest_addr,
						   int dest_port, CallRecorder rec) {
		init(do_sync, codecs, frame_rate, frame_size,
				src_socket, dest_addr, dest_port);
		call_recorder = rec;
	}

	/** Inits the RtpStreamSender */
	private void init(boolean do_sync, Codecs codecs,
			  long frame_rate, int frame_size,
			  OpenTalkSocket src_socket, String dest_addr,
			  int dest_port) {
		this.codecs = codecs;
		this.frame_rate = (int)frame_rate;
		this.frame_size = frame_size;
		this.do_sync = do_sync;
		try {
			rtp_socket = new RtpSocket(src_socket, InetAddress
					.getByName(dest_addr), dest_port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Sets the synchronization adjustment time (in milliseconds). */
	public void setSyncAdj(int millisecs) {
		sync_adj = millisecs;
	}

	/** Whether is running */
	public boolean isRunning() {
		return running;
	}
	
	public boolean mute() {
		return muted = !muted;
	}

	public static int delay = 0;
	public static boolean changed;
	
	/** Stops running */
	public void halt() {
		running = false;
	}

	Random random;
	double smin = 200,s;
	int nearend;
	
	void calc(short[] lin,int off,int len) {
		int i,j;
		double sm = 30000,r;
		
		for (i = 0; i < len; i += 5) {
			j = lin[i+off];
			s = 0.03*Math.abs(j) + 0.97*s;
			if (s < sm) sm = s;
			if (s > smin) nearend = 3000*mu/5;
			else if (nearend > 0) nearend--;
		}
		r = (double)len/(100000*mu);
		if (sm > 2*smin || sm < smin/2)
			smin = sm*r + smin*(1-r);
	}

	void calc1(short[] lin,int off,int len) {
		int i,j;
		
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			lin[i+off] = (short)(j>>2);
		}
	}

	void calc2(short[] lin,int off,int len) {
		int i,j;
		
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			lin[i+off] = (short)(j>>1);
		}
	}

	void calc10(short[] lin,int off,int len) {
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

	void noise(short[] lin,int off,int len,double power) {
		int i,r = (int)(power*2);
		short ran;

		if (r == 0) r = 1;
		for (i = 0; i < len; i += 4) {
			ran = (short)(random.nextInt(r*2)-r);
			lin[i+off] = ran;
			lin[i+off+1] = ran;
			lin[i+off+2] = ran;
			lin[i+off+3] = ran;
		}
	}
	
	public static int m;
	int mu;
	
	/** Runs it in a new Thread. */
	public void run() {
		WifiManager wm = (WifiManager) TalkApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		long lastscan = 0,lastsent = 0;

		if (rtp_socket == null) return;
		int seqn = 0;
		long time = 0;
		double p = 0;
		int micgain = 0;
		long last_tx_time = 0;
		long next_tx_delay;
		long now;
		running = true;
		m = 1;
		int dtframesize = 4;
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		mu = codecs.codec.samp_rate()/8000;
		int min = AudioRecord.getMinBufferSize(codecs.codec.samp_rate(),
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		if (min == 640) {
			if (frame_size == 960) frame_size = 320;
			if (frame_size == 1024) frame_size = 160;
			min = 4096*3/2;
		} else if (min < 4096) {
			if (min <= 2048 && frame_size == 1024) frame_size /= 2;
			min = 4096*3/2;
		} else if (min == 4096) {
			min *= 3/2;
			if (frame_size == 960) frame_size = 320;
		} else {
			if (frame_size == 960) frame_size = 320;
			if (frame_size == 1024) frame_size = 160; // frame_size *= 2;
		}
		frame_rate = codecs.codec.samp_rate()/frame_size;
		long frame_period = 1000 / frame_rate;
		frame_rate *= 1.5;
		byte[] buffer = new byte[frame_size + 12];
		RtpPacket rtp_packet = new RtpPacket(buffer, 0);
		rtp_packet.setPayloadType(0);
		if (DEBUG)
			println("Reading blocks of " + buffer.length + " bytes");
		
		println("Sample rate  = " + codecs.codec.samp_rate());
		println("Buffer size = " + min);

		AudioRecord record = null;
		
		short[] lin = new short[frame_size*(frame_rate+2)];
		int num,ring = 0,pos;
		random = new Random();
		codecs.codec.init();
		while (running) {
			 if (changed || record == null) {
				if (record != null) {
					record.stop();
					record.release();
				}
				changed = false;
				record = new AudioRecord(MediaRecorder.AudioSource.MIC, codecs.codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
							min);
//				record = findAudioRecord();

				if (record.getState() != AudioRecord.STATE_INITIALIZED) {
//					Receiver.engine(Receiver.mContext).rejectcall();
					record = null;
					break;
				}
				if (Build.VERSION.SDK_INT >= 16) {
					RtpStreamSenderNew_SDK16.aec(record);
				}
				record.startRecording();
			 }
			 if (muted || CallState.call_state == CallState.UA_STATE_HOLD) {
				record.stop();
				while (running && (muted || CallState.call_state == CallState.UA_STATE_HOLD)) {
					try {
						sleep(1000);
					} catch (InterruptedException e1) {
					}
				}
				record.startRecording();
			 }
			 //DTMF change start
			 if (dtmf.length() != 0) {
	 			 byte[] dtmfbuf = new byte[dtframesize + 12];
				 RtpPacket dt_packet = new RtpPacket(dtmfbuf, 0);
				 dt_packet.setPayloadType(dtmf_payload_type);
 				 dt_packet.setPayloadLength(dtframesize);
				 dt_packet.setSscr(rtp_packet.getSscr());
				 long dttime = time;
				 int duration;
				 
	 			 for (int i = 0; i < 6; i++) { 
 	 				 time += 160;
 	 				 duration = (int)(time - dttime);
	 				 dt_packet.setSequenceNumber(seqn++);
	 				 dt_packet.setTimestamp(dttime);
	 				 dtmfbuf[12] = rtpEventMap.get(dtmf.charAt(0));
	 				 dtmfbuf[13] = (byte)0x0a;
	 				 dtmfbuf[14] = (byte)(duration >> 8);
	 				 dtmfbuf[15] = (byte)duration;
	 				 try {
						rtp_socket.send(dt_packet);
						sleep(20);
	 				 } catch (Exception e1) {
	 				 }
	 			 }
	 			 for (int i = 0; i < 3; i++) {
	 				 duration = (int)(time - dttime);
	 				 dt_packet.setSequenceNumber(seqn);
	 				 dt_packet.setTimestamp(dttime);
	 				 dtmfbuf[12] = rtpEventMap.get(dtmf.charAt(0));
	 				 dtmfbuf[13] = (byte)0x8a;
	 				 dtmfbuf[14] = (byte)(duration >> 8);
	 				 dtmfbuf[15] = (byte)duration;
	 				 try {
						rtp_socket.send(dt_packet);
	 				 } catch (Exception e1) {
	 				 }	 			 
	 			 }
	 			 time += 160; seqn++;
				dtmf=dtmf.substring(1);
			 }
			 //DTMF change end

			 if (frame_size < 480) {
				 now = System.currentTimeMillis();
				 next_tx_delay = frame_period - (now - last_tx_time);
				 last_tx_time = now;
				 if (next_tx_delay > 0) {
					 try {
						 sleep(next_tx_delay);
					 } catch (InterruptedException e1) {
					 }
					 last_tx_time += next_tx_delay-sync_adj;
				 }
			 }
			 pos = Integer.parseInt(Build.VERSION.SDK) == 21?0:((ring+delay*frame_rate*frame_size/2)%(frame_size*(frame_rate+1)));
			 num = record.read(lin,pos,frame_size);
			 if (num <= 0)
				 continue;

			 // Call recording: Save the frame to the CallRecorder.
			 if (call_recorder != null)
			 	call_recorder.writeOutgoing(lin, pos, num);

			 if (RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL) {
 				 calc(lin,pos,num);
 	 			 if (RtpStreamReceiver.nearend != 0 && RtpStreamReceiver.down_time == 0)
	 				 noise(lin,pos,num,p/2);
	 			 else if (nearend == 0)
	 				 p = 0.9*p + 0.1*s;
 			 } else switch (micgain) {
 			 case 1:
 				 calc1(lin,pos,num);
 				 break;
 			 case 2:
 				 calc2(lin,pos,num);
 				 break;
 			 case 10:
 				 calc10(lin,pos,num);
 				 break;
 			 }
			 if (CallState.call_state != CallState.UA_STATE_INCALL &&
					 CallState.call_state != CallState.UA_STATE_OUTGOING_CALL) {
				 if (codecs.codec.number() != 8) {
					 G711.alaw2linear(buffer, lin, num, mu);
					 num = codecs.codec.encode(lin, 0, buffer, num);
				 }
			 } else {
				 num = codecs.codec.encode(lin, Integer.parseInt(Build.VERSION.SDK) == 21?0:(ring%(frame_size*(frame_rate+1))), buffer, num);
			 }
			 
 			 ring += frame_size;
 			 rtp_packet.setSequenceNumber(seqn++);
 			 rtp_packet.setTimestamp(time);
 			 rtp_packet.setPayloadLength(num);
 			 now = SystemClock.elapsedRealtime();
 			 if (RtpStreamReceiver.timeout == 0 || now-lastsent > 500)
	 			 try {
	 				 lastsent = now;
	 				 rtp_socket.send(rtp_packet);
	 				 if (m > 1 && (RtpStreamReceiver.timeout == 0))
	 					 for (int i = 1; i < m; i++)
	 						 rtp_socket.send(rtp_packet);
	 			 } catch (Exception e) {
	 			 }
 			 if (codecs.codec.number() == 9)
 				 time += frame_size/2;
 			 else
 				 time += frame_size;
 			 if (RtpStreamReceiver.good != 0 &&
 					 RtpStreamReceiver.loss2/RtpStreamReceiver.good > 0.01) {
 				 if (now-lastscan > 10000) {
 					 wm.startScan();
 					 lastscan = now;
 				 }
 				 if (delay == 0 &&
 						 (codecs.codec.number() == 0 || codecs.codec.number() == 8 || codecs.codec.number() == 9))
 					 m = 2;
 				 else
 					 
 					 m = 1;
 			 } else
 				 m = 1;
		}
		if (Integer.parseInt(Build.VERSION.SDK) < 5)
			while (RtpStreamReceiver.getMode() == AudioManager.MODE_IN_CALL)
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
		if (record != null) {
			record.stop();
			record.release();
		}
		m = 0;

		codecs.codec.close();
		rtp_socket.close();
		rtp_socket = null;
		
		// Call recorder: stop recording outgoing.
		if (call_recorder != null)
		{
			call_recorder.stopOutgoing();
			call_recorder = null;
		}

		if (DEBUG)
			println("rtp sender terminated");
	}

	/** Debug output */
	private static void println(String str) {
		System.out.println("RtpStreamSender: " + str);
	}

	/** Set RTP payload type of outband DTMF packets. **/  
	public void setDTMFpayloadType(int payload_type){
		dtmf_payload_type = payload_type; 
	}
	
	/** Send outband DTMF packets */
	public void sendDTMF(char c) {
		dtmf = dtmf+c; // will be set to 0 after sending tones
	}
	//DTMF change

	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	public AudioRecord findAudioRecord() {
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						LogUtils.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
								+ channelConfig);
						int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

						if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
							// check if we can instantiate and have a success
							AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
								return recorder;
						}
					} catch (Exception e) {
						LogUtils.e(TAG, rate + "Exception, keep trying.");
					}
				}
			}
		}
		return null;
	}
}
