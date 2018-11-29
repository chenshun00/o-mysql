#### 遇到的问题

Q:java.lang.VerifyError: Expecting to find xxxx

A:原因是调用方法的时候类型不对，例如传递的是 xx(Long key) , 就不能写 mv.visitLdcxxx(1000)，需要先将1000转型成Long类型的，然后传递参数。

Q:某些时候代码没有问题，能编译，能打包，但是运行就是不行

A:java8 jvm的Verify验证更为严格了，需要加入如下参数 java7`-XX:-UseSplitVerifier` , java8` -noverify` 才能将取消


Q:在一些定制的环境中，需要去请求外部的数据，而限制于 `agent` 本身的性质，我们只能使用 `jdk` 的 `HttpClient`，但是同时又需要监控 `httpClient`
的执行的话，就可能存在一些不能监控的意外。

A:解决方法，需要在 `transform` 运行完之后加入 `init` 的调用