netty的学习
## 关于select、selectionKey、channel的关系理解
![img.png](img.png)

每一个selectionKey都会记录 自己对应哪个selector
而selectionKey会有一个interest set 记录这个channel感兴趣的event类型有哪些
（四种类型 ）
SelectionKey.OP_CONNECT  一个channel成功连接到另一个服务器称为”连接就绪“
SelectionKey.OP_ACCEPT      服务器开启  表示接收客户端的连接事件
SelectionKey.OP_READ        读就绪
SelectionKey.OP_WRITE       写就绪


注意 一个channel可能不只是只注册到一个selector
所以每个channel有一个selectionKey数组（标记对应的selector）
通过这个selectionKey数组（因为每个selectionKey都会记录对应的selector）
当要channel.register(selector,one of SelectionKy.InterestSet) 
第二个参数就是上面的四种类型之一
这个方法 首先会调用channel的findKey（selector）  来找到对应的selectionKey
如果没找到  说明这个channel是第一次注册到该selector，则新建一个selectionKey对应这个selector
后续 channel如果要要注册新的interesting事件（上面的四种之一），就会找到这个selectionKey，往里面的interest set这个集合添加新的感兴趣的事情

一句话selectionKey对应一个selector   一个selector不一定只有一个selectionKey，channel通过selectionKey和对应的selector绑定
[参考链接](https://juejin.cn/post/6844903824663003150)

## netty的一些理解
![img_1.png](img_1.png)
EventLoop就是一个线程  而EventLoop就是一个线程池
EventLoop对应多个channel，所以channel通过方法eventloop()就会获取管理它的那个线程
每个eventloop都有一个selector（底层就是NIO）
每个NioChannel都有一个唯一的ChannelPipeLine
[参考](https://www.cnblogs.com/jing99/p/12515157.html#:~:text=%E4%B8%80%E4%B8%AAEventLoop%20%E5%8F%AF%E8%83%BD%E4%BC%9A%E8%A2%AB%E5%88%86%E9%85%8D%E7%BB%99%E4%B8%80%E4%B8%AA%E6%88%96%E5%A4%9A%E4%B8%AAChannel%E3%80%82%20Channel%20%E4%B8%BANetty%20%E7%BD%91%E7%BB%9C%E6%93%8D%E4%BD%9C%E6%8A%BD%E8%B1%A1%E7%B1%BB%EF%BC%8CEventLoop,%E4%B8%BB%E8%A6%81%E6%98%AF%E4%B8%BAChannel%20%E5%A4%84%E7%90%86%20I%2FO%20%E6%93%8D%E4%BD%9C%EF%BC%8C%E4%B8%A4%E8%80%85%E9%85%8D%E5%90%88%E5%8F%82%E4%B8%8E%20I%2FO%20%E6%93%8D%E4%BD%9C%E3%80%82)
### 如何理解channelPipeLine
比如生活中对一个事物的加工，放在流水线上经历多次加工才算生产成功。
对于一个事件，（channel要干嘛），这个事件会被放在ChannelPipeLine，ChannelPipeLine可以绑定多个Handler，按顺序执行这些Handler，最后完成事件处理

添加一个Handler在流水线的最后一步
socketChannel.pipeline().addLast(new NettyServerHandler());
[参考](https://www.cnblogs.com/qdhxhz/p/10234908.html)
