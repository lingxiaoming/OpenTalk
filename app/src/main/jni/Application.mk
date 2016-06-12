#APP_PROJECT_PATH := $(call my-dir)
APP_MODULES	 := gsm_jni

APP_STL := gnustl_static
APP_OPTIM        := release 
APP_CFLAGS       += -O3
APP_ABI			:= armeabi