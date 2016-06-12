LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
SPANDSP     := spandsp
LOCAL_MODULE    := gsm_jni

LOCAL_C_INCLUDES +=
LOCAL_CFLAGS = -DFIXED_POINT -DEXPORT="" -UHAVE_CONFIG_H -I$(LOCAL_PATH)/$(SPANDSP)/spandsp $(LOCAL_PATH)/$(SPANDSP)

LOCAL_SRC_FILES := gsm_jni.cpp \
	$(SPANDSP)/gsm0610_decode.c \
	$(SPANDSP)/gsm0610_encode.c \
	$(SPANDSP)/gsm0610_lpc.c \
	$(SPANDSP)/gsm0610_preprocess.c \
	$(SPANDSP)/gsm0610_rpe.c \
	$(SPANDSP)/gsm0610_short_term.c \
	$(SPANDSP)/gsm0610_long_term.c
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_CLINCLUDES += $(LOCAL_PATH)/$(SPANDSP)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SPANDSP)/spandsp
LOCAL_CFLAGS = -O3
include $(BUILD_SHARED_LIBRARY)