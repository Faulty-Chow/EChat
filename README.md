# 项目说明
E-Chat安卓客户端，这是一个小型的局域网聊天应用\
大概做了一星期，一个人完成了服务器端和客户端的编写。\
1. 服务器端使用了servlet，并没有使用Openfire偷懒。\
2. Android客户端借助了okhttp\
3. 数据库用的是MySql

# Android 项目下文件说明
## 1. 工具类
1. CacheUtil\
    用于缓存，基本逻辑是每次从服务器请求数据后，将其以JSON形式存在本地，Activity优先从本地读取。\
    userInfo类  运行时缓存，相当于各个Activity的共享内存
2. HttpUtil\
    封装了okhttp客户端，提供同步/异步的get/post方法
## 2. Service
1. WebSocketService\
    借助okhttp实现http长连接，以便服务器收到消息更新时，可以主动向客户端发送message
## 3. Activity
1. MainActivity\

