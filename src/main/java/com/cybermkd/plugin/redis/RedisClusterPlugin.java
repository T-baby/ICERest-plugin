package com.cybermkd.plugin.redis;

import com.cybermkd.common.Plugin;
import com.cybermkd.common.util.Stringer;
import com.cybermkd.log.Logger;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.Set;

/**
 * 创建人:T-baby
 * 创建日期: 16/7/5
 * 文件描述:
 */
public class RedisClusterPlugin implements Plugin{
    // 集群名称
    String clusterName = null;

    // 集群对象
    JedisCluster jedisCluster = null;

    // 超时时间
    Integer timeout = null;

    // 连接池
    GenericObjectPoolConfig poolConfig = null;

    // 最多重定向次数
    Integer maxRedirections = null;

    // 集群地址集合
    Set<HostAndPort> redisClusterNodes;

    private Logger logger=Logger.getLogger(RedisClusterPlugin.class);

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes) {

        // 检查数据
        this.isRightHostAndPortSet(clusterName, redisClusterNodes);

        // 绑定集群名称
        this.clusterName = clusterName;

        // 绑定地址集合
        this.redisClusterNodes = redisClusterNodes;

    }

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     * @param timeout 超时时间
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes, Integer timeout) {

        // 复用传入集群方法
        this(clusterName, redisClusterNodes);

        // 超时时间绑定
        this.timeout = timeout;

    }

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     * @param  poolConfig 连接池对象
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes, GenericObjectPoolConfig poolConfig) {

        // 复用传入集群方法
        this(clusterName, redisClusterNodes);

        // 连接池绑定
        this.poolConfig = poolConfig;

    }

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     * @param timeout 超时时间
     * @param poolConfig 连接池配置
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes, Integer timeout, GenericObjectPoolConfig poolConfig) {

        // 复用传入集群方法
        this(clusterName, redisClusterNodes, timeout);

        // 连接池绑定
        this.poolConfig = poolConfig;

    }

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes, Integer timeout, Integer maxRedirections) {

        // 复用传入集群方法
        this(clusterName, redisClusterNodes, timeout);

        // 连接池绑定
        this.maxRedirections = maxRedirections;

    }

    /**
     *
     * 传入集群信息
     *
     * @param clusterName 集群名称
     * @param redisClusterNodes 集群地址集合
     * @param  poolConfig 连接池对象
     *
     */
    public RedisClusterPlugin(String clusterName, Set<HostAndPort> redisClusterNodes, Integer timeout, Integer maxRedirections, GenericObjectPoolConfig poolConfig) {

        // 复用传入集群方法
        this(clusterName, redisClusterNodes, timeout, maxRedirections);

        // 连接池绑定
        this.poolConfig = poolConfig;

    }

    @Override
    public boolean start() {

        if(timeout != null && maxRedirections != null && poolConfig != null) {
            jedisCluster = new JedisCluster(redisClusterNodes, timeout, maxRedirections, poolConfig);
        } else if(timeout != null && maxRedirections != null) {
            jedisCluster = new JedisCluster(redisClusterNodes, timeout, maxRedirections);
        } else if(timeout != null && poolConfig != null) {
            jedisCluster = new JedisCluster(redisClusterNodes, timeout, poolConfig);
        } else if(timeout != null) {
            jedisCluster = new JedisCluster(redisClusterNodes, timeout);
        } else if(poolConfig != null){
            jedisCluster = new JedisCluster(redisClusterNodes, poolConfig);
        } else {
            jedisCluster = new JedisCluster(redisClusterNodes);
        }

        // 加入集群集合
        RedisCluster.addCache(clusterName, jedisCluster);

        return true;
    }

    @Override
    public boolean stop() {

        // 清除出集群集合
        JedisCluster removeRedisCluster = RedisCluster.removeCache(clusterName);

        // 关闭集群链接
        try {
            removeRedisCluster.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return false;

    }

    // 判断传入的集群位置资料是否正确
    private void isRightHostAndPortSet(String clusterName, Set<HostAndPort> redisClusterNodes) {

        // 集群名称不能为空
        if (Stringer.isBlank(clusterName)) {
            throw new IllegalArgumentException("clusterName can not be blank.");
        }

        // 检查集群具体地址和端口号是否正常
        if(redisClusterNodes != null && redisClusterNodes.size()>0) {
            for(HostAndPort hap : redisClusterNodes) {

                // 获取主机ip
                String host = hap.getHost();

                // 空字符串
                if (Stringer.isBlank(host)) {
                    throw new IllegalArgumentException("host can not be blank.");
                }

                // 获取端口
                Integer port = hap.getPort();

                // 空端口数据
                if(port == null) {
                    throw new IllegalArgumentException("port can not be blank.");
                }

            }
        } else {

            // 集群集合数据为空
            throw new IllegalArgumentException("redisClusterNodes can not be blank.");

        }

    }
}
