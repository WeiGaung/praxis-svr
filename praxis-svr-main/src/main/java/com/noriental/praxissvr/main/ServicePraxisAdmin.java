package com.noriental.praxissvr.main;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.container.Container;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project:praxis-svr
 * Package: com.noriental.praxis.main
 * User: huzexin
 * Date: 15/9/10
 * Time: 下午4:19
 */

public class ServicePraxisAdmin {

    private static final String CONTAINER_KEY = "dubbo.container";

    private static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";

    static {
        ZKPropertiesLoader.load("/xdfapp/praxis-svr");
    }

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);

    private static volatile boolean running = true;


    public static void main(String[] args) {
        logger.info("praxis-svr is starting...");
        try {

            if (null == args || args.length == 0) {

                String config = ConfigUtils.getProperty(CONTAINER_KEY, loader.getDefaultExtensionName());
                args = Constants.COMMA_SPLIT_PATTERN.split(config);
            }

            final List<Container> containers = new ArrayList<>(20);

            for (String arg : args) {
                containers.add(loader.getExtension(arg));
            }

            logger.info("USE container type(" + Arrays.toString(args) + ") to run dubbo service");

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        for (Container container : containers) {
                            try {
                                container.stop();
                                logger.info("Dubbo " + container.getClass().getSimpleName() + " stopped!");
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }

                            synchronized (ServicePraxisAdmin.class) {
                                running = false;
                                ServicePraxisAdmin.class.notify();
                            }
                        }
                    }
                });
            }

            for (Container container : containers) {
                container.start();
                logger.info("Dubbo " + container.getClass().getSimpleName() + " started!");
            }

        } catch (RuntimeException e) {

            logger.error(e.getMessage(), e);
            System.exit(1);

        }


        synchronized (ServicePraxisAdmin.class) {
            while (running) {
                try {
                    ServicePraxisAdmin.class.wait();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
