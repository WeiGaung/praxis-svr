package com.noriental.praxissvr.bean.quiz;

import com.noriental.praxissvr.question.bean.QuesitonRecommend;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author chenlihua
 * @date 2016/3/31
 * @time 10:05
 */
public class QuesitonRecommendTest {

    @Test
    public void testEquals() throws Exception {
        QuesitonRecommend a = new QuesitonRecommend();
        a.setQuestionId(1L);
        QuesitonRecommend b = new QuesitonRecommend();
        b.setQuestionId(2L);
        int aHashcode = a.hashCode();
        System.out.println("aHashcode = " + aHashcode);
        int bHashcode = b.hashCode();
        System.out.println("bHashcode = " + bHashcode);
        assertFalse(aHashcode == bHashcode);
        assertNotSame(a, b);

        QuesitonRecommend c = new QuesitonRecommend();
        c.setQuestionId(1L);

        int cHashcode = c.hashCode();
        System.out.println("cHashcode = " + cHashcode);
        assertTrue(aHashcode == cHashcode);
        assertEquals(a, c);
    }
}