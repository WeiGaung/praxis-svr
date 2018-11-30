package com.noriental;


import com.noriental.praxissvr.answer.request.FindStuAnswsOnBatchRequest;
import com.noriental.utils.json.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/24
 * @time 9:50
 */
public class Test {

    public static void main(String[] args) throws Exception {



        String s = "{\"content\": {\"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": " +
                "[{\"type\": \"text\", \"value\": \"The English test will be removed from China’s college entrance "
                + "exam by 2020, according to details of exam and admis\u001Fsion reform revealed by the Ministry of " +
                "" + "" + "" + "Education. The national college entrance exam, known as the “Gaokao” has been used to" +
                " " + "evaluate " + "" + "Chinese students for three decades. The Ministry of Education has worked " +
                "out a " + "plan for " + "reforming " + "exams and enrollment. The Ministry will solicit(征求) public " +
                "opinions " + "before its " + "release. Instead, " + "tests will be held several times a year to " +
                "allow students to " + "choose when and " + "how often they sit the " + "exam so as to alleviate " +
                "study pressure and change " + "China’s " + "once-in-a-lifetime exam system.\"}, " + "{\"type\": " +
                "\"newline\", \"value\": \"1\"}, " + "{\"type\": " + "\"text\", \"value\": \"The plan and " +
                "suggestions for its implementation will be " + "announced in the" + " first half of next year. It " +
                "will be " + "piloted in se\u001Flected provinces " + "and cities and " + "promoted nationwide from " +
                "2017. A new exam and " + "admission system will be " + "established by 2020, " + "according to the " +
                "education ministry.\"}, {\"type\": " + "\"newline\", " + "\"value\": \"1\"}, {\"type\": " +
                "\"text\", \"value\": \"The decision has aroused a heated " + "discussion among Shanghai educators "
                + "and parents who doubted the reform would re\u001Fduce the " + "burden" + " of learning English or " +
                "if the " + "substitute test could reflect a student’s English " + "skills and help " + "students " +
                "learn English better" + ".\"}, {\"type\": \"newline\", \"value\": " + "\"1\"}, {\"type\": \"text\", " +
                "" + "\"value\": \"“The reform " + "shows China is learning from the " + "West to give students more " +
                "test-taking " + "chances. But more " + "chances might become more of a " + "burden since Chinese " +
                "students are likely to repeat " + "the test " + "until they get the highest " + "score,” said Cai " +
                "Jigang, a professor at Fudan University’s " + "College " + "of Foreign Languages " + "and Literature" +
                " and chairman of the Shanghai Advisory Committee for " + "College" + " English " + "Teaching at " +
                "Tertiary Level.\"}, {\"type\": \"newline\", \"value\": \"1\"}, " + "{\"type\": " + "\"text\", " +
                "\"value\": \"Yu Lizhong, chancellor of New York University Shanghai, where " + "classes " + "are in " +
                "English and students are required to have a high standard of English, said the most " + "important " +
                "aspect of the reform lay in what to test and how to test.\"}, {\"type\": \"newline\", " +
                "\"value\": \"1\"}, {\"type\": \"text\", \"value\": \"“ As far as I see, the reform doesn’t mean " +
                "English is no longer important for Chinese students after it will be excluded from the unified " +
                "college entrance exam,” Yu said. “In a way, English is even more important than before since the " +
                "test would only serve as reference, while every college and university, even every major, can have "
                + "different requirements of a student’s English skills under a diverse evaluation system. ”\"}, " +
                "{\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"style\": 4, \"value\": \"Yu said "
                + "some students will have their study pressure reduced if the major they choose doesn’t need " +
                "excellent" + " English while others still need to study hard if they want to be among the best " +
                "students.\"}, " + "{\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"The " +
                "" + "" + "education ministry " + "said the reform would not affect students attending the college "
                + "entrance " + "exam over the next three " + "years.\"}], \"questions\": [{\"body\": [{\"type\": " +
                "\"text\", " + "\"value\": \"What can we learn from the" + " first paragraph?\"}], \"type\": " +
                "{\"id\":" + " 1, \"name\": " + "\"选择题\"}, \"answer\": [\"D\"], \"options\": " + "[[{\"type\": " +
                "\"option\", " + "\"value\": \"A\"}, " + "{\"type\": \"text\", \"value\": \"English will become " +
                "less and less " + "important in the stage of " + "compulsory education.\"}], [{\"type\": \"option\"," +
                " " + "\"value\": " + "\"B\"}, {\"type\": \"text\", " + "\"value\": \"It has been 30 years since " +
                "English became one " + "subject of national college entrance" + " exam.\"}], [{\"type\": \"option\"," +
                " \"value\": \"C\"}, " + "{\"type\": \"text\", \"value\": " + "\"China’s once-in-a-lifetime exam " +
                "system is unacceptable at " + "all" + ".\"}], [{\"type\": \"option\", " + "\"value\": \"D\"}, " +
                "{\"type\": \"text\", \"value\": \"The" + " system that " + "tests are held several " + "times does " +
                "more good than once-in-a-lifetime exam " + "system.\"}]], \"analysis\":" + " [{\"type\": " +
                "\"text\", \"value\": \"无\"}], \"difficulty\": " + "\"2\"}, {\"body\": [{\"type\": \"text\", " +
                "\"value\": \"According to the passage, Shanghai " + "educators and parents argue that _____.\"}], "
                + "\"type\": {\"id\": 1, \"name\": \"选择题\"}, " + "\"answer\": [\"C\"], \"options\": [[{\"type\": " +
                "\"option\"," + "" + " \"value\": \"A\"}, {\"type\": " + "\"text\", \"value\": \"the new exam and " +
                "admission system will " + "make no" + " difference\"}], " + "[{\"type\": \"option\", \"value\": " +
                "\"B\"}, {\"type\": \"text\", " + "\"value\": " + "\"English " + "shouldn’t be removed from China’s " +
                "college entrance exam\"}], [{\"type\": " + "\"option\", " + "\"value\": \"C\"}, {\"type\": \"text\"," +
                " \"value\": \"the reform may accomplish the " + "very " + "opposite\"}], [{\"type\": \"option\", " +
                "\"value\": \"D\"}, {\"type\": \"text\", \"value\": " + "\"Western " + "educational system does not " +
                "apply to China\"}]], \"analysis\": [{\"type\": \"text\", " + "" + "\"value\": " + "\"无\"}], " +
                "\"difficulty\": \"2\"}, {\"body\": [{\"type\": \"text\", \"value\": " + "\"What " + "does the " +
                "passage" + " try to express in the underlined sentence?\"}], \"type\": {\"id\":" + " 1, " +
                "\"name\": \"选择题\"}, " + "\"answer\": [\"B\"], \"options\": [[{\"type\": \"option\", " + "\"value\": " +
                "" + "\"A\"}, {\"type\": \"text\", " + "\"value\": \"Students needn’t lay a good " + "foundation " +
                "during the " + "period of high school.\"}], " + "[{\"type\": \"option\", \"value\": " + "\"B\"}, " +
                "{\"type\": \"text\", " + "\"value\": \"Whether students should" + " study hard English may " +
                "depend on their major.\"}], " + "[{\"type\": \"option\", \"value\": \"C\"}, " + "{\"type\": " +
                "\"text\", \"value\": \"Students can " + "constantly strive for perfection only in their major" + ""
                + ".\"}], [{\"type\": \"option\", \"value\": " + "\"D\"}, {\"type\": \"text\", \"value\": \"English "
                + "must be " + "close to full mark.\"}]], " + "\"analysis\": [{\"type\": \"text\", \"value\": " +
                "\"无\"}], " + "\"difficulty\": " + "\"2\"}, {\"body\": " + "[{\"type\": \"text\", \"value\": \"What’s" +
                " the purpose " + "of the passage?\"}], " + "\"type\": {\"id\": " + "1, \"name\": \"选择题\"}, " +
                "\"answer\": [\"C\"], " + "\"options\": [[{\"type\": \"option\"," + " \"value\": " + "\"A\"}, " +
                "{\"type\": \"text\", \"value\": " + "\"To advise students not to devote themselves to" + " " +
                "English.\"}], [{\"type\": \"option\", " + "\"value\": \"B\"}, {\"type\": \"text\", \"value\": \"To " +
                "call " + "on Education Department to remove " + "English from “Gaokao”.\"}], [{\"type\": \"option\"," +
                " \"value\": " + "\"C\"}, {\"type\": \"text\", " + "\"value\": \"To support the act of Ministry of " +
                "Education.\"}], " + "[{\"type\": \"option\", " + "\"value\": \"D\"}, {\"type\": \"text\", \"value\":" +
                " \"To encourage students " + "to do as they have " + "planned.\"}]], \"analysis\": [{\"type\": " +
                "\"text\", \"value\": \"无\"}], " + "\"difficulty\": " + "\"2\"}]}, \"questionId\": 5876740}";


        String s1 = "{\"content\": {\"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": " +
                "[{\"type\": \"text\", \"value\": \"The English test will be removed from China’s college entrance "
                + "exam by 2020, according to details of exam and admisu001fsion reform revealed by the Ministry of "
                + "Education. The national college entrance exam, known as the “Gaokao” has been used to evaluate " +
                "Chinese students for three decades. The Ministry of Education has worked out a plan for reforming "
                + "exams and enrollment. The Ministry will solicit(征求) public opinions before its release. Instead, "
                + "tests will be held several times a year to allow students to choose when and how often they sit "
                + "the " + "exam so as to alleviate study pressure and change China’s once-in-a-lifetime exam system"
                + ".\"}, " + "{\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"The plan "
                + "and" + " " + "suggestions for its implementation will be announced in the first half of next year." +
                " " + "It will " + "be " + "piloted in seu001flected provinces and cities and promoted nationwide " +
                "from " + "2017. A new exam " + "and " + "admission system will be established by 2020, according to " +
                "the " + "education ministry.\"}, " + "{\"type\": " + "\"newline\", \"value\": \"1\"}, {\"type\": " +
                "\"text\", " + "\"value\": \"The decision has " + "aroused a heated " + "discussion among Shanghai " +
                "educators and " + "parents who doubted the reform would " + "reu001fduce the burden " + "of learning" +
                " English or if the " + "substitute test could reflect a student’s " + "English skills and help " +
                "students learn English " + "better.\"}, {\"type\": \"newline\", \"value\": " + "\"1\"}, {\"type\": " +
                "\"text\", " + "\"value\": " + "\"“The reform shows China is learning from the West to " + "give " +
                "students more test-taking " + "chances. But more chances might become more of a burden since " +
                "Chinese students are likely to " + "repeat " + "the test until they get the highest score,” said Cai" +
                " " + "Jigang, a professor at Fudan " + "University’s " + "College of Foreign Languages and " +
                "Literature and " + "chairman of the Shanghai " + "Advisory Committee for " + "College English " +
                "Teaching at Tertiary Level.\"}," + " {\"type\": " + "\"newline\", \"value\": \"1\"}, " + "{\"type\":" +
                " \"text\", \"value\": \"Yu Lizhong, " + "chancellor " + "of New York University Shanghai, where " +
                "classes are in English and students are " + "required to" + " have a high standard of English, said " +
                "the most " + "important aspect of the reform lay " + "in what" + " to test and how to test.\"}, " +
                "{\"type\": \"newline\", " + "\"value\": \"1\"}, {\"type\": " + "\"text\", \"value\": \"“ As far as I" +
                " see, the reform doesn’t mean " + "English is no longer " + "important for Chinese students after it" +
                " will be excluded from the unified " + "college entrance " + "exam,” Yu said. “In a way, English is " +
                "even more important than before since the " + "test would " + "only" + " serve as reference, while " +
                "every college and university, even every major, can have " + "different " + "requirements of a " +
                "student’s English skills under a diverse evaluation system. ”\"}, " + "{\"type\": " + "\"newline\", " +
                "\"value\": \"1\"}, {\"type\": \"text\", \"style\": 4, \"value\": \"Yu" + " said " + "some " +
                "students will have their study pressure reduced if the major they choose " + "doesn’t need " +
                "excellent" + "" + " English while others still need to study hard if they want to be " + "among the " +
                "best students.\"}, " + "{\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", " +
                "\"value\": \"The education ministry " + "" + "said the reform would not affect students attending "
                + "the college entrance exam over the next " + "three " + "years.\"}], \"questions\": [{\"body\": " +
                "[{\"type\": \"text\", \"value\": \"What can we " + "learn from the" + " first paragraph?\"}], " +
                "\"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": " + "[\"D\"], \"options\": " + "[[{\"type\": "
                + "\"option\", \"value\": \"A\"}, {\"type\": \"text\", " + "\"value\": \"English will become " +
                "less " + "and less important in the stage of compulsory education" + ".\"}], [{\"type\": \"option\"," +
                " " + "\"value\": \"B\"}, {\"type\": \"text\", \"value\": \"It has been" + " 30 years since English " +
                "became " + "one " + "subject of national college entrance exam.\"}], [{\"type\": " + "\"option\", " +
                "\"value\": " + "\"C\"}, " + "{\"type\": \"text\", \"value\": \"China’s once-in-a-lifetime " + "exam " +
                "system is " + "unacceptable at all" + ".\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": " +
                "" + "\"text\", " + "\"value\": \"The system that " + "tests are held several times does more good " +
                "than " + "once-in-a-lifetime exam system.\"}]], \"analysis\":" + " [{\"type\": \"text\", \"value\": " +
                "\"无\"}], " + "\"difficulty\": \"2\"}, {\"body\": [{\"type\": \"text\", " + "\"value\": \"According " +
                "to the " + "passage," + " Shanghai educators and parents argue that _____.\"}], " + "\"type\": " +
                "{\"id\": 1, " + "\"name\": " + "\"选择题\"}, \"answer\": [\"C\"], \"options\": [[{\"type\": \"option\"," +
                "" + " \"value\": " + "\"A\"}, " + "{\"type\": \"text\", \"value\": \"the new exam and admission " +
                "system will make no" + " " + "difference\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"type\": " +
                "\"text\", \"value\": " + "\"English shouldn’t be removed from China’s college entrance exam\"}], " +
                "[{\"type\": \"option\", " + "\"value\": \"C\"}, {\"type\": \"text\", \"value\": \"the reform may " +
                "accomplish the very " + "opposite\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": " +
                "\"text\", \"value\": \"Western " + "educational system does not apply to China\"}]], \"analysis\": " +
                "[{\"type\": \"text\", \"value\": " + "\"无\"}], \"difficulty\": \"2\"}, {\"body\": [{\"type\": " +
                "\"text\", \"value\": \"What does the " + "passage" + " try to express in the underlined " +
                "sentence?\"}], \"type\": {\"id\": 1, \"name\": " + "\"选择题\"}, " + "\"answer\": [\"B\"], \"options\":" +
                " [[{\"type\": \"option\", \"value\": \"A\"}, " + "{\"type\": \"text\", " + "\"value\": \"Students " +
                "needn’t lay a good foundation during the period of " + "high school.\"}], " + "[{\"type\": " +
                "\"option\", \"value\": \"B\"}, {\"type\": \"text\", \"value\": " + "" + "\"Whether students should"
                + " study hard English may depend on their major.\"}], [{\"type\": " + "\"option\", \"value\": " +
                "\"C\"}, " + "{\"type\": \"text\", \"value\": \"Students can constantly " + "strive" + " for " +
                "perfection only in their major" + ".\"}], [{\"type\": \"option\", \"value\": \"D\"}," + " " +
                "{\"type\":" + " \"text\", \"value\": \"English must be " + "close to full mark.\"}]], \"analysis\":"
                + " [{\"type\": " + "\"text\", \"value\": \"无\"}], \"difficulty\": " + "\"2\"}, {\"body\": " +
                "[{\"type\": " + "\"text\", " + "\"value\": \"What’s the purpose of the passage?\"}], " + "\"type\": " +
                "{\"id\": 1, " + "\"name\": \"选择题\"}, " + "\"answer\": [\"C\"], \"options\": [[{\"type\": \"option\"," +
                "" + " \"value\": " + "\"A\"}, {\"type\": " + "\"text\", \"value\": \"To advise students not to " +
                "devote themselves to" + " " + "English.\"}], [{\"type\":" + " \"option\", \"value\": \"B\"}, " +
                "{\"type\": \"text\", \"value\": \"To " + "call " + "on Education " + "Department to remove English " +
                "from “Gaokao”.\"}], [{\"type\": \"option\"," + " \"value\": " + "\"C\"}, " + "{\"type\": \"text\", " +
                "\"value\": \"To support the act of Ministry of " + "Education.\"}], " + "[{\"type\":" + " " +
                "\"option\", \"value\": \"D\"}, {\"type\": \"text\", \"value\":" + " \"To encourage students " + "to " +
                "do " + "as they have planned.\"}]], \"analysis\": [{\"type\": " + "\"text\", \"value\": \"无\"}], " +
                "\"difficulty\": \"2\"}]}, \"questionId\": 5876740}";
     /*   s= s.replaceAll("[\u0000-\u001f]", "");
        SuperQuestionSsdb content = JsonUtil.readValue(s, SuperQuestionSsdb.class);
        System.out.println(content.getContent());*/


//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String ss="2017-09-25 12:32:19";
//        Date result=sdf.parse(ss);
//
//        String newData="2017-09-26 12:30:41";
//        Date newResult=sdf.parse(newData);
//        long difHours=getTimeDiff(result,newResult);
//        System.out.print("difHours:"+difHours+"");

        String s11="{\"reqId\":null,\"studentId\":81951143029,\"resourceId\":2460622,\"exerciseSource\":\"8\",\"redoSource\":null,\"subQuesSort\":false,\"structIdList\":null,\"forPad\":true}";
        FindStuAnswsOnBatchRequest i = JsonUtil.readValue(s11, FindStuAnswsOnBatchRequest.class);
        System.out.println(i.toString());

    }
    private static String hexString = "0123456789ABCDEF";

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String cleanString(String dirtyString) {
        char[] charArray = dirtyString.toCharArray();
        String cleanStr = "";
        for (char charactor : charArray) {
            Integer[] wrongChar = {1, 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 18, 19, 20, 12, 22, 23, 24, 25,
                    26, 27, 28, 29, 30, 31};
            List<Integer> arr = Arrays.asList(wrongChar);
            Integer thisChar = new Integer(charactor);
            if (arr.contains(thisChar)) {//如果包含非法字符，就跳过
                // System.out.println("Alex","警告：该字符串包含非法ASCII字符"+dirtyString);
                continue;
            } else if (thisChar == 10) {//换行符处理，把换行符换成\n，修复不同android版本不兼容的问题
                cleanStr = cleanStr.concat("\n");
            } else {//正常字符
                cleanStr = cleanStr.concat(String.valueOf(charactor));
            }
        }
        return cleanStr;
    }

    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    private static void isConSpeCharacters(String string) {
        // TODO Auto-generated method stub
        string.replaceAll("[\u0000-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "");
    }

    public static char[] generateRandomArray(int num) {
        String chars = "0123456789";
        char[] rands = new char[num];
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * 10);
            rands[i] = chars.charAt(rand);
        }
        return rands;
    }

    public static long getTimeDiff(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        if (null != endDate) {
            long diff = nowDate.getTime() - endDate.getTime();
            long day = diff / nd;
            long hour = 0;
            if (day > 0) {
                hour = 24 * day;
            }
            hour = diff % nd / nh + hour;
            return hour;

        }
        return 0;
    }
}





/*ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_0`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_1`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_2`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_3`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_4`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_5`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_6`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_7`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_8`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_9`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_10`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_11`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_12`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_13`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_14`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_15`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_16`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_17`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_18`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_19`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_20`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_21`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_22`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_23`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_24`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_25`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_26`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_27`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_28`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_29`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_30`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_31`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_32`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_33`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_34`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_35`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_36`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_37`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_38`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_39`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_40`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_41`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_42`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_43`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_44`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_45`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_46`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_47`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_48`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2018_49`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;

ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_0`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_1`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_2`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_3`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_4`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_5`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_6`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_7`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_8`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_9`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_10`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_11`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_12`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_13`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_14`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_15`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_16`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_17`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_18`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_19`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_20`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_21`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_22`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_23`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_24`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_25`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_26`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_27`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_28`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_29`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_30`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_31`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_32`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_33`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_34`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_35`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_36`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_37`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_38`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_39`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_40`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_41`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_42`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_43`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_44`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_45`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_46`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_47`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_48`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2017_49`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;

ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_0`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_1`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_2`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_3`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_4`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_5`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_6`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_7`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_8`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_9`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_10`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_11`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_12`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_13`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_14`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_15`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_16`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_17`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_18`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_19`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_20`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_21`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_22`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_23`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_24`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_25`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_26`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_27`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_28`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_29`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_30`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_31`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_32`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_33`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_34`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_35`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_36`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_37`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_38`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_39`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_40`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_41`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_42`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_43`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_44`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_45`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_46`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_47`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_48`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2016_49`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_0`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_1`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_2`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_3`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_4`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_5`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_6`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_7`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_8`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_9`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_10`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_11`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_12`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_13`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_14`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_15`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_16`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_17`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_18`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_19`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_20`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_21`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_22`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_23`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_24`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_25`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_26`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_27`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_28`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_29`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_30`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_31`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_32`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_33`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_34`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_35`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_36`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_37`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_38`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_39`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_40`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_41`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_42`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_43`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_44`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_45`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_46`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_47`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_48`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;
ALTER TABLE `neworiental_answer`.`entity_student_exercise_2015_49`
ADD COLUMN `is_white_list` tinyint(4) COMMENT '是否存在白名单 0 : 不存在 1 : 存在' AFTER `intell_postil_status`;*/
