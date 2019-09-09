#
# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

#
# Build app code.
#
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

dialer_dir := ../../apps/Dialer/java/com/android/dialer/app
wetalk_support_dir := ../../../../../../packages/apps/WeTalk/support
moments_dir := ../../../../../../packages/apps/Moments


WEARLAUNCHER_MANIFEST_FILES := \
  AndroidManifest.xml \
  $(dialer_dir)/readboysupport/AndroidManifest.xml \
  $(moments_dir)/support/AndroidManifest.xml

LOCAL_FULL_LIBS_MANIFEST_FILES := \
  $(addprefix $(LOCAL_PATH)/, $(WEARLAUNCHER_MANIFEST_FILES)/)

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v4 \
    android-support-v7-appcompat \
    android-support-v7-recyclerview \
    android-support-v7-palette \
    android-support-v13 

LOCAL_STATIC_JAVA_LIBRARIES := \
	wearlauncher-picasso \
	wearlauncher-volley \
	launcher-glide \
    launcher-baseadapter

LOCAL_STATIC_JAVA_LIBRARIES += moments-bravh
LOCAL_STATIC_JAVA_LIBRARIES += moments-support
    
LOCAL_JAVA_LIBRARIES := telephony-common
LOCAL_JAVA_LIBRARIES += ims-common


LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-renderscript-files-under, src) \
    $(call all-java-files-under, $(wetalk_support_dir)/src) \
    $(call all-java-files-under, $(dialer_dir)/readboysupport)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    vendor/mediatek/proprietary/packages/apps/Dialer/java/com/android/dialer/app/readboysupport/res \
    frameworks/support/v7/recyclerview/res \
    frameworks/support/v7/cardview/res \
    packages/apps/WeTalk/support/res \
    packages/apps/Moments/support/res \
	packages/apps/Moments/support/res-extra \
    packages/apps/Moments/BRAVH/res


LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.android.keyguard \
    --extra-packages com.readboy.wetalk.support \
    --extra-packages com.android.dialer.app \
    --extra-packages com.readboy.mmsupport \
    --extra-packages com.readboy.support.speechinput \
    --extra-packages com.scwang.smartrefresh.layout
	
LOCAL_CERTIFICATE := platform
#LOCAL_SDK_VERSION := current
LOCAL_MIN_SDK_VERSION := 21
LOCAL_PACKAGE_NAME := WearLauncher
LOCAL_PRIVILEGED_MODULE := true
#LOCAL_DEX_PREOPT := false
LOCAL_PROGUARD_ENABLED := disabled


LOCAL_DX_FLAGS := --multi-dex --main-dex-list=$(mainDexList) --minimal-main-dex
LOCAL_JACK_FLAGS += --multi-dex native

LOCAL_USE_AAPT2 := true

LOCAL_OVERRIDES_PACKAGES += Launcher3 Launcher3Go

ifeq ($(strip $(CENON_SIMPLIFY_VERSION)), yes)
LOCAL_OVERRIDES_PACKAGES += \
    DataTransfer \
    AutoDialer \
    BasicDreams \
    BSPTelephonyDevTool \
    BookmarkProvider \
    BluetoothMidiService \
    BtTool \
    BackupRestoreConfirmation \
    CallLogBackup \
    WallpaperCropper \
    WallpaperBackup \
    PhotoTable \
    PicoTts \
    LiveWallpapers \
    LiveWallpapersPicker \
    MagicSmokeWallpapers \
    VisualizationWallpapers \
    Galaxy4 \
    HoloSpiralWallpaper \
    NoiseField \
    PhaseBeam \
    YahooNewsWidget \
    CarrierConfig \
    MtkQuickSearchBox \
    QuickSearchBox \
    MtkFloatMenu \
    MTKThermalManager \
    TouchPal \
    Development \
    MultiCoreObserver \
    CtsShimPrebuilt \
    EasterEgg \
    DuraSpeed \
    EmergencyInfo \
    MtkEmergencyInfo \
    ExactCalculator \
    PrintRecommendationService \
    CallLogBackup \
    CtsShimPrivPrebuilt \
    SimRecoveryTestTool \
    FileManagerTest \
    WiFiTest \
    SensorHub \
    BuiltInPrintService \
    CompanionDeviceManager \
    MtkWallpaperPicker \
    EmCamera \
    Stk \
    Stk1

## set MTK_BASIC_PACKAGE

LOCAL_OVERRIDES_PACKAGES += \
    Email \
    Exchange2 \
    MtkEmail \
    VpnDialogs \
    SharedStorageBackup \
    Calculator \
    MtkBrowser \
    MtkCalendar \
    MusicBspPlus \
    MusicFX \
    FMRadio \
    MtkDeskClock \
    SimProcessor \
    WiFiTest \
    SensorHub \
    QuickSearchBox \
    SchedulePowerOnOff \
    DownloadProviderUi \
    FileManager \
    MtkContacts  \
    PrintSpooler \
    MtkGallery2 \
    MtkLatinIME \
    CellBroadcastReceiver \
    MtkCalendar \
    CalendarImporter \
    MtkWebView \
    MtkCalendarProvider \
    UserDictionaryProvider \
    HTMLViewer \
    NfcNci \
    Protips \
    CallRecorderService \
    MtkSimProcessor \
    Provision \
    BatteryWarning \
    Music \
    MtkSystemUI

endif

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	launcher-glide:libs/glide-3.7.0.jar \
    launcher-baseadapter:/libs/baseadapter.jar \
	wearlauncher-picasso:libs/picasso.jar \
	wearlauncher-volley:libs/RBVolley.jar

include $(BUILD_MULTI_PREBUILT)

#include $(BUILD_HOST_JAVA_LIBRARY)

# ==================================================
include $(call all-makefiles-under,$(LOCAL_PATH))

