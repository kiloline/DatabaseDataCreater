# DatabaseDataCreater：一个用于从create table的SQL当中自动生成自定义的insert数据的工具
###今后将不再提供Java8的测试和支持，全面转移到Java11直到下一个Java LTS为止
### 简介
本程序的作用是根据给定的建立数据库表的SQL（create语句），自动的生成给定行数的数据，这些数据可以以insert语句的方式产生，也可以是逗号隔开的csv格式以便通过数据库外部表或load工具来利用。支持通过JDBC直接导入数据。支持生成json字符串。
## 最简化使用方法
该方法可以不需要config.properties的配置文件。  
1.下载https://github.com/kiloline/DatabaseDataCreater/releases/download/1.2.0/DBDF-1.2.0.zip  
2.解压  
3.命令行定位到jar包所在位置  
4.输入命令：java -jar DBDF-1.2.0-jar-with-dependencies.jar  
5.按照提示输入create命令和输出条数  
6.在jar包所在的文件夹下就可以找到和表名一致的.sql文件，里面是相应条数的insert数据  
7.复制粘贴运行三连  

## 当前版本性能测试
#### 硬件环境
CPU:Intel i7-7700HQ 3.4~3.5Ghz  
Memory:16G DDR4 2400Mhz  
SSD:WDS480G2G0B 西数绿盘 480G SATA SSD

#### 软件环境以及测试条件
OS:Windows10 1809  
Java:Java 11.0.3  
测试条件:  
1.4线程  
2.异步写入  
3.默认值比例0.9  
4.禁止SQL优化  
5.写入方式为csv  
6.生成条数5000000  
  
 ![image](https://github.com/kiloline/DatabaseDataCreater/blob/master/sample_test.png)  

####性能变化

## 程序参数详解
-h:显示帮助。  
-f:指定输入的SQL文件位置，如果不指定的话将会询问对什么样的create创建数据。  
-n:指定输出的数据条数，如果不指定的话将会询问具体的输出条数。  
-o:指定保存输出数据的文件夹位置。  
-t:指定线程数量，请根据CPU核心数量来确定。  
-i:指定生成数据的载入方式，有"jdbc"、"sql"、"csv"、"json"、"mongo"五种方式。  
-a:允许异步写（将一个表的insert拆分成多个sql文件），可以加快生成速度；在-t参数大于1时才有实际意义。  
-O:允许SQL优化，将长度上限特别大的字段随机缩短长度，可以加快生成速度，或者模拟现实当中的字段长度。  

## 支持的SQL数据类型
1.数值型  
number  
numeric  
integer  
int  
float  
double  
decimal  

2.字符型  
varchar  
varchar2  
char  
nvarchar  
nvarchar2  
string  
text  

3.日期型  
date  
timestamp  

4.布尔型  
boolean  
bool

## 自定义数据格式，按照预计生成数据的方法
注意：字段类型会覆盖先前指定的类型，所以务必保证对应字段的数据类型是长度足够的字符串形式。
### 一般的数据库当中已经存在的方法
1.默认值/default：该关键字后面的跟随的常量将会成为该字段的默认值。  
2.primary key/unique约束:该关键字将会使该字段中所有数据均唯一。使用的注意事项见最下。

### 本程序默认的数据格式实现
#### 1.stringtype
可以在每个字段的后面加上stringtype关键字来指定当前字段的数据格式，包括ch_idcard（中国身份证号）、telephone（中国手机号）、email（电子邮件）、ch_word（汉字字符串）、a/b/c/d/e_ip（五种IP地址）、warp_latitude（经纬度）等几种数据。
#### 2.numberarea  
可以在每个字段的后面加上numberarea关键来指定当前字段的数值范围，该方式仅对标记为数值型的字段适用。  
例如numberarea 5~10，就会生成范围在{5,6,7,8,9,10}之内的任意数，根据字段类型也可以是浮点数。

### 如何自定义数据格式
#### 1.inline方式  
如果某个字段有多个确定值，只需要从中选择一个的话，可以用inline方式。多个值放在字段后面的一个大括号中，用逗号分开。需要注意的是这种方法不限制数据类型，但是字符型和日期型需要添加单引号，数值型不需要添加引号。  
例如"name varchar(10) {"aaa","bbb"}"，这样的话那么这个字段里的值就只有aaa和bbb。  
该方式优先级最高，会覆盖primary key/unique约束、默认值、stringtype、numberarea等方式。

#### 2.regulartype
即通过正则表达式生成规定格式的字符串，关键字是regulartype。  
因为标准的正则表达式当中，很多元字符对于生成字符串是没有用的，所以在这里的元字符是有所简化的版本。   
##### 元字符定义：  
{n}：匹配前面的子表达式确定次数。  
{n,}：匹配前面的子表达式至少n次，至多(n+1)5次。  
{n,m}：匹配前面的子表达式至少n次，至多m次，n<=m。在逗号和两个数之间不能有空格。  
\*：匹配前面的子表达式零次或多次。 等价于{0,}。  
+：匹配前面的子表达式一次或多次。+ 等价于 {1,}。  
?：匹配前面的子表达式零次或一次。 等价于{0,1}。  
x|y：匹配x子表达式或者y子表达式，选择其一来生成字符串。  
[xyz]，字符集合，匹配中括号所包含的任意一个字符。  
[a-z]，字符范围。匹配指定范围内的任意字符。  
\d：匹配一个数字。   
\s：匹配一个空格。   
\S：匹配所有非空白字符。（不全）   
\w：匹配范围包含大小写字母、数字、下划线。   
\W：匹配非字母数字下划线。   
\num：匹配一个10^6以内的正整数。   
\z：匹配一个Unicode汉字，但不保证你认识。   
()：子表达式。  
##### 转义字符定义：  
在正常的正则表达式中，包含以下转义字符。  
\n、\r、\t、\b、\f、\0、\\\\、\\( and \\)、\\[ and \\]、\\{ and \\}、\\' and \\"、\\*、\\+、\\?  
另外，针对中括号中的转义，如果出现'\\'的话，那么后面的所有字符都会当作普通的转义来处理。  

最后在正则表达式结束的时候一定要用'$'作为结束标记。  

#### 3.stringtype for free  

本质上仍然是正则表达式，使用properties配置文件来对应名称和正则表达式。配置文件位置在config.properties文件的CustomStringtypeConf选项中进行指定，两个文件之间用';'进行分隔。  


## 配置文件config.properties的参数设置
### 数据库连接部分参数
DBsoftware:确定数据库软件，例如Oracle或者MySQL  
IP:目标数据库的地址  
port:目标数据库的端口  
user:目标数据库的登陆用户名  
password:目标数据库的登陆密码，可以留空  
database:目标数据库名称  

### 数据导入
toDB:传递给DB的方式，目前允许jdbc（通过jdbc传SQL），sql（写到对应的表名称的sql文件里），csv（通过写入类似外部表的文件载入），json（json字符串文件），mongo（MongoDB）。    
baseFileDir:数据文件写入位置，当toDB为jdbc的时候参数失效。    
canbeNegative:是否允许数值型数据出现负数，当指定输入范围时失效。    

### 性能参数
Optimal:SQL优化，（伪）随机缩短字符串长度，避免超长字符串的变量生成导致长时间卡顿。   
asynchronous:允许异步写文件以加快输出。  
TOTAL_THREADS:总线程数量，当asynchronous为true的时候整个任务将会被分成和线程数量同等的多个文件。  
defaultProportion:取值区间0~1，default值在整个字段中占用的默认比例，如果有使用default值的字段，加大该参数会适当的提高程序的性能，但是一定要按照实际的业务需求制定该参数的允许上限。  
WriterEngine:写文件使用的引擎，目前有default和apache两种，性能上差不多，不同条件下略有差异。  

## 已经支持通过JDBC直接发送数据的数据库
Oracle  
MySQL  
SQLserver  
DB2  
PostgreSQL  
SQLite  
H2  
MongoDB

## 注意事项
1.关于数据库连接选项的设置这里暂时不提供，请用配置文件实现。  
2.关于写文件引擎选项的设置这里暂时不提供，请用配置文件实现。  
3.如果通过JDBC方式直接写入数据库的话，由于JDBC本身效率不佳，并且为了保证数据库一致性，并不会开启异步写；也就是-a参数是无效的。  
4.目前文件路径不能带空格，因为暂时没加对于单双引号的参数识别。  
5.如果采用异步写的方式，primary key/unique约束在多个文件之间失效。如果建立基于全局的约束会导致性能下降。  
6.过大的表尽量不要使用primary key/unique约束，会导致异常的内存占用和性能下降。  
7.本工具不支持过分复杂的create，带有索引或者约束的SQL请将这些限制直接的写在对应字段后方，不要写在create括号的后面，否则会被掠过。  
8.目前不支持注释，包括--和/* */  
9.当前的多线程策略已经相对完善，在设置异步写入线程数的时候请尽量设置成CPU逻辑核心数量减一的倍数；尤其是Windows系统至少需要一
个核心作为系统调节的核心，占用所有的逻辑核心并不见得就会带来最大的吞吐量。  
