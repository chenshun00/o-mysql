### 监控sql执行

#### 背景
每次执行sql都不知道最终执行的 `sql` 是什么样子的，每次出了问题都要通过抓包去解决，于是产生了
`o-mysql`

#### 环境

*   安装maven

#### 如何使用

*   安装

1、clone 到本地, `mvn clean install`,获取jar包

2、或者直接运行如下脚本

```bash
cd ~
git clone git@github.com:chenshun00/o-mysql.git
cd o-mysql
pwd
mvn clean install -U
```

* 使用

idea可以直接在 `edit configurations` --> `Vm options` 处加入如下命令，点击 apply,保存即可

```bash
-javaagent:/*[替换成你到jar包位置]*/.m2/repository/top/huzhurong/agent/o-mysql/1.0-SNAPSHOT/o-mysql-1.0-SNAPSHOT.jar
```