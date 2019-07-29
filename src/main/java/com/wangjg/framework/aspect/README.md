* 什么是AOP  
    AOP，即面向切面编程，Aspect Oriented Programming，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。它和我们平时接触到的OOP都是编程的不同思想。OOP，即『面向对象编程』，它提倡的是将功能模块化，对象化。AOP提倡的是针对同一类问题的统一处理，可以让我们在开发过程中把注意力尽可能多地放在真正需要关心的核心业务逻辑处理上，既提高工作效率，又使代码变得更加简洁优雅。
* 关于AOP的一些概念  
    * 切面（Aspect）：一个关注点的模块化，这个关注点实现可能另外横切多个对象。其实就是共有功能的实现。如日志切面、权限切面、事务切面等。
    * 通知（Advice）：是切面的具体实现。以目标方法为参照点，根据放置的地方不同，可分为前置通知（Before）、后置通知（AfterReturning）、异常通知（AfterThrowing）、最终通知（After）与环绕通知（Around）5种。在实际应用中通常是切面类中的一个方法，具体属于哪类通知由配置指定的。
    * 切入点（Pointcut）：用于定义通知应该切入到哪些连接点上。不同的通知通常需要切入到不同的连接点上，这种精准的匹配是由切入点的正则表达式来定义的。
    * 连接点（Joinpoint）：就是程序在运行过程中能够插入切面的地点。例如，方法调用、异常抛出或字段修改等。
    * 目标对象（Target Object）：包含连接点的对象，也被称作被通知或被代理对象。这些对象中已经只剩下干干净净的核心业务逻辑代码了，所有的共有功能等代码则是等待AOP容器的切入。
    * AOP代理（AOP Proxy）：将通知应用到目标对象之后被动态创建的对象。可以简单地理解为，代理对象的功能等于目标对象的核心业务逻辑功能加上共有功能。代理对象对于使用者而言是透明的，是程序运行过程中的产物。
    * 编织（Weaving）：将切面应用到目标对象从而创建一个新的代理对象的过程。这个过程可以发生在编译期、类装载期及运行期，当然不同的发生点有着不同的前提条件。譬如发生在编译期的话，就要求有一个支持这种AOP实现的特殊编译器（如AspectJ编译器）；发生在类装载期，就要求有一个支持AOP实现的特殊类装载器；只有发生在运行期，则可直接通过Java语言的反射机制与动态代理机制来动态实现（如Spring）。
    * 引入（Introduction）：添加方法或字段到被通知的类。
* 代理  
    AOP的实现手段之一是建立在Java语言的反射机制与动态代理机制之上的。业务逻辑组件在运行过程中，AOP容器会动态创建一个代理对象供使用者调用，该代理对象已经按程序员的意图将切面成功切入到目标方法的连接点上，从而使切面的功能与业务逻辑的功能都得以执行。从原理上讲，调用者直接调用的其实是AOP容器动态生成的代理对象，再由代理对象调用目标对象完成原始的业务逻辑处理，而代理对象则已经将切面与业务逻辑方法进行了合成。
* 静态代理  
    静态代理的实现比较简单，代理类通过实现与目标对象相同的接口，并在类中维护一个代理对象。通过构造器塞入目标对象，赋值给代理对象，进而执行代理对象实现的接口方法，并实现前拦截，后拦截等所需的业务功能。
        * AspectJ
        * 编程代理
* 动态代理  
        动态代理分为JDK动态代理和CGLib动态代理，而代码织入方式分为动态织入与静态织入两大类，关于代理模式的一些细节不是本文的重点，在 谈谈23种设计模式在Android源码及项目中的应用 中的代理模式章节已经有讲述过。
        * JDK动态代理
        * CGLib动态代理
* 什么是AspectJ  
    AspectJ会通过生成新的AOP代理类来对目标类进行增强，有兴趣的同学可以去查看经过ajc编译前后的代码，比照一下就会发现，假设我们要切入一个方法，那么AspectJ会重构一个新的方法，并且将原来的方法替代为这个新的方法，这个新的方法就会根据配置在调用目标方法的前后等指定位置插入特定代码，这样系统在调用目标方法的时候，其实是调用的被AspectJ增强后的代理方法，而这个代理类会在编译结束时生成好，所以属于静态织入的方式。
    ```text
     AspectJ是eclipse基金会的一个项目，官网地址：[AspectJ](https://www.eclipse.org/aspectj/docs.php)
    ```
* AspectJ机制  
    * 切面语法  
        网上到处都是的那种所谓”AspectJ使用方法”，这套东西做到了将 决定是否使用切面的权利，还给了切面。在写切面的时候就可以决定哪些类的哪些方法会被代理，从而 从逻辑上不需要侵入业务代码。由于这套语法实在是太有名，导致很多人都误以为AspectJ等于切面语法，其实不然。
    * 织入工具  
        刚才讲到切面语法能够让切面 从逻辑上与业务代码解耦，但是 从操作上来讲，当JVM运行业务代码的时候，他甚至无从得知旁边还有个类想横插一刀。。。这个问题大概有两种解决思路，一种就是提供注册机制，通过额外的配置文件指明哪些类受到切面的影响，不过这还是需要干涉对象创建的过程；另外一种解决思路就是在编译期(或者类加载期)我们优先考虑一下切面代码，并将切面代码通过某种形式插入到业务代码中，这样业务代码不就知道自己被“切”了么？这种思路的一个实现就是 aspectjweaver，就是这里的织入工具。
* 织入方式  
    * 编译时织入，利用ajc编译器替代javac编译器，直接将源文件(java或者aspect文件)编译成class文件并将切面织入进代码。
    * 编译后织入，利用ajc编译器向javac编译期编译后的class文件或jar文件织入切面代码。
    * 加载时织入，不使用ajc编译器，利用aspectjweaver.jar工具，使用java agent代理命令在类加载期将切面织入进代码。
* 怎么使用AspectJ  
    AspectJ提供了两套对切面的描述方法
    * 一种就是我们常见的 基于java注解切面描述的方法，这种方法兼容java语法，写起来十分方便，不需要IDE的额外语法检测支持；
    * 另外一种是基于aspect文件的切面文件描述方法，这种语法本身并不是java语法，因此写的时候需要IDE的插件支持才能进行语法检查。
* AspectJ文件  
    * aspectjrt.jar包主要是提供运行时的一些注解，静态方法等等东西，通常我们要使用aspectJ的时候都要使用这个包。
    * aspectjtools.jar包主要是提供赫赫有名的ajc编译器，可以在编译期将将java文件或者class文件或者aspect文件定义的切面织入到业务代码中。通常这个东西会被封装进各种IDE插件或者自动化插件中。
    * aspectjweaver.jar包主要是提供了一个java agent用于在类加载期间织入切面(Load time weaving)。并且提供了对切面语法的相关处理等基础方法，供ajc使用或者供第三方开发使用。这个包一般我们不需要显式引用，除非需要使用LTW。
* 织入命令
    * 编译时织入，直接从java源码编译成class java -jar aspectjtools.jar -cp aspectjrt.jar -source 1.5 -sourceroots src/main/java/ -d target/classes
    * 编译后织入，从java编译后的class再次编译class
        ```jshelllanguage
        java -jar aspectjtools.jar -cp aspectjrt.jar -source 1.5 -inpath target/classes -d target/classes
        ```
    * 加载时织入(LTW)，java -javaagent:aspectjweaver.jar -cp aspectjrt.jar:target/classes/ com.scio.cloud.test.App
* @PointCut详解  
    1. execute表达式
        * 拦截任意公共方法
            ```text
            execution(public* *(..))
            ```
        * 拦截以set开头的任意方法
            ```text
            execution(* set*(..))
            ```
        * 拦截类或者接口中的方法
            ```text
            execution(* com.xyz.service.AccountService.*(..))
            ```
            拦截AccountService(类、接口)中定义的所有方法
        * 拦截包中定义的方法，不包含子包中的方法
            ```text
            execution(* com.xyz.service.*.*(..))
            ```
            拦截com.xyz.service包中所有类中任意方法，不包含子包中的类
        * 拦截包或者子包中定义的方法
            ```text
            execution(* com.xyz.service..*.*(..))
            ```
            拦截com.xyz.service包或者子包中定义的所有方法
    2. within表达式  
        表达式格式：包名.* 或者 包名..*
        * 拦截包中任意方法，不包含子包中的方法
            ```text
            within(com.xyz.service.*)
            ```
            拦截service包中任意类的任意方法
        * 拦截包或者子包中定义的方法
            ```text
            within(com.xyz.service..*)
            ```
            拦截service包及子包中任意类的任意方法
    3. this/target表达式
        * this 表达式  
            代理对象为指定的类型会被拦截  
            **目标对象使用aop之后生成的代理对象必须是指定的类型才会被拦截，注意是目标对象被代理之后生成的代理对象和指定的类型匹配才会被拦截**
        * target表达式  
            目标对象为指定的类型被拦截
            **目标对象（被代理对象）的类型和指定的类型匹配的才会被拦截**
        
        this作用于代理对象，target作用于目标对象  
        this表示目标对象被代理之后生成的代理对象和指定的类型匹配会被拦截，匹配的是代理对象  
        target表示目标对象和指定的类型匹配会被拦截，匹配的是目标对象  
    4. args 表达式  
        * 匹配方法中的参数
            ```text
            @Pointcut("args(com.ms.aop.args.demo1.UserModel)")
            ```
            匹配只有一个参数，且类型为com.ms.aop.args.demo1.UserModel
        * 匹配多个参数
            ```text
            args(type1,type2,typeN)
            ```
        * 匹配任意多个参数
            ```text
            @Pointcut("args(com.ms.aop.args.demo1.UserModel,..)")
            ```
            匹配第一个参数类型为com.ms.aop.args.demo1.UserModel的所有方法, .. 表示任意个参数
    5. @target/@within表达式
        * @target表达式  
            匹配的目标对象的类有一个指定的注解
            ```text
            @target(com.ms.aop.jtarget.Annotation1)
            ```
            目标对象中包含com.ms.aop.jtarget.Annotation1注解，调用该目标对象的任意方法都会被拦截
        * @within表达式  
            指定匹配必须包含某个注解的类里的所有连接点
            ```text
            @within(com.ms.aop.jwithin.Annotation1)
            ```
            声明有com.ms.aop.jwithin.Annotation1注解的类中的所有方法都会被拦截
        * @target 和 @within 的不同点
            @target(注解A)：判断被调用的目标对象中是否声明了注解A，如果有，会被拦截  
            @within(注解A)： 判断被调用的方法所属的类中是否声明了注解A，如果有，会被拦截  
            @target关注的是被调用的对象，@within关注的是调用的方法所在的类
    6. @annotation表达式  
        匹配有指定注解的方法（注解作用在方法上面）
        ```text
        @annotation(com.ms.aop.jannotation.demo2.Annotation1)
        ```
        被调用的方法包含指定的注解
    7. @args表达式  
        方法参数所属的类型上有指定的注解，被匹配  
        注意：是方法参数所属的类型上有指定的注解，不是方法参数中有注解  
        * 匹配1个参数，且第1个参数所属的类中有Anno1注解
            ```text
            @args(com.ms.aop.jargs.demo1.Anno1)
            ```
        * 匹配多个参数，且多个参数所属的类型上都有指定的注解
            ```text
            @args(com.ms.aop.jargs.demo1.Anno1,com.ms.aop.jargs.demo1.Anno2)
            ```
        * 匹配多个参数，且第一个参数所属的类中有Anno1注解
            ```text
            @args(com.ms.aop.jargs.demo2.Anno1,..)
            ```    
* spring 创建一个基本的日志切面类，需要包含以下的要点：  
    1. 添加@Aspect注解，声明是切片
    2. 添加@Component注解，将其作为一个元素注入进去
    3. 使用@Pointcut定义一个切入点，可以是一个规则表达式，比如下例中某个package下的所有函数
    4. 根据需要在需要切片的地方切入内容
        - 使用@Before在切入点开始处切入内容
        - 使用@After在切入点结尾处切入内容
        - 使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
        - 使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
        - 使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
    