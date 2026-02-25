package fr.abes.sudoc.entity.notice;

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

    @Test
    void checkProviderIn035a() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        //cas 1 seule occurrence
        Datafield datafield = new Datafield();
        datafield.setTag("035");
        SubField subField1 = new SubField();
        subField1.setCode("a");
        subField1.setValue("test");
        datafield.setSubFields(Lists.newArrayList(subField1));
        notice.setDatafields(Lists.newArrayList(datafield));

        Assertions.assertFalse(notice.checkProviderIn035a(provider));

        subField1.setValue("CAIRN123456");
        Assertions.assertTrue(notice.checkProviderIn035a(provider));

        subField1.setValue("123456CAIRN654987");
        Assertions.assertFalse(notice.checkProviderIn035a(provider));

        subField1.setCode("0");
        Assertions.assertFalse(notice.checkProviderIn035a(provider));

        datafield.setTag("200");
        Assertions.assertFalse(notice.checkProviderIn035a(provider));
    }

    @Test
    void checkProviderIn035aMultipleZone() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        Datafield datafield = new Datafield();
        SubField subField1 = new SubField();
        datafield.setTag("035");
        subField1.setCode("a");
        subField1.setValue("test");
        datafield.setSubFields(Lists.newArrayList(subField1));

        Datafield datafield1 = new Datafield();
        datafield1.setTag("035");
        SubField subField2 = new SubField();
        subField2.setCode("a");
        subField2.setValue("CAIRN");
        datafield1.setSubFields(Lists.newArrayList(subField2));

        notice.setDatafields(Lists.newArrayList(datafield, datafield1));
        Assertions.assertTrue(notice.checkProviderIn035a(provider));
    }

    @Test
    void checkProviderIn035aMultipleSousZone() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        Datafield datafield = new Datafield();
        SubField subField1 = new SubField();
        datafield.setTag("035");
        subField1.setCode("a");
        subField1.setValue("test");

        SubField subField2 = new SubField();
        subField2.setCode("a");
        subField2.setValue("CAIRN");
        datafield.setSubFields(Lists.newArrayList(subField1, subField2));

        notice.setDatafields(Lists.newArrayList(datafield));
        Assertions.assertTrue(notice.checkProviderIn035a(provider));
    }

    @Test
    void checkProviderInZone() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        //cas 1 seule occurrence
        Datafield datafield = new Datafield();
        datafield.setTag("200");
        SubField subField1 = new SubField();
        subField1.setCode("c");
        subField1.setValue("test");
        datafield.setSubFields(Lists.newArrayList(subField1));
        notice.setDatafields(Lists.newArrayList(datafield));

        Assertions.assertFalse(notice.checkProviderInZone(provider, "200", "c"));

        subField1.setValue("CAIRN123456");
        Assertions.assertTrue(notice.checkProviderInZone(provider, "200", "c"));

        subField1.setValue("123456CAIRN654987");
        Assertions.assertTrue(notice.checkProviderInZone(provider, "200", "c"));

        subField1.setCode("0");
        Assertions.assertFalse(notice.checkProviderInZone(provider, "200", "c"));

        datafield.setTag("215");
        Assertions.assertFalse(notice.checkProviderInZone(provider, "200", "c"));
    }
    @Test
    void checkProviderInZone214() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();

        // Cas 1 : Aucune zone 214
        Assertions.assertFalse(notice.checkProviderInZone214(provider));

        // Cas 5 : Une seule occurrence, pas de match
        Datafield datafield1 = new Datafield();
        datafield1.setTag("214");
        datafield1.setInd1("#");
        datafield1.setInd2("0"); // Publication
        SubField subField1 = new SubField();
        subField1.setCode("c");
        subField1.setValue("autre");
        datafield1.setSubFields(Lists.newArrayList(subField1));
        notice.setDatafields(Lists.newArrayList(datafield1));
        Assertions.assertFalse(notice.checkProviderInZone214(provider));

        // Cas 6 : Une seule occurrence, match
        subField1.setValue("CAIRN123");
        Assertions.assertTrue(notice.checkProviderInZone214(provider));

        // Cas 2 : Plusieurs occurrences, mais aucune avec ind2 == 2
        Datafield datafield2 = new Datafield();
        datafield2.setTag("214");
        datafield2.setInd1("#");
        datafield2.setInd2("0"); // Publication
        SubField subField2 = new SubField();
        subField2.setCode("c");
        subField2.setValue("CAIRN");
        datafield2.setSubFields(Lists.newArrayList(subField2));
        notice.setDatafields(Lists.newArrayList(datafield1, datafield2));
        // Ici datafield1 a "CAIRN123" mais on a 2 zones et aucune n'a ind2=2
        Assertions.assertFalse(notice.checkProviderInZone214(provider));

        // Cas 4 : Plusieurs occurrences, match sur la zone avec ind2 == 2
        datafield2.setInd2("2"); // On change en Diffusion
        Assertions.assertTrue(notice.checkProviderInZone214(provider));

        // Cas 3 : Plusieurs occurrences, la zone avec ind2 == 2 ne matche pas
        subField2.setValue("autre");
        Assertions.assertFalse(notice.checkProviderInZone214(provider));
    }

    @Test
    void checkProviderInZoneMultipleZone() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        Datafield datafield = new Datafield();
        SubField subField1 = new SubField();
        datafield.setTag("200");
        subField1.setCode("c");
        subField1.setValue("test");
        datafield.setSubFields(Lists.newArrayList(subField1));

        Datafield datafield1 = new Datafield();
        datafield1.setTag("200");
        SubField subField2 = new SubField();
        subField2.setCode("c");
        subField2.setValue("CAIRN");
        datafield1.setSubFields(Lists.newArrayList(subField2));

        notice.setDatafields(Lists.newArrayList(datafield, datafield1));
        Assertions.assertTrue(notice.checkProviderInZone(provider, "200", "c"));
    }

    @Test
    void checkProviderInZoneMultipleSousZone() {
        String provider = "CAIRN";
        NoticeXml notice = new NoticeXml();
        Datafield datafield = new Datafield();
        SubField subField1 = new SubField();
        datafield.setTag("200");
        subField1.setCode("c");
        subField1.setValue("test");

        SubField subField2 = new SubField();
        subField2.setCode("c");
        subField2.setValue("CAIRN");
        datafield.setSubFields(Lists.newArrayList(subField1, subField2));

        notice.setDatafields(Lists.newArrayList(datafield));
        Assertions.assertTrue(notice.checkProviderInZone(provider, "200", "c"));
    }

    @Test
    void idDeleted() {
        NoticeXml noticeXml = new NoticeXml();
        noticeXml.setLeader("     dls0 22        450 ");
        Assertions.assertTrue(noticeXml.isDeleted());
    }
}
