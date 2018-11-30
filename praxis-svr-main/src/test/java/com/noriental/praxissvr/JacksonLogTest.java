package com.noriental.praxissvr;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import static org.junit.Assert.*;
/**
 * @author chenlihua
 * @date 2016/3/31
 * @time 16:45
 */
public class JacksonLogTest {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void main() {
        Person person = new Person();
        String s = JsonUtil.obj2Json(person);
        logger.debug("person:" + s);
        assertFalse(s.contains("name"));
        person.setName("john");
        String s1 = JsonUtil.obj2Json(person);
        logger.debug("person:" + s1);
        assertTrue(s1.contains("name"));
    }
}
