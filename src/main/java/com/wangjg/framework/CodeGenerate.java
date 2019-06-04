package com.wangjg.framework;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.*;

/**
 * @author wangjg
 * 2019-06-04
 */
public class CodeGenerate {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main1(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");

//        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("wangjg");
        gc.setOpen(false);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://47.104.200.143:7306/demo?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("d2FuZ2pnMDcyNA==");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
//        pc.setModuleName(scanner("模块名"));
        final String moduleName = scanner("模块名");


        Map<String, String> pathInfo = new HashMap<>();

        String entityPath = projectPath + "/src/main/java/com/wangjg/framework" + "/pojo/entity/" + moduleName;
        pathInfo.put(ConstVal.ENTITY_PATH, entityPath);

        String mapperPath = projectPath + "/src/main/java/com/wangjg/framework" + "/mapper/" + moduleName;
        pathInfo.put(ConstVal.MAPPER_PATH, mapperPath);

        String servicePath = projectPath + "/src/main/java/com/wangjg/framework" + "/service/" + moduleName;
        pathInfo.put(ConstVal.SERVICE_PATH, servicePath);

        String serviceImplPath = projectPath + "/src/main/java/com/wangjg/framework" + "/service/impl/" + moduleName;
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, serviceImplPath);

        String controllerPath = projectPath + "/src/main/java/com/wangjg/framework" + "/controller/" + moduleName;
        pathInfo.put(ConstVal.CONTROLLER_PATH, controllerPath);

        pc.setPathInfo(pathInfo);
        pc.setParent("com.wangjg.framework");

        pc.setController("controller." + moduleName);
        pc.setService("service." + moduleName);
        pc.setServiceImpl("service.impl." + moduleName);
        pc.setMapper("mapper." + moduleName);
        pc.setXml("mapper." + moduleName);
        pc.setEntity("pojo.entity." + moduleName);

        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                final HashMap<String, Object> map = new HashMap<>();
                map.put("voPackage", pc.getParent() + ".pojo.vo" + StringPool.DOT + moduleName);
                this.setMap(map);
            }
        };

        // 如果模板引擎是 freemarker
        //String mapperDotXmltemplatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        String mapperDotXmlTemplatePath = "/templates/mapper.xml.vm";
        String voTemplatePath = "/templates/vo.java.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(mapperDotXmlTemplatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/com/wangjg/framework/mapper/" + moduleName
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });


        focList.add(new FileOutConfig(voTemplatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/com/wangjg/framework/pojo/vo/" + moduleName
                        + "/" + tableInfo.getEntityName() + "VO" + StringPool.DOT_JAVA;
            }
        });

        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录");
                return false;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/entity.java");
        templateConfig.setService("templates/service.java");
        templateConfig.setController("templates/controller.java");
        templateConfig.setXml("templates/mapper.xml");
        templateConfig.setMapper("templates/mapper.java");
        templateConfig.setService("templates/service.java");
        templateConfig.setServiceImpl("templates/serviceImpl.java");

        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
//        strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
//        strategy.setSuperEntityColumns("id");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(scanner("table_prefix"));
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }


}
