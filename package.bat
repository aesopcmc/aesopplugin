@echo off
@REM 解决中文乱码问题
chcp 65001
@REM 设置环境变量
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.8
@REM set CLASSPATH=.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;
set PATH=%JAVA_HOME%\bin;C:\Windows\System32\OpenSSH;

echo "》》开始打包"
call E:\Programming\maven-3.8.8\apache-maven-3.8.8\bin\mvn clean package

echo "》》开始上传文件"
@REM 设置本地文件路径
set localFile=E:\Programming\mc\aesopplugin\Build\target\AesopPlugin-1.3.4.jar
@REM 设置服务器ip地址
set host=192.168.0.101
@REM 设置服务器端口
set port=2299
@REM 设置服务器登录用户名
set user=root
@REM 设置需要上传的位置路径
set remotePath=/home/minecraft/1.20.2/plugins
@REM 执行scp命令上传文件
scp -P %port% %localFile% %user%@%host%:%remotePath%
echo "》》上传完毕"

@REM pause脚本执行完成之后需要手动关闭，如需直接关闭，替换成exit即可
exit

