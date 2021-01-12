package com.alibaba.otter.canal.adapter.launcher.config;

import com.alibaba.otter.canal.client.adapter.support.Constant;
import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private static final String DEFUALT_SCRIPT_PATH_MARK = "DEFAULT";
    private static final String DEFUALT_SCRIPT_PATH = ".." + File.separator + Constant.CONF_DIR + File.separator + "script";

    @Value("${filter.enable:false}")
    private boolean filterEnable;
    @Value("${filter.script:DEFAULT}")
    private String scriptPath;

    /**
     * 是否启用过滤
     * @return
     */
    public boolean getFilterEnable(){
        return this.filterEnable;
    }

    /**
     * 脚本路径
     * @return
     */
    public String getScriptPath(){
        if(StringUtils.equals(this.scriptPath, DEFUALT_SCRIPT_PATH_MARK)){
            return DEFUALT_SCRIPT_PATH;
        }else{
            return this.scriptPath;
        }
    }
}
