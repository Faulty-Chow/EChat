# 项目说明
E-Chat安卓客户端，这是一个小型的局域网聊天应用\
大概做了一星期，一个人完成了服务器端和客户端的编写。\
1. 服务器端使用了servlet，并没有使用Openfire偷懒。\
https://github.com/Faulty-Chow/EChat-Server
2. Android客户端依赖了okhttp\
https://github.com/Faulty-Chow/EChat
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
### 1. MainActivity

![](README_IMG%5CFirst%20Page.jpg)
显示图标，主要用于读取加载缓存

### 2. Login_Register_Activity
![](README_IMG%5CLogin.jpg)![](README_IMG%5CRegister.jpg)
登录和注册界面，没啥好说的

### 3. Chat_List_Activity
![](README_IMG%5CGroup%20Chat%20List.jpg)![](README_IMG%5CPrivate%20Chat.jpg)
聊天列表，群聊和私聊分页显示\
订阅 WebSocketService ,可以实现有消息时红点显示

### 4. Chat_Activity
![](README_IMG%5CGroup%20Chat.jpg)![](README_IMG%5CPrivate%20Chat.jpg)
聊天界面 极简风格
订阅 WebSocketService ，可以实时推送消息

### 5. User_Info_Activity
![](README_IMG%5CSelf%20User%20Info%201.jpg)![](README_IMG%5CUser%20Info.jpg)
显示用户信息，自己的和好友的
![](README_IMG%5CSelf%20User%20info%202.jpg) ![](README_IMG%5CUser%20Info.jpg) 
更改自己的用户信息

### 6. Group_Info_Activity
![](README_IMG%5CGroup%20Info%20Member.jpg)![](README_IMG%5CGroup%20Info%20Owner%201.jpg)
显示群聊信息，群管理员（可以编辑）和普通成员（只能查看）
![](README_IMG%5CGroup%20Info%20Owner%202.jpg)
增删成员