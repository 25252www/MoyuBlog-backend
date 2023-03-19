package com.moyu.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IPUtils {
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress != null && ipAddress.length() != 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ipAddress.indexOf(",") != -1) {
                    ipAddress = ipAddress.split(",")[0];
                }
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }
        return ipAddress;
    }

    public static String getCityInfo(String ip) throws Exception {
        // 读取resources下的ip2region.xdb文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:ip2region.xdb");
        InputStream is = resources[0].getInputStream();
        File file = new File("ip2region.xdb");
        FileUtils.copyInputStreamToFile(is, file);
        is.close();
        if (StringUtils.isEmpty(String.valueOf(file))) {
            log.error("ip2region.xdb文件不存在");
            return null;
        }
        // 1、创建 searcher 对象
        String dbPath = file.getAbsolutePath();
        log.info("dbPath: {}", dbPath);
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithFileOnly(dbPath);
        } catch (IOException e) {
            log.error("failed to create searcher with {} {}", dbPath, e.getMessage(), e);
            return null;
        }
        // 2、查询
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            log.info("region: {}, ioCount: {}, took: {} μs", region, searcher.getIOCount(), cost);
            if (!StringUtils.isEmpty(region)) {
                region = region.replace("|0", "");
                region = region.replace("0|", "");
            }
            return region;
        } catch (Exception e) {
            log.error("failed to search({}) {}", ip, e.getMessage(), e);
        }
        // 3、关闭资源
        searcher.close();
        // 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。
        return null;
    }

    public static String getIpPossession(String ip) throws Exception {
        String cityInfo = getCityInfo(ip);
        if (!StringUtils.isEmpty(cityInfo)) {
            cityInfo = cityInfo.replace("|", " ");
            String[] cityList = cityInfo.split(" ");
            if (cityList.length > 0) {
                // 国内的显示到具体的省
                if ("中国".equals(cityList[0])) {
                    if (cityList.length > 1) {
                        return cityList[1];
                    }
                }
                // 国外显示到国家
                return cityList[0];
            }
        }
        return "未知";
    }

}
