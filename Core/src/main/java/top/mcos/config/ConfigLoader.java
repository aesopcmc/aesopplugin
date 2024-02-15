package top.mcos.config;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import top.mcos.AesopPlugin;
import top.mcos.business.activity.config.ActivityConfig;
import top.mcos.business.yanhua.config.YanHuaConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathEntity;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathList;
import top.mcos.config.ann.PathValue;
import top.mcos.config.configs.BaseConfig;
import top.mcos.business.firework.config.FireworkConfig;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 配置文件加载
 */
public final class ConfigLoader {
    /**
     * config.yml 配置
     */
    public static BaseConfig baseConfig;
    /**
     * firework.yml 配置
     */
    public static FireworkConfig fireworkConfig;
    /**
     * yanhua.yml 配置
     */
    public static YanHuaConfig yanHuaConfig;
    /**
     * 活动配置
     */
    public static ActivityConfig activityConfig;

    // 自定义配置文件
    private final static Map<String, CustomerConfigFile> ccMap = new HashMap<>();

    public static synchronized void load(String configFile) {
        if(configFile==null) {
            baseConfig = initConfig(BaseConfig.class);
            fireworkConfig = initConfig(FireworkConfig.class);
            yanHuaConfig = initConfig(YanHuaConfig.class);
            activityConfig = initConfig(ActivityConfig.class);
        } else if(configFile.contains("yanhua")) {
            yanHuaConfig = initConfig(YanHuaConfig.class);
        }
        //System.out.println("主配置："+ baseConfig.getSettingConfig());
        //List<NoticeConfig> msgs = baseConfig.getNoticeConfigs();
        //for (NoticeConfig s : msgs) {
        //    System.out.println("消息条目："+s);
        //}

        //List<RegenWorldConfig> regens = baseConfig.getRegenWorldConfigs();
        //for (RegenWorldConfig s : regens) {
        //    System.out.println("重置世界条目："+s);
        //}

        //System.out.println("yanHuaConfig配置："+ yanHuaConfig);

    }

    private static <T> T initConfig(Class<T> configClass) {
        T object = null;
        String configFileName = configClass.getAnnotation(ConfigFileName.class).value();
        try {
            if (StringUtils.isNotBlank(configFileName)) {
                if ("config.yml".equals(configFileName)) {
                    // 加载config.yml
                    // 若配置文件不存在，自动根据resources/config.yml创建配置文件放置数据目录（/plugin/myplugin/config.yml）
                    AesopPlugin.getInstance().saveDefaultConfig();
                    // 重新读取配置到内存
                    AesopPlugin.getInstance().reloadConfig();
                    // 封装为对象管理
                    object = readConfig(configClass, null, AesopPlugin.getInstance().getConfig());
                } else {
                    // 加载自定义配置文件xxx.yml
                    CustomerConfigFile fireworkConfigFile = ccMap.get(configFileName);
                    if (fireworkConfigFile == null) {
                        fireworkConfigFile = new CustomerConfigFile(configFileName);
                        ccMap.put(configFileName, fireworkConfigFile);
                    }
                    fireworkConfigFile.saveDefaultConfig();
                    fireworkConfigFile.reloadConfig();
                    object = readConfig(configClass, null, fireworkConfigFile.getConfig());
                }
            }
        } catch (Exception e) {
            AesopPlugin.logger.log(configFileName+"配置加载出错，已跳过", ConsoleLogger.Level.ERROR);
            e.printStackTrace();
            try {
                object = configClass.getConstructor().newInstance();
            } catch (Throwable e1) {
                e1.printStackTrace();
            }
        }
        return object;
    }

    public static synchronized void saveConfig(Object configObject) {
        //System.out.println("当前对象：" + configObject);
        Class<?> clazz = configObject.getClass();
        Field[] fields = clazz.getDeclaredFields();

        // 识别是否是是自定义配置文件
        CustomerConfigFile customerConfigFile = null;
        String configFileName = clazz.getAnnotation(ConfigFileName.class).value();
        if(StringUtils.isNotBlank(configFileName) && !"config.yml".equals(configFileName)) {
            customerConfigFile = ccMap.get(configFileName);
            if (customerConfigFile==null) throw new RuntimeException("无法找到配置文件："+configFileName);
        }

        String key="";

        // 获取key值
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PathKey.class)) {
                try {
                    Object fieldValue = field.get(configObject);
                    key = (String) fieldValue;
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    AesopPlugin.logger.log("获取{key}出错");
                }
            }
        }

        // 设置每个字段的值
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PathValue.class)) {
                PathValue annotation = field.getAnnotation(PathValue.class);
                String annotationValue = annotation.value();
                // 路径替换{key}
                annotationValue = annotationValue.replace("{key}", key);

                Type genericType = field.getGenericType();
                if (Date.class.getTypeName().equals(genericType.getTypeName())) {
                    // 日期类型特殊处理转换
                    try {
                        Object dateValue = field.get(configObject);
                        if(dateValue!=null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateStrValue = sdf.format(dateValue);
                            if(customerConfigFile==null) {
                                AesopPlugin.getInstance().getConfig().set(annotationValue, dateStrValue);
                            } else {
                                customerConfigFile.getConfig().set(annotationValue, dateStrValue);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AesopPlugin.logger.log("设置配置【"+annotationValue+"】出错，日期格式转换有误", ConsoleLogger.Level.ERROR);
                    }
                } else {
                    try {
                        Object fieldValue = field.get(configObject);
                        if(customerConfigFile==null) {
                            AesopPlugin.getInstance().getConfig().set(annotationValue, fieldValue);
                        } else {
                            //System.out.println("保存配置: " + annotationValue + ", 值: "+fieldValue);
                            customerConfigFile.getConfig().set(annotationValue, fieldValue);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        AesopPlugin.logger.log("设置配置【"+annotationValue+"】出错", ConsoleLogger.Level.ERROR);
                    }
                }
            }
        }

        // 更新内存配置到data配置文件
        if(customerConfigFile==null) {
            AesopPlugin.getInstance().saveConfig();
        } else {
            //System.out.println("保存");
            customerConfigFile.saveConfig();
        }
    }

    private static <T> T readConfig(Class<T> clazz, String key, FileConfiguration configuration) throws Exception {
        T object = clazz.getConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        // 遍历所有属性
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PathKey.class)) {
                if (key != null) {
                    try {
                        field.set(object, key);
                    } catch (IllegalAccessException e) {
                        AesopPlugin.logger.log("【" + key + "】配置加载出错，已跳过", ConsoleLogger.Level.ERROR);
                    }
                }
            } else if (field.isAnnotationPresent(PathValue.class)) {
                PathValue annotation = field.getAnnotation(PathValue.class);
                Type genericType = field.getGenericType();
                //System.out.println(genericType);
                String annotationValue = "";
                try {
                    if (key != null) {
                        annotationValue = annotation.value().replace("{key}", key);
                    } else {
                        annotationValue = annotation.value();
                    }
                    Object v = configuration.get(annotationValue);

                    if (Date.class.getTypeName().equals(genericType.getTypeName())) {
                        // 日期类型特殊处理转换
                        try {
                            if (v != null && StringUtils.isNotBlank(v.toString())) {
                                field.set(object, DateUtils.parseDate(v.toString(), "yyyy-MM-dd HH:mm:ss"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AesopPlugin.logger.log(annotationValue + "日期【" + v + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
                        }
                    } else {
                        field.set(object, v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AesopPlugin.logger.log("【" + annotationValue + "】配置加载出错，已跳过", ConsoleLogger.Level.ERROR);
                }
            } else if (field.isAnnotationPresent(PathEntity.class)) {
                Class<?> actualClass = field.getType();
                Object ins = readConfig(actualClass, null, configuration);
                try {
                    field.set(object, ins);
                } catch (IllegalAccessException e) {
                    AesopPlugin.logger.log("【" + actualClass + "】对象加载失败，已跳过", ConsoleLogger.Level.ERROR);
                }
            } else if (field.isAnnotationPresent(PathList.class)) {
                if (field.getGenericType() instanceof ParameterizedType genericType) {
                    Type rawType = genericType.getRawType();
                    // 读取列表对象数据
                    if (List.class.getTypeName().equals(rawType.getTypeName())) {
                        // 取得列表的泛型类型
                        Type[] actualTypeArguments = genericType.getActualTypeArguments();
                        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                            Class<?> actualClass = (Class<?>) actualTypeArguments[0];
                            //System.out.println("成员方法返回的泛型信息：" + actualClass);

                            PathList classAnnotation = field.getAnnotation(PathList.class);
                            //System.out.println("类注解值:"+classAnnotation.value());
                            if (classAnnotation != null) {
                                List list = new ArrayList();
                                ConfigurationSection cf = configuration.getConfigurationSection(classAnnotation.value());
                                if(cf!=null) {
                                    Map<String, Object> subConfigs = cf.getValues(false);
                                    subConfigs.forEach((k, value) -> {
                                        //ConfigurationSection section = (ConfigurationSection) value;
                                        Object subObject = null;
                                        try {
                                            subObject = readConfig(actualClass, k, configuration);
                                        } catch (Exception e) {
                                            AesopPlugin.logger.log("【" + k + "】集合配置加载出错", ConsoleLogger.Level.ERROR);
                                            e.printStackTrace();
                                        }
                                        if (subObject != null) {
                                            list.add(subObject);
                                        }
                                    });
                                }

                                try {
                                    field.set(object, list);
                                } catch (IllegalAccessException e) {
                                    AesopPlugin.logger.log("【" + actualClass + "】对象集合加载失败，已跳过", ConsoleLogger.Level.ERROR);
                                }
                            }
                        }
                    }
                }
            }
        }
        return object;
    }

}
