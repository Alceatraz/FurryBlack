#FurryBlack - 一个小动物形象的人工智障

## 警告

**由于强迫症对不齐很难受 所以很多代码的名命很怪**

> user → user
> disz → discuz  
> grop → group  

> 私聊 → 私聊
> 组聊 → 讨论组
> 群聊 → 群聊

[很怪.jpg]https://git.blacktech.studio/blacktechstudio/furryblack/raw/master/images/very_strange.jpg)

## 警告×2

$是环境变量的意思

例如 `$NAME` 意思是这里是名字而不是照抄"`$NAME`"

## 文件结构

**PACKAGENAME即名称**

conf：所有配置文件的目录
data：所有数据文件的目录

conf/module：所有模块的配置文件，其下按照模块名称生成目录
data/module：所有模块的数据文件，其下按照模块名称生成目录

conf/module/$NAME/config.properties：Module自动为每个模块生成的配置文件

## 框架

**一切皆模块**

除去主类和其他构造类，一切功能类皆为模块。

模块提供了一些基础工具，如初始化配置及数据目录，提供一个Properties对象，用于快速配置

模块有三个既定功能的子类分为

- 触发器 `extends ModuleTrigger`
- 监听器 `extends ModuleListener`
- 执行器 `extends ModuleExecutor`

entry为入口文件，目前共有四个核心模块（`extends Module`）：

- Systemd：整个框架的核心，负责启停，事件处理
- Message：发送消息的模块，管理员的ID将在此配置
- Nickmap：昵称管理模块，昵称的获取
- DDNSAPI：一个动态域名的客户端

核心模块由于安全问题，仅在entry中有其实例，使用`getSystemd()`可返回其代理子类，子类只对需要公开的方法进行调用转发