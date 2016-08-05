package com.cybermkd.plugin.cron4j;

import com.cybermkd.common.Plugin;
import com.cybermkd.common.util.Stringer;
import com.cybermkd.log.Logger;
import com.cybermkd.plugin.kit.Reflect;
import com.cybermkd.plugin.kit.ResourceKit;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Cron4jPlugin implements Plugin {

    private static final String JOB = "job";

    private final Logger log = Logger.getLogger(getClass());

    private Map<Runnable, String> jobs = Maps.newLinkedHashMap();

    private String config;

    private Scheduler scheduler;

    private Map<String, String> jobProp;


    public Cron4jPlugin add(String jobCronExp, Runnable job) {
        jobs.put(job, jobCronExp);
        return this;
    }

    public Cron4jPlugin config(String config) {
        this.config = config;
        return this;
    }

    @Override
    public boolean start() {
        loadJobsFromProperties();
        startJobs();
        return true;
    }

    private void startJobs() {
        scheduler = new Scheduler();
        Set<Entry<Runnable, String>> set = jobs.entrySet();
        for (Entry<Runnable, String> entry : set) {
            scheduler.schedule(entry.getValue(), entry.getKey());
            log.debug(entry.getValue() + " has been scheduled to run and repeat based on expression: " + entry.getKey());
        }
        scheduler.start();
    }

    private void loadJobsFromProperties() {
        if (Stringer.isBlank(config)) {
            return;
        }
        jobProp = ResourceKit.readProperties(config);
        Set<Entry<String, String>> entries = jobProp.entrySet();
        for (Entry<String, String> entry : entries) {
            String key = entry.getKey();
            if (!key.endsWith(JOB) || !isEnableJob(enable(key))) {
                continue;
            }
            String jobClassName = jobProp.get(key) + "";
            String jobCronExp = jobProp.get(cronKey(key)) + "";
            Class<Runnable> clazz = Reflect.on(jobClassName).get();
            try {
                jobs.put(clazz.newInstance(), jobCronExp);
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        }
    }

    private String enable(String key) {
        return key.substring(0, key.lastIndexOf(JOB)) + "enable";
    }

    private String cronKey(String key) {
        return key.substring(0, key.lastIndexOf(JOB)) + "cron";
    }

    private boolean isEnableJob(String enableKey) {
        Object enable = jobProp.get(enableKey);
        if (enable != null && "false".equalsIgnoreCase((enable + "").trim())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean stop() {
        scheduler.stop();
        return true;
    }

}
