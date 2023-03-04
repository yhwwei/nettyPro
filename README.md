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
