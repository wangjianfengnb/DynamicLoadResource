###静默安装Demo运行教程

####运行环境
 `Android API 21` 的模拟器，一定要`Android API 21`的模拟器，因为不同的`API LEVEL` 需要不同的签名文件，我这里只是提供了`API 21` 的签名文件
 
###安装部署
前期准备，需要静默安装的APK文件，修改名字为`autoinstall.apk`(这个名称是我在`MainActivity`里面写死的，如果要改名的话，可以自己改代码)

打开AndroidStudio`，导入工程之后。

然后把这个`autoinstall.apk` 放到 `silentinstaller` 的 5.0目录下。

然后`AndroidStudio ->Build ->Build APK` 生成apk到 `/silentinstaller/build/outputs/apk/` 目录下。

把`silentinstaller-debug-unaligned.apk` 拷贝到 `/silentinstaller/5.0` 目录下

打开`Studio` 的`Terminal`执行下面命令：

进入5.0目录

	cd 5.0

对`silentinstaller-debug-unaligned.apk` 签名，签名后生成的文件为 `silentinstaller.apk`
	
	java -jar SignApk.jar platform.x509.pem platform.pk8 silentinstaller-debug-unaligned.apk silentinstaller.apk
	
把`autoinstall.apk` 和 `silentinstaller.apk` 拷贝到模拟器中的 `/data/app/`目录

	adb push autoinstall.apk /data/app/
	adb push silentinstaller.apk /data/app/

进入adb shell 
	
	adb shell
	
安装`silentinstaller.apk`

	pm install -r /data/app/silentinstaller.apk


在模拟器中运行 silentinstaller 程序，点击按钮  查看logcat 在回到模拟器home界面，发现你的 autoinstall.apk已经安装完毕。

   