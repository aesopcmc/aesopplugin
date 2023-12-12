package top.mcos.config;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.BaseConfig;
import top.mcos.config.configs.NoticeConfig;
import top.mcos.config.configs.RegenWorldConfig;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 配置文件加载
 */
public class ConfigLoader {
    public static BaseConfig baseConfig;

    public static synchronized void load() {
        //重新读取配置
        AesopPlugin.getInstance().reloadConfig();
        baseConfig = readConfig(BaseConfig.class, null);
        if(baseConfig ==null) {
            baseConfig = new BaseConfig();

        }
        //System.out.println("主配置："+ baseConfig);
        //List<NoticeConfig> msgs = baseConfig.getNoticeConfigs();
        //for (NoticeConfig s : msgs) {
        //    System.out.println("消息条目："+s);
        //}
        //List<RegenWorldConfig> regens = baseConfig.getRegenWorldConfigs();
        //for (RegenWorldConfig s : regens) {
        //    System.out.println("重置世界条目："+s);
        //}
        //loadRegenWorlds();
        //loadNoticeMessages();
    }

    public static synchronized void saveConfig(Object configObject) {
        Class<?> clazz = configObject.getClass();
        Field[] fields = clazz.getDeclaredFields();

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
                            AesopPlugin.getInstance().getConfig().set(annotationValue, dateStrValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AesopPlugin.logger.log("设置配置【"+annotationValue+"】出错，日期格式转换有误", ConsoleLogger.Level.ERROR);
                    }
                } else {
                    try {
                        Object fieldValue = field.get(configObject);
                        AesopPlugin.getInstance().getConfig().set(annotationValue, fieldValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        AesopPlugin.logger.log("设置配置【"+annotationValue+"】出错", ConsoleLogger.Level.ERROR);
                    }
                }
            }
        }

        // 更新内存配置到data配置文件
        AesopPlugin.getInstance().saveConfig();
    }

    private static <T> T readConfig(Class<T> clazz, String key) {
        T object;
        try {
            object = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        // 遍历所有属性
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(PathKey.class)) {
                if(key!=null) {
                    try {
                        field.set(object, key);
                    } catch (IllegalAccessException e) {
                        AesopPlugin.logger.log("【" + key +"】配置加载出错，已跳过", ConsoleLogger.Level.ERROR);
                    }
                }
            }else if (field.isAnnotationPresent(PathValue.class)) {
                PathValue annotation = field.getAnnotation(PathValue.class);
                Type genericType = field.getGenericType();
                //System.out.println(genericType);
                String annotationValue="";
                try {
                    if(key!=null ) {
                        annotationValue = annotation.value().replace("{key}", key);
                    } else {
                        annotationValue = annotation.value();
                    }
                    Object v = AesopPlugin.getInstance().getConfig().get(annotationValue);

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
                    AesopPlugin.logger.log("【" + annotationValue +"】配置加载出错，已跳过", ConsoleLogger.Level.ERROR);
                }
            } else {
                // 列表对象
                if(field.getGenericType() instanceof ParameterizedType genericType) {
                    Type rawType = genericType.getRawType();
                    if(List.class.getTypeName().equals(rawType.getTypeName())) {
                        // 是列表对象属性

                        // 取得泛型类型
                        Type[] actualTypeArguments = genericType.getActualTypeArguments();
                        if(actualTypeArguments!=null && actualTypeArguments.length>0) {
                            Class<?> actualClass = (Class<?>) actualTypeArguments[0];
                            //System.out.println("成员方法返回的泛型信息：" + actualClass);

                            PathEntity classAnnotation = actualClass.getAnnotation(PathEntity.class);
                            //System.out.println("类注解值:"+classAnnotation.value());
                            if(classAnnotation!=null) {
                                List list = new ArrayList();

                                Map<String, Object> worlds = AesopPlugin.getInstance().getConfig()
                                        .getConfigurationSection(classAnnotation.value()).getValues(false);
                                worlds.forEach((k, value) -> {
                                    //ConfigurationSection section = (ConfigurationSection) value;
                                    Object subObject = readConfig(actualClass, k);
                                    if(subObject!=null) {
                                        list.add(subObject);
                                    }
                                });
                                try {
                                    field.set(object, list);
                                } catch (IllegalAccessException e) {
                                    AesopPlugin.logger.log("【" + actualClass +"】对象集合加载失败，已跳过", ConsoleLogger.Level.ERROR);
                                }
                            }
                        }

                    }
                }
            }
        }
        return object;
    }

    //private static synchronized void loadRegenWorlds() {
    //    regenWorldConfigs.clear();
    //    Map<String, Object> worlds = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.regen-world").getValues(false);
    //    worlds.forEach((key,value)->{
    //        try {
    //            ConfigurationSection section = (ConfigurationSection)value;
    //
    //            boolean enable = section.getBoolean("enable");
    //            String cron = section.getString("cron");
    //            boolean newSeed = section.getBoolean("new-seed");
    //            boolean randomSeed = section.getBoolean("random-seed");
    //            String seed = section.getString("seed");
    //            boolean keepGameRules = section.getBoolean("keep-game-rules");
    //            boolean afterLoadChunky = section.getBoolean("after-load-chunky");
    //            double afterLoadChunkyRadius = section.getDouble("after-load-chunky-radius");
    //            String afterNoticeKey = section.getString("after-notice-key");
    //            List<String> afterRunCommands = section.getStringList("after-run-commands");
    //
    //            RegenWorldConfig config = new RegenWorldConfig();
    //            config.setKey(key);
    //            config.setEnable(enable);
    //            config.setCron(cron);
    //            config.setNewSeed(newSeed);
    //            config.setRandomSeed(randomSeed);
    //            config.setSeed(seed);
    //            config.setKeepGameRules(keepGameRules);
    //            config.setAfterLoadChunky(afterLoadChunky);
    //            config.setAfterLoadChunkyRadius(afterLoadChunkyRadius);
    //            config.setAfterNoticeKey(afterNoticeKey);
    //            config.setAfterRunCommands(afterRunCommands);
    //            regenWorldConfigs.add(config);
    //        }catch (Exception e) {
    //            e.printStackTrace();
    //            AesopPlugin.logger.log(key + "配置加载出错，已跳过");
    //        }
    //    });
    //}
    //
    ///**
    // * 加载消息配置
    // */
    //public static synchronized void loadNoticeMessages() {
    //    noticeMessageConfigs.clear();
    //    // 加载配置，注册定时任务，注入数据
    //    Map<String, Object> msgMap = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.notice").getValues(false);
    //    msgMap.forEach((key,value)->{
    //        try {
    //            ConfigurationSection section = (ConfigurationSection) value;
    //            boolean enable = section.getBoolean("enable");
    //            String cron = section.getString("cron");
    //            String startStr = section.getString("start");
    //            Date start = null;
    //            if (StringUtils.isNotBlank(startStr)) {
    //                try {
    //                    start = DateUtils.parseDate(startStr, "yyyy-MM-dd HH:mm:ss");
    //                } catch (ParseException e) {
    //                    AesopPlugin.logger.log("start日期【" + startStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
    //                }
    //            }
    //            String endStr = section.getString("end");
    //            Date end = null;
    //            if (StringUtils.isNotBlank(endStr)) {
    //                try {
    //                    end = DateUtils.parseDate(endStr, "yyyy-MM-dd HH:mm:ss");
    //                } catch (ParseException e) {
    //                    AesopPlugin.logger.log("end日期【" + endStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
    //                }
    //            }
    //            String position = section.getString("position");
    //            String message = section.getString("message");
    //            String subMessage = section.getString("sub-message");
    //            NoticeMessageConfig msg = new NoticeMessageConfig();
    //            msg.setKey(key);
    //            msg.setEnable(enable);
    //            msg.setCron(cron);
    //            msg.setStart(start);
    //            msg.setEnd(end);
    //            msg.setPositionType(position);
    //            msg.setMessage(message);
    //            msg.setSubMessage(subMessage);
    //            noticeMessageConfigs.add(msg);
    //        }catch (Exception e) {
    //            e.printStackTrace();
    //            AesopPlugin.logger.log(key + "消息配置加载出错，已跳过");
    //        }
    //    });
    //}
}
