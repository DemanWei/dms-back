package com.qst.dms.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qst.dms.component.MailUtils;
import com.qst.dms.domain.Alert;
import com.qst.dms.domain.Performance;
import com.qst.dms.domain.Rule;
import com.qst.dms.domain.User;
import com.qst.dms.service.AlertService;
import com.qst.dms.service.PerformanceService;
import com.qst.dms.service.RuleService;
import com.qst.dms.service.UserService;
import com.qst.dms.utils.DateTimeUtils;
import com.qst.dms.utils.NumberUtils;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.management.MemoryUsage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PerformanceSchedule {
    @Value("${spring.application.name}")
    private String appName;

    @Resource(name = "heapMemoryUsage")
    private MemoryUsage heapMemoryUsage;
    @Resource(name = "nonHeapMemoryUsage")
    private MemoryUsage nonHeapMemoryUsage;
    @Autowired
    private Sigar sigar;
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private UserService userService;
    @Autowired
    private MailUtils mailUtils;


    @Scheduled(cron = "*/10 * * * * ?")
    public void monitor() {
        // 获取最近创建的一条rule
        List<Rule> ruleList = ruleService.list();
        // 指标列表
        Map<String, Rule> metricMap = ruleList.stream()
                .collect(Collectors.toMap(Rule::getMetricName, rule -> rule));

        try {
            /**
             * 报错: java.lang.UnsatisfiedLinkError: org.hyperic.sigar.Cpu.gather(Lorg/hyperic/sigar/Sigar;)V
             * 原因: 需要把sigar-amd64-winnt.dll放到jdk11/bin目录下,注意一定要下载https://github.com/cnstar9988/sigar/blob/master/sigar-amd64-winnt.dll
             * 解决方案: https://blog.csdn.net/a_c_c_a/article/details/117668026
             * */
            Performance performance = new Performance();
            List<Alert> alertList = new ArrayList<>();

            // CPU使用率
            double cpuUsage = NumberUtils.round(sigar.getCpuPerc().getCombined() * 100, 2);
            performance.setCpuUsage(cpuUsage);
            checkAndGenerateAlert("cpu_usage", metricMap, (long) cpuUsage, alertList);

            // 内存使用情况
            Mem mem = sigar.getMem();
            performance.setMemoryTotal(mem.getTotal() / 1024 / 1024 / 1024);
            performance.setMemoryUsed(mem.getUsed() / 1024 / 1024 / 1024);
            performance.setMemoryUnit("GB");
            double memeoryUsage = mem.getUsed() / mem.getTotal() * 100;
            checkAndGenerateAlert("memory_usage", metricMap, (long) memeoryUsage, alertList);

            // 磁盘使用情况
            FileSystem[] fslist = sigar.getFileSystemList();
            long diskTotalBytes = 0;
            long diskUsedBytes = 0;
            for (FileSystem fs : fslist) {
                FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                diskTotalBytes += usage.getTotal();
                diskUsedBytes += usage.getUsed();
            }
            performance.setDiskTotal(diskTotalBytes / 1024 / 1024);
            performance.setDiskUsed(diskUsedBytes / 1024 / 1024);
            performance.setDiskUnit("MB");
            double diskUsage = diskUsedBytes / diskTotalBytes * 100;
            checkAndGenerateAlert("disk_usage", metricMap, (long) diskUsage, alertList);

            // 网络流量
            String[] ifNames = sigar.getNetInterfaceList();
            long totalRxBytes = 0;
            long totalTxBytes = 0;
            for (String ifName : ifNames) {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(ifName);
                totalRxBytes += ifstat.getRxBytes();
                totalTxBytes += ifstat.getTxBytes();
            }
            performance.setNetRx(totalRxBytes / 1024 / 1024);
            performance.setNetTx(totalTxBytes / 1024 / 1024);
            performance.setNetUnit("MB");
            checkAndGenerateAlert("net", metricMap, totalRxBytes, alertList);
            checkAndGenerateAlert("net", metricMap, totalTxBytes, alertList);

            // heap
            performance.setHeapUsed(heapMemoryUsage.getUsed() / 1024 / 1024);
            performance.setHeapMax(heapMemoryUsage.getMax() / 1024 / 1024);
            performance.setHeapUnit("MB");
            double heapUsage = heapMemoryUsage.getUsed() / heapMemoryUsage.getMax() * 100;
            checkAndGenerateAlert("heap_usage", metricMap, (long) heapUsage, alertList);

            // non_heap
            performance.setNonHeapUsed(nonHeapMemoryUsage.getUsed() / 1024 / 1024);
            performance.setNonHeapMax(nonHeapMemoryUsage.getMax() / 1024 / 1024);
            performance.setNonHeapUnit("MB");
            double nonHeapUsage = nonHeapMemoryUsage.getUsed() / nonHeapMemoryUsage.getMax() * 100;
            checkAndGenerateAlert("non_heap_usage", metricMap, (long) nonHeapUsage, alertList);

            // 监控信息写入DB
            performanceService.save(performance);

            // 是否预警
            if (alertList.size() > 0) {
                alertService.saveBatch(alertList);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果指标真实值 > 规则阈值,则生成预警
     * 发送邮件
     */
    private void checkAndGenerateAlert(String key, Map<String, Rule> metricMap, long metricValueReal, List<Alert> alertList) {
        if (metricMap.containsKey(key) && metricValueReal > metricMap.get("cpu_usage").getMetricValue()) {
            // 预警
            Alert alert = new Alert();
            alert.setMetricName(key);
            alert.setMetricValueReal(metricValueReal);
            alert.setMetricValueRule(metricMap.get(key).getMetricValue());
            alert.setMetricUnit(metricMap.get(key).getMetricUnit());
            alertList.add(alert);

            // 获取开启邮件接收的用户
            List<User> userList = userService.list(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getReceiveEmail, 1)
            );
            // 发邮件
            final String text = MessageFormat.format(
                    "[{0}] 指标名称:{1}, 指标真实值:{2}, 规则阈值:{3}, 指标单位:{4}",
                    DateTimeUtils.now(), alert.getMetricName(), alert.getMetricValueReal(),
                    alert.getMetricValueRule(), alert.getMetricUnit()
            );
            final String title = appName + "-系统警报提醒";
            userList.forEach((user) -> {
                // 每个开启接收的用户都发
                String to = user.getEmail();
                mailUtils.sendEmail(to, title, text);
            });
        }
    }
}
