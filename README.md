Glusterfs_restful API接口说明
	本系统是以java的JFinal框架为技术基础，实现的一个Glusterfs的一个restful API，部署在Glusterfs本地或者远程，通过http请求的方式，控制和查看glusterfs集群。本文档详述glusterfs restful API的接口使用说明。
一、	配置文件
程序配置文件位置在源码目录的res下，有两个基本配置文件，a_little_config.txt和log4j.properties，其中log4j.properties是日志配置，a_little_config.txt是系统配置参数。
二、	返回信息格式
Http请求后返回一个json格式化后的包含请求结果的对象。主要包括两个部分：
i.	Obejct resultData：表示返回的结果对象
ii.	Stack exceptionsStack ：表示异常信息栈，如果有异常，则异常信息在栈中。
三、	集群信息
1.	获取集群信息
a)	gfs/getClusterInfo 获取集群几点信息，相当于gluster peer status（但包含当前进行命令的节点，即集群中的所有节点；gluster peer status不显示本机节点），返回结果是Map<String,String> 其中，key表示节点ip，value表示节点是否可以连接，其中PeerinCluster(Connected)表示可以连接，PeerinCluster(DisConnected)表示不能连接。
2.	设置集群
a)	gfs/probePeer/ip 为集群添加节点。通过url传入需要添加的节点参数，即节点ip，其中 ”.“ 用“@“符号代替。相当于执行gluster peer probe。返回一个整数表示状态，返回1表示添加成功；-1表示ip不合法，-2 表示出错，0表示添加失败。
b)	gfs/detachPeer/ip 为集群删除节点。通过url传入需要删除的节点参数，即节点ip，其中 ”.“ 用“@“符号代替。相当于执行gluster detach probe。返回一个整数表示状态，返回1表示删除成功；-1表示ip不合法，-2 表示出错，0表示删除失败。
四、	数据卷信息
a)	gfs/getAllVolumeInfo 获取集群所有volume的信息。返回一个List<VolumeEntity>，VolumeEntity中包含一个volume的基本信息，但不包括volume的数据。
b)	gfs/getOneVolumeInfo 获取一个volume的信息。通过url传入一个volumeName，然后返回一个VolumeEntity的对象，但不包括volume的数据。
c)	gfs/getOneVolume/VolumeName 获取一个volume，包括volume的信息和volume中的数据，返回一个VolumeEntity的对象。
d)	gfs/getAllVolume 获取集群所有volume的信息。返回一个List<VolumeEntity>，VolumeEntity中包含一个volume的基本信息，且包括volume的数据。
e)	gfs/stopVolume/volumeName 通过url传入一个volumeName，停止VolumeName的，返回，1 表示成功 ，0表示已经处于停止状态，-1表示 volumeName不存在；-2表示停止失败，其他错误。
f)	gfs/startVolume/volumeName 通过url传入一个volumeName，开始VolumeName的，返回1 表示成功 ，0表示已经处于开始状态，-1表示 volumeName不存在；-2表示开始失败，其他错误。
g)	gfs/createVolume/volumeName-count-type-bricks-moutPoint，其中bricks以~分隔,地址“.”用@代替；返回一个状态值，0:参数不合格； 1:可以创建 ;-1：brick的ip不在集群中或者未连接;-2 -3 -4:表示类型与brick数目不匹配 ; -5 :volumeName 已经存在；-6：挂载点存在且不为空，不能作为挂载点。
h)	gfs/deleteVolume/volumeName 通过url传入一个volumeName，删除VolumeName的，返回1 表示成功 ；-1表示volume name不存在；-2表示volume 不在停止状态不能删除；-3表示删除失败，-4表示Constant.MountRecordPath文件不存在;-5表示其他错误。
i)	VolumeEntity类相关属性
 
五、	数据操作
a)	data/removeData/path删除数据，通过url传入需要删除文件或者文件夹绝对路径path，开启删除path任务，返回状态值，1表示任务开启成功，将任务加入删除任务列表，正在删除，-1表示源文件不存在，放弃删除任务。
b)	data/copyData/sourcepath~destpath拷贝数据，通过url传入需要拷贝文件或者文件夹绝对路径sourcepath和需要拷贝到的目的路径destpath，开启拷贝path任务，返回状态值，1表示任务开启成功，将任务加入拷贝任务列表，正在拷贝，-1表示源文件不存在，放弃拷贝任务。
c)	data/ getOperateTasks 获得数据操作任务列表。返回一个List<TaskOperateData> operateDataTask，其中TaskOperateData为任务类，包含每个数据操作任务需要的信息。
d)	TaskOperateData类相关属性：
 
