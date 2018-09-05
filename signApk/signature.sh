#! /bin/bash

#对变量赋值：
a="this file is tested succeesfully  "
# 现在打印变量a的内容：
echo $a

# 转换系统签名命令
#./keytool-importkeypair -k SigNature.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias SigNature
./keytool-importkeypair -k nscarlauncher.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias nscar
# SignDemo.jks : 签名文件
# 123456 : 签名文件密码
# platform.pk8、platform.x509.pem : 系统签名文件
# SignDemo : 签名文件别名