package com.noriental.praxissvr.bean.answerques;

import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.statis.bean.AnswAndResultSatis;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author chenlihua
 * @date 2016/3/30
 * @time 18:09
 */
public class AnswAndResultSatisTest {

    @Test
    public void testEquals() throws Exception {
        AnswAndResultSatis a = new AnswAndResultSatis();
        a.setLevel(StudentWork.WorkLevel.MODULE);
        a.setModuleId(15L);
        AnswAndResultSatis b = new AnswAndResultSatis();
        b.setLevel(StudentWork.WorkLevel.MODULE);
        b.setModuleId(15L);
        assertTrue(a.hashCode() == b.hashCode());
        assertEquals(a, b);

        b.setModuleId(16L);
        assertFalse(a.hashCode() == b.hashCode());
        assertNotSame(a, b);
    }
}