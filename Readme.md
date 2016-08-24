##动态加载另一个APK文件的资源


###知识要点

- DexClassLoader加载APK文件。
	
- 详情需要理解DexClassLoader和PathClassLoader的区别:[Android中插件开发篇之----类加载器 ](http://blog.csdn.net/jiangwei0910410003/article/details/41384667)


- 如何将插件APK的资源加载到本地的Resource中，需要用到AssetManager的addAssetPath方法。这个方法可以加载资源目录也可以加载zip，apk就是一个zip文件。


###操作教程
- 运行app module。
- 运行resourceloaderapk module
- 将resourceloaderapk module 生成的apk 重命名为 resource.apk 。名字可以改的，具体看MainActivity代码
- 利用adb 将 resource.apk 移动到 /data/data/packagename/cache 目录下。
- 退出app重新运行 



####静默安装APK的代码在silentinstaller module里面

