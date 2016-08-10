package com.cybermkd.plugin.motan;

import com.cybermkd.common.Plugin;
import com.cybermkd.log.Logger;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;


public class MotanPlugin implements Plugin{

    Logger logger=Logger.getLogger(this.getClass());

    @Override
    public boolean start() {
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        logger.warn("(｡・`ω´･) Motan registration by ICEREST !");
        return true;
    }

    @Override
    public boolean stop() {
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
        logger.warn("(｡・`ω´･) Motan has been closed by ICEREST !");
        return false;
    }
}
