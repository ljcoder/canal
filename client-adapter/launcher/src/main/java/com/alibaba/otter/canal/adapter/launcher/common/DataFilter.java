package com.alibaba.otter.canal.adapter.launcher.common;

import com.alibaba.otter.canal.adapter.launcher.config.FilterConfig;
import com.alibaba.otter.canal.connector.core.consumer.CommonMessage;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataFilter {

    private final static Object lock = new Object();

    private Invocable invocable;

    private final static Logger logger = LoggerFactory.getLogger(DataFilter.class);

    @Resource
    private FilterConfig filterConfig;

    @PostConstruct
    private void initScriptEngine(){
        if(this.filterConfig.getFilterEnable()) {
            logger.info("enable data filter ...");
            logger.info("load javascript ...");
            this.createScriptEngine();
        }
    }

    /**
     * 重新加载脚本
     */
    public void reload() {
        this.createScriptEngine();
    }

    private void createScriptEngine(){
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        List<FileReader> scripts = this.loadScriptFiles();
        for(FileReader script: scripts){
            try {
                scriptEngine.eval(script);
            } catch (ScriptException e) {
                logger.error("javascrpit load error", e);
                return;
            }
        }
        synchronized (DataFilter.lock){
            this.invocable = (Invocable) scriptEngine;
        }
    }

    private List<FileReader> loadScriptFiles(){
        List<FileReader> fileReaders = Lists.newArrayList();
        File scriptDir = new File(this.filterConfig.getScriptPath());
        if (scriptDir.exists()) {
            File[] scriptFiles = scriptDir.listFiles();
            if(scriptFiles != null && scriptFiles.length > 0){
                Arrays.stream(scriptFiles).forEach(scriptFile->{
                    if(scriptFile.getName().endsWith(".js")){
                        try {
                            fileReaders.add(new FileReader(scriptFile));
                            logger.info("load javascript [{}] file", scriptFiles);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }else{
            logger.error("[{}] filter script file not found !",this.filterConfig.getScriptPath());
        }
        return fileReaders;
    }



    /**
     * 过滤数据
     * @param messages
     * @return
     */
    public List<CommonMessage> filter(List<CommonMessage> messages){
        if(this.filterConfig.getFilterEnable()){
            if(Objects.nonNull(this.invocable)){
                try {
                    String sn = UUID.randomUUID().toString().replace("-", "");
                    List<CommonMessage> result = (List<CommonMessage>) this.invocable.invokeFunction("main", new Object[]{
                            messages, sn
                    });
                    return result;
                } catch (ScriptException e) {
                    e.printStackTrace();
                    return messages;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return messages;
                }
            }
        }
        return messages;
    }
}
