package com.noriental.praxissvr.service.studentwork;

import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author chenlihua
 * @date 2016/3/31
 * @time 10:13
 */
public class StudentWorkAnswerTest {

    @Test
    public void testEquals() throws Exception {
        StudentWorkAnswer a = new StudentWorkAnswer();
        a.setQuestionId(null);
        StudentWorkAnswer b = new StudentWorkAnswer();
        b.setQuestionId(1L);
        StudentWorkAnswer c = new StudentWorkAnswer();
        c.setQuestionId(1L);
        int aHash = a.hashCode();
        int bHash = b.hashCode();
        int cHash = c.hashCode();
        System.out.println("aHash = " + aHash);
        System.out.println("bHash = " + bHash);
        System.out.println("cHash = " + cHash);
        assertFalse(a.hashCode() == b.hashCode());
        assertFalse(a.equals(b));
        assertTrue(b.hashCode() == c.hashCode());
        assertTrue(b.equals(c));
    }
}