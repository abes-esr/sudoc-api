package fr.abes.convergence.kbartws.entity.notice;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class NoticeXmlTest {

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

        Assertions.assertEquals("Oax3", notice.getTypeDocument());
        notice.setControlfields(Lists.newArrayList());
        assertNull(notice.getTypeDocument());
    }

    @Test
    void isNoticeElectronique() {
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