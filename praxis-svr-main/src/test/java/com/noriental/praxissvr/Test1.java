package com.noriental.praxissvr;

/**
 * @author kate
 * @create 2017-11-22 10:31
 * @desc 测试  3233929
 **/
public class Test1 {

    public static void main(String[] args) {

       /* String a="kate Zhang";
        System.out.println(hash(a));*/
       String s="[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"\\u4EB2\\u60C5\\\"," +
               "\\\"matrix\\\":\\\"group1\\/M00\\/87\\/63\\/CgoHy1nz74aAbv2cAAADZBm9cFo808.txt\\\"},{\\\"index\\\":2," +
               "\\\"recogniseResult\\\":\\\"n\\\"," +
               "\\\"matrix\\\":\\\"group1\\/M01\\/87\\/63\\/CgoHylnz74aAXhreAAAAqmhV9P4079.txt\\\"},{\\\"index\\\":3," +
               "\\\"recogniseResult\\\":\\\"\\u7F51\\u7EDC\\u4E2D\\\"," +
               "\\\"matrix\\\":\\\"group1\\/M00\\/87\\/63\\/CgoHylnz74aAdMzbAAADSu_asms685.txt\\\"},{\\\"index\\\":4," +
               "\\\"recogniseResult\\\":\\\"\\u53CB\\u8C0A\\\"," +
               "\\\"matrix\\\":\\\"group1\\/M01\\/87\\/63\\/CgoHy1nz74aANfS_AAAC8UEiB6U681.txt\\\"},{\\\"index\\\":5," +
               "\\\"recogniseResult\\\":\\\"\\u53CB\\u8C0A\\\"," +
               "\\\"matrix\\\":\\\"group1\\/M00\\/87\\/63\\/CgoHyVnz74aAV5DWAAACvGWpM28693.txt\\\"}]";
        //System.out.println(StringEscapeUtils.unescapeEcmaScript(s));

//        List<Date> dateList=new ArrayList<>();
//        dateList.add(new Date());


    }


   static final int hash(Object k) {
        int h = 0;
        if (0 != h && k instanceof String) {
//            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
}
