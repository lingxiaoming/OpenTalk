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
package com.azfn.opentalk.network.rtp.media;


import com.azfn.opentalk.network.rtp.Codecs.Codecs;
import com.azfn.opentalk.network.rtp.OpenTalkSocket;
import com.azfn.opentalk.tools.LogUtils;

/**
 * Audio launcher based on javax.sound
 */
public class JAudioLauncher implements MediaLauncher {
    private static final String TAG = "JAudioLauncher";

    /**
     * Sample rate [bytes]
     */
    int sample_rate = 8000;
    /**
     * Sample size [bytes]
     */
    int sample_size = 1;
    /**
     * Frame size [bytes]
     */
    int frame_size = 160;
    /**
     * Frame rate [frames per second]
     */
    int frame_rate = 50; //=sample_rate/(frame_size/sample_size);
    boolean signed = false;
    boolean big_endian = false;

    //String filename="audio.wav";


    int dir; // duplex= 0, recv-only= -1, send-only= +1;

    OpenTalkSocket socket = null;
    RtpStreamSender sender = null;
    RtpStreamReceiver receiver = null;

    //change DTMF
    boolean useDTMF = false;  // zero means not use outband DTMF

    /**
     * Costructs the audio launcher
     */
    public JAudioLauncher(RtpStreamSender rtp_sender, RtpStreamReceiver rtp_receiver) {
        sender = rtp_sender;
        receiver = rtp_receiver;
    }

    /**
     * Costructs the audio launcher
     */
    public JAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, Codecs codecs, int dtmf_pt) {
        frame_rate = sample_rate / frame_size;
        useDTMF = (dtmf_pt != 0);
        try {
            CallRecorder call_recorder = new CallRecorder(null, codecs.codec.samp_rate()); // Autogenerate filename from date.
            socket = new OpenTalkSocket(local_port);
            dir = direction;
            // sender
            if (dir >= 0) {
                LogUtils.d(TAG, "new audio sender to " + remote_addr + ":" + remote_port);
                //audio_input=new AudioInput();
                sender = new RtpStreamSender(true, codecs, frame_rate, frame_size, socket, remote_addr, remote_port, call_recorder);
                sender.setSyncAdj(2);
                sender.setDTMFpayloadType(dtmf_pt);
            }

            // receiver
            if (dir <= 0) {
                receiver = new RtpStreamReceiver(socket, codecs, call_recorder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts media application
     */
    public boolean startMedia() {

        if (sender != null) {
            sender.start();
        }
        if (receiver != null) {
            receiver.start();
        }

        return true;
    }

    /**
     * Stops media application
     */
    public boolean stopMedia() {
        if (sender != null) {
            sender.halt();
            sender = null;
        }
        if (receiver != null) {
            receiver.halt();
            receiver = null;
        }
        if (socket != null)
            socket.close();
        return true;
    }

    public boolean muteMedia() {
        if (sender != null)
            return sender.mute();
        return false;
    }

    public int speakerMedia(int mode) {
        if (receiver != null)
            return receiver.speaker(mode);
        return 0;
    }

    //change DTMF

    /**
     * Send outband DTMF packets
     **/
    public boolean sendDTMF(char c) {
        if (!useDTMF) return false;
        sender.sendDTMF(c);
        return true;
    }

}