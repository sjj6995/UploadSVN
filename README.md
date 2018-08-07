插件使用方法及版本更新内容
=====
版本0.0.2:
-----
在gradle.properties中添加属性<br>
```
LocalPath= apk生成路径
SvnPath= 上传apk的svn路径
UserName = svn用户账号
PassWord = svn用户密码
ProjectName = apk文件名字前缀，如Project = Demo apk文件名字为 Demo-debug-<svnRevision>.apk
```
在Project中build.gradle中添加：<br>
```
apply plugin: 'com.autozi.gradlesvn'
```

版本0.0.3:
-----
更新内容及变化：<br>
在gradle.properties中删除LocalPath属性<br>
在主module的build.gradle中添加flavor:<br>
```
  productFlavors {
        ...
        svn{
        }
    }
```
解决了直接使用android studio run Debug包时，编译时间过长时会导致无法安装apk。（注意：android studio中build variants 不要选择svn即可）<br>

Gradle:
-----
```
 repositories {
        maven {
            url "https://dl.bintray.com/sjj6996/autozi"
        }
    }
    dependencies {
        classpath 'com.autozi.autozi:uploadsvn:0.0.2'
    }
```
打包Task：
```
./gradlew uploadsvn
```
特别提醒:
-----
com.android.tools.build:gradle:xx ，如果xx小于3.0的话请使用0.0.2版本，大于等于3.0使用0.0.3版本
