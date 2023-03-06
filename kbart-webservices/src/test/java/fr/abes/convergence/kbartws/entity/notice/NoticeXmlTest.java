package fr.abes.convergence.kbartws.entity.notice;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NoticeXmlTest {

    @Test
    void get4XXDollar0() {
        NoticeXml notice = new NoticeXml();
        //cas 1 seule occurrence
        Datafield datafield = new Datafield();
        datafield.setTag("452");
        SubField subField1 = new SubField();
        subField1.setCode("0");
        subField1.setValue("test");
        datafield.setSubFields(Lists.newArrayList(subField1));
        notice.setDatafields(Lists.newArrayList(datafield));

        Assertions.assertFalse(notice.get4XXDollar0("452").isEmpty());
        Assertions.assertEquals("test", notice.get4XXDollar0("452").get(0));

        //cas pas d'occurrence
        notice = new NoticeXml();
        Assertions.assertTrue(notice.get4XXDollar0("452").isEmpty());

        //cas n occurrence
        notice.setDatafields(Lists.newArrayList(datafield, datafield));
        Assertions.assertFalse(notice.get4XXDollar0("452").isEmpty());
        Assertions.assertEquals(2, notice.get4XXDollar0("452").size());
        Assertions.assertEquals("test", notice.get4XXDollar0("452").get(0));
        Assertions.assertEquals("test", notice.get4XXDollar0("452").get(1));

        //cas avec un datafield mais pas le bon subfield
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("test2");
        datafield.setSubFields(Lists.newArrayList(subField));
        notice.setDatafields(Lists.newArrayList(datafield));
        Assertions.assertTrue(notice.get4XXDollar0("452").isEmpty());
    }
}