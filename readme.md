### 监控sql执行

#### 背景
监控执行的 `sql` 语句，读取返回行数，结合 `agent` 产生了 `o-mysql`

#### 如何开发

*   安装maven
*   导入该项目(别忘了star哦)

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

加入 `-Dmysql.log=yes` jvm参数输出到日志`${user.home}/logs/agent/xxxxx.log`，而不是输出在控制台

#### 使用结果

```text
【sql:SELECT id,context,type,meta_id,add_time FROM perform_data WHERE id = 1154080】,【rt:1(ms)】,【扫描行数:1】
【sql:SELECT id,context,type,meta_id,add_time FROM perform_data WHERE id = 111111】,【rt:2(ms)】,【扫描行数:0】
```

> 仅支持 `select`,`update`   

#### Asm 和 agent 的使用

*   注意点

jvm 中，一份class文件是一致的 `(equals(class) = true)` 的前提是类是一致的，加载该类的 `classLoader` 是一样的才是相同的class
如果class是一样的，但是 `classLoader` 不一致，那么equals方法会返回false。表明不是同一个类，jdk本身自带的因为都是`boot` 加载的，所以没有问题

*   使用场景
    *   监控(APM)，例如 `rt`,`错误率`,`慢sql`,`sdk 调用率` , `memcache/redis qps`等等
    *   安全审计
    
> 相比aop的优势，应用代码无侵入，取消 `-javaagent` 参数即可取消监控
    
#### 参考资料

[https://github.com/alibaba/TProfiler](https://github.com/alibaba/TProfiler)

[https://github.com/btraceio/btrace](https://github.com/btraceio/btrace)
