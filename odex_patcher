# odex_patcher for Android
An .odex file is an Optimized .dex file (hence the 'o'), meaning that it has been optimized for a specific platform. The advantage to this is that startup time is much faster since the VM doesn't have to perform any optimizing at startup/runtime. When an app is installed, the system will create an odex file (based on the dex file inside apk file) to optimize the startup time. This means that when an app is launched, the system will use the odex file, not the original dex file inside the apk. Thus, we can directly modify the odex file to patch the app, without re-installation. But of course, we need root access to do the patch. This app is designed to achieve such goal.  
Please be noted: the code is not well arranged, as most of them are directly copied from APK Editor. If you like it, please help to improve it.

## How to Use
### Step 1: Compile and Install Odex Patcher
Please use APK Builder (https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkbuilder) to compile it:  
https://youtu.be/Lp-hQU2IfxI
### Step 2: Modifiy an app
Modify app with APK Editor Pro (https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkeditor.pro), here is an example:  
https://youtu.be/cvgpwVID2qk  
You could also use apktool to edit the smali code on PC.
### Step 3: Patch the app with Odex Patcher
Open Odex Patcher, select the apk generated in Step 2, and then click at the "Pacth" button:  
https://youtu.be/e835a7lg9cE
