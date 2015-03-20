package com.raildeliveryservices.burnrubber.models;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class SoundPlayer {
	
	private static SoundPool _soundPool;
	private static SparseIntArray _soundPoolMap;
	
	public static final int notificationSound = com.raildeliveryservices.burnrubber.R.raw.notification;
	
	public static void initSounds(Context context) {
		_soundPool = new SoundPool(2, AudioManager.STREAM_NOTIFICATION, 100);
		_soundPoolMap = new SparseIntArray(1);
		
		_soundPoolMap.put(notificationSound, _soundPool.load(context, com.raildeliveryservices.burnrubber.R.raw.notification, 1));
	}
	
	public static void playSound(Context context, int soundId) {
		if (_soundPool == null || _soundPoolMap == null) {
			initSounds(context);
		}
		
		float volume = 1.0f;
		_soundPool.play(_soundPoolMap.get(soundId), volume, volume, 1, 0, 1f);
	}
}
