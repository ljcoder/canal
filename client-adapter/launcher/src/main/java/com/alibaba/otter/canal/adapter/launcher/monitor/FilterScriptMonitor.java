package com.alibaba.otter.canal.adapter.launcher.monitor;

import com.alibaba.otter.canal.adapter.launcher.common.DataFilter;
import com.alibaba.otter.canal.adapter.launcher.config.FilterConfig;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FilterScriptMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigMonitor.class);

    @Resource
    private DataFilter dataFilter;
    @Resource
    private FilterConfig filterConfig;

    private FileAlterationMonitor fileMonitor;

    @PostConstruct
    public void init() {
        if(this.filterConfig.getFilterEnable()) {
            File confDir = new File(this.filterConfig.getScriptPath());
            try {
                FileAlterationObserver observer = new FileAlterationObserver(confDir,
                        FileFilterUtils.suffixFileFilter("js"));
                FilterScriptMonitor.FileListener listener = new FilterScriptMonitor.FileListener();
                observer.addListener(listener);
                fileMonitor = new FileAlterationMonitor(3000, observer);
                fileMonitor.start();

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if(this.filterConfig.getFilterEnable()) {
            try {
                fileMonitor.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private class FileListener extends FileAlterationListenerAdaptor {

        @Override
        public void onFileChange(File file) {
            super.onFileChange(file);
            try {
                dataFilter.reload();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // ignore
                }
                logger.info("## data filter script reloaded.");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
