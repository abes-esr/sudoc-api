package fr.abes.sudoc.entity.notice;

import fr.abes.sudoc.exception.ZoneNotFoundException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoticesBibioTest {

    @Test
    void isDeleted() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     dam0 22        450 ");
        Assertions.assertTrue(notice.isDeleted());
    }

    @Test
    void getTypeDocument() {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("008");
        controlfield.setValue("Oax3");
        notice.setControlfields(Lists.newArrayList(controlfield));

        Assertions.assertEquals("Oax3", notice.get008());
        notice.setControlfields(Lists.newArrayList());
        assertNull(notice.get008());
    }

    @Test
    void isNoticeElectronique() throws ZoneNotFoundException {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("008");
        controlfield.setValue("Aax3");
        notice.setControlfields(Lists.newArrayList(controlfield));
        Assertions.assertFalse(notice.isNoticeElectronique());

        controlfield.setTag("008");
        controlfield.setValue("Oax3");
        notice.setControlfields(Lists.newArrayList(controlfield));
        Assertions.assertTrue(notice.isNoticeElectronique());
    }

    @Test
    void isNoticeImprimee() throws ZoneNotFoundException {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("008");
        controlfield.setValue("Aax3");
        notice.setControlfields(Lists.newArrayList(controlfield));
        Assertions.assertTrue(notice.isNoticeImprimee());

        controlfield.setTag("008");
        controlfield.setValue("Oax3");
        notice.setControlfields(Lists.newArrayList(controlfield));
        Assertions.assertFalse(notice.isNoticeImprimee());
    }
    @Test
    void getPpn() {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("001");
        controlfield.setValue("143519379");
        notice.setControlfields(Lists.newArrayList(controlfield));

        Assertions.assertEquals("143519379", notice.getPpn());
    }

    @Test
    void toStringTest() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("echelle");
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("001");
        controlfield.setValue("143519379");
        notice.setControlfields(Lists.newArrayList(controlfield));
        Assertions.assertEquals("Notice {leader=echelle, ppn=143519379}", notice.toString());
    }
}
