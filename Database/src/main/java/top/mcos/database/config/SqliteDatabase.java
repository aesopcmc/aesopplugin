package top.mcos.database.config;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.database.dao.PlayerStatisticDao;
import top.mcos.database.dao.impl.GiftClaimRecordDaoImpl;
import top.mcos.database.dao.impl.GiftItemDaoImpl;
import top.mcos.database.dao.impl.PlayerFireworkDaoImpl;
import top.mcos.database.dao.impl.PlayerStatisticDaoImpl;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.domain.PlayerFirework;
import top.mcos.database.domain.PlayerStatistic;

import java.io.File;
import java.sql.SQLException;

/**
 * Sqlite数据库配置
 */
@Getter
public class SqliteDatabase {
    private ConnectionSource connectionSource;
    private GiftClaimRecordDao giftClaimRecordDao;
    private GiftItemDao giftItemDao;
    private PlayerFireworkDao playerFireworkDao;
    private PlayerStatisticDao playerStatisticDao;

    public SqliteDatabase(String dataFolder) {
        // this uses h2 by default but change to match your database
        // create a connection source to our database
        // 使用数据库事务
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Transactions
        // DAO使用示例
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#DAO-Usage
        // 复杂原生sql 查询语法
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Raw-Statements
        // 普通查询示例：
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Select-Arguments
        // 使用列参数
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Column-Arguments
        // 连接查询
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Join-Queries
        // 条件更新
        // https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Building-Statements

        try {
            // 获得JDBC连接
            connectionSource = new JdbcConnectionSource(getJdbcUrl(dataFolder, "database"), null, null);

            // 实例化Dao
            giftClaimRecordDao = new GiftClaimRecordDaoImpl(connectionSource, GiftClaimRecord.class);
            giftItemDao = new GiftItemDaoImpl(connectionSource, GiftItem.class);
            playerFireworkDao = new PlayerFireworkDaoImpl(connectionSource, PlayerFirework.class);
            playerStatisticDao = new PlayerStatisticDaoImpl(connectionSource, PlayerStatistic.class);

            // 如果表不存在，创建默认的表
            TableUtils.createTableIfNotExists(connectionSource, GiftClaimRecord.class);
            TableUtils.createTableIfNotExists(connectionSource, GiftItem.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerFirework.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerStatistic.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getJdbcUrl(String dataPath, String database) {
        return "jdbc:sqlite:" + dataPath + File.separator + database + ".db";
    }

    // 使用示例
    /*
        GiftlogDao giftlogDao = database.getGiftlogDao();
        Giftlog giftlog = new Giftlog();
        giftlog.setPlayerId("pid123");
        giftlog.setPlayerName("aesop");
        giftlog.setGiftType(1);
        giftlog.setCreateTime(DateUtils.toCalendar(new Date()).toString());
        try {
            // 创建数据
            giftlogDao.create(giftlog);

            List<Giftlog> datalist = giftlogDao.queryBuilder()
                    //.where().eq("", "")
                    .query();


            System.out.println("输出数据：" + datalist);
        } catch (SQLException e) {
            e.printStackTrace();
        }
     */

}
