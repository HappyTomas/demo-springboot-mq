## mq组建
>基于activemq的


### 一、介绍
>找时间完善文档

### 二、详情

### 三、开发计划相关

#### 3.1.todo-list

***branch:master***

**2018-7-20**
- 1.需要优化mq组件，可参考[bug_logs](#logs)文件；
- 2.mq_sender表，主键生成id会重复，概率还很大哦；
- 3.mq_receiver_index3的唯一约束，要求[topic_queue`, `msg_id`]组合唯一。解决：1.增加主键自增的id；2.目前msg_id没有完全做到唯一；3.topic_queue也要唯一，追加'-6f5e'标识；
- 4.`msg_time`要支持毫秒维度；
- 5.新增状态字段，正常or异常的；

---
**2018-7-21**
- 1.如果是异常，需要记录异常原因，再加状态表示已处理，加重试次数；(发延迟mq提醒有异常待处理，重试5次就放弃)

---

#### 3.2.完成记录(总)


### 四、其他

- [mybatis generator MySQL 自增ID出现重复问题MySQLIntegrityConstraintViolationException](http://blog.csdn.net/lvbang_lzt/article/details/55188357)


