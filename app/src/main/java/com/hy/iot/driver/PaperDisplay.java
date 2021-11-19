package com.hy.iot.driver;

/**
 * @author huangye
 */
public class PaperDisplay extends PaperIo {

    public static final int DELAY_TIME = 50;
    public static final int PIC_WHITE = 255;
    public static final int PIC_BLACK = 254;
    public static final int PIC_Orientation = 253;
    public static final int PIC_LEFT_BLACK_RIGHT_WHITE = 249;
    public static final int PIC_UP_BLACK_DOWN_WHITE = 248;

    private byte[] DisBuffer = new byte[250 * 16];

    private static final byte[] init_data = {
            0x50, (byte) 0xAA, 0x55, (byte) 0xAA, 0x55, (byte) 0xAA, 0x11, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F, 0x0F, 0x0F, 0x0F,
            0x0F, 0x0F, 0x0F, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
    };
    private byte[] gImage_logo = new byte[4000];

    void MyRESET() {
        nRST_L();
        DELAY_mS(10);//1ms
        nRST_H();
        DELAY_mS(10);//1ms
    }

    // 30us
    void DELAY_100nS(int delaytime) {
//        for (int i = 0; i < 1000; i++) {
//
//        }
//        try {
//            Thread.sleep(0, delaytime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    // 1ms
    void DELAY_mS(int delaytime) {
//        try {
//            Thread.sleep(delaytime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    //  1s
    void DELAY_S(int delaytime) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    //  1M
    void DELAY_M(int delaytime) {
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void READBUSY() {
//        while (1) {     =1 BUSY
//            if ((P2IN & BIT2) == 0)
//                break;
//        }
//        DELAY_M(2);

        DELAY_S(5);
    }

    void FIRST_PICTURE() {
        SPI4W_WRITECOM((byte) 0x21);
        SPI4W_WRITEDATA((byte) 0x83);
        SPI4W_WRITECOM((byte) 0x22);
        SPI4W_WRITEDATA((byte) 0xC4);
    }

    public void INIT_SSD1673() {
        MyRESET();
        READBUSY();
        SPI4W_WRITECOM((byte) 0x01);       // Gate Setting
        SPI4W_WRITEDATA((byte) 0xF9);    // MUX Gate lines=250-1=249(F9H)
        SPI4W_WRITEDATA((byte) 0x00);    // B[2]:GD=0[POR](G0 is the 1st gate output channel)  B[1]:SM=0[POR](left and right gate interlaced)  B[0]:TB=0[POR](scan from G0 to G319)
        SPI4W_WRITECOM((byte) 0x3A);       // number of dummy line period   set dummy line for 50Hz frame freq
        SPI4W_WRITEDATA((byte) 0x06);    // Set 50Hz   A[6:0]=06h[POR] Number of dummy line period in term of TGate
        SPI4W_WRITECOM((byte) 0x3B);       // Gate line width   set gate line for 50Hz frame freq
        SPI4W_WRITEDATA((byte) 0x0B);    // A[3:0]=1011(78us)  Line width in us   78us*(250+6)=19968us=19.968ms
        SPI4W_WRITECOM((byte) 0x3C);          // Select border waveform for VBD
        //    SPI4W_WRITEDATA((byte)0x30);    // GS0-->GS0
        //    SPI4W_WRITEDATA((byte)0x31);    // GS0-->GS1
        //    SPI4W_WRITEDATA((byte)0x32);    // GS1-->GS0
        SPI4W_WRITEDATA((byte) 0x33);    // GS1-->GS1
        //    SPI4W_WRITEDATA((byte)0x43);    // VBD-->VSS
        //    SPI4W_WRITEDATA((byte)0x53);    // VBD-->VSH
        //    SPI4W_WRITEDATA((byte)0x63);    // VBD-->VSL
        //    SPI4W_WRITEDATA((byte)0x73);    // VBD-->HiZ

        SPI4W_WRITECOM((byte) 0x11);          // Data Entry mode
        SPI4W_WRITEDATA((byte) 0x01);    // 01 �CY decrement, X increment
        SPI4W_WRITECOM((byte) 0x44);       // set RAM x address start/end, in page 22
        SPI4W_WRITEDATA((byte) 0x00);    // RAM x address start at 00h;
        SPI4W_WRITEDATA((byte) 0x0f);    // RAM x address end at 0fh(15+1)*8->128    2D13
        SPI4W_WRITECOM((byte) 0x45);          // set RAM y address start/end, in page 22
        SPI4W_WRITEDATA((byte) 0xF9);    // RAM y address start at FAh-1;		    2D13
        SPI4W_WRITEDATA((byte) 0x00);    // RAM y address end at 00h;		    2D13

        SPI4W_WRITECOM((byte) 0x2C);       // Vcom= *(-0.02)+0.01???
        //    SPI4W_WRITEDATA((byte)0x82);    //-2.5V
        //    SPI4W_WRITEDATA((byte)0x69);    //-2V
        SPI4W_WRITEDATA((byte) 0x4B);    //-1.4V
        //    SPI4W_WRITEDATA((byte)0x50);    //-1.5V
        //    SPI4W_WRITEDATA((byte)0x37);    //-1V
        //    SPI4W_WRITEDATA((byte)0x1E);    //-0.5V

        WRITE_LUT();
        SPI4W_WRITECOM((byte) 0x21);       // Option for Display Update
        SPI4W_WRITEDATA((byte) 0x83);    // A[7]=1(Enable bypass)  A[4]=0ȫ��(value will be used as for bypass)

        DIS_IMG(PIC_WHITE);         //

        SPI4W_WRITECOM((byte) 0x21);       //
        SPI4W_WRITEDATA((byte) 0x03);    // ����ˢ�»ָ�������ǰ��2��ͼ�Ƚ�
        SPI4W_WRITECOM((byte) 0x3C);       // Select border waveform for VBD
        SPI4W_WRITEDATA((byte) 0x73);    // VBD-->HiZ  ����ˢ��ʱBorder���Ǹ���

    }

    void WRITE_LUT() {
        byte i;
        SPI4W_WRITECOM((byte) 0x32);//write LUT register
        for (i = 0; i < 29; i++) {
            SPI4W_WRITEDATA(init_data[i]);//write LUT register
        }
    }

    void Init_buff() {
        int i;
        for (i = 0; i < 4000; i++) {
            DisBuffer[i] = gImage_logo[i];
        }
    }

    void DIS_IMG(int num) {
        int row, col;
        int pcnt;

        SPI4W_WRITECOM((byte) 0x4E);
        SPI4W_WRITEDATA((byte) 0x00);  // set RAM x address count to 0;
        SPI4W_WRITECOM((byte) 0x4F);
        SPI4W_WRITEDATA((byte) 0xF9);  // set RAM y address count to 250-1;	2D13
        DELAY_S(5);
        SPI4W_WRITECOM((byte) 0x24);
        DELAY_S(5);
        pcnt = 0;
        for (col = 0; col < 250; col++) {  // send 128x250bits ram 2D13
            for (row = 0; row < 16; row++) { // 128 SOURCE1bit,128/8=16
                switch (num) {
                    case 1:
                        SPI4W_WRITEDATA(DisBuffer[pcnt]);
                        break;
                    case 2:
                        SPI4W_WRITEDATA(gImage_logo[pcnt]);
                        break;
                    case PIC_WHITE:
                        SPI4W_WRITEDATA((byte) 0xff);
                        break;
                    default:
                        break;
                }
                pcnt++;
            }
        }
        SPI4W_WRITECOM((byte) 0x22);
        SPI4W_WRITEDATA((byte) 0xC7);    // (Enable Clock Signal, Enable CP) (Display update,Disable CP,Disable Clock Signal)
        //  SPI4W_WRITEDATA((byte)0xF7);    // (Enable Clock Signal, Enable CP, Load Temperature value, Load LUT) (Display update,Disable CP,Disable Clock Signal)
        SPI4W_WRITECOM((byte) 0x20);
        DELAY_mS(1);
        READBUSY();
        DELAY_S(DELAY_TIME);
    }

    void SetpointXY(int xs, int xe, int ys, int ye) {
        SPI4W_WRITECOM((byte) 0x44);//set RAM x address start/end, in page 36
        SPI4W_WRITEDATA((byte) xs);//RAM x address start at 00h;
        SPI4W_WRITEDATA((byte) xe);//RAM x address end at 11h(17)->72: [because 1F(31)->128 and 12(18)->76]

        SPI4W_WRITECOM((byte) 0x45);//set RAM y address start/end, in page 37
        SPI4W_WRITEDATA((byte) ys);//RAM y address start at 00h;
        SPI4W_WRITEDATA((byte) ye);//RAM y address start at ABh(171)->172: [because B3(179)->180]

        SPI4W_WRITECOM((byte) 0x4E);//set RAM x address count to 0;
        SPI4W_WRITEDATA((byte) xs);
        SPI4W_WRITECOM((byte) 0x4F);//set RAM y address count to 0;
        SPI4W_WRITEDATA((byte) ye);

        SPI4W_WRITECOM((byte) 0x24);
    }

    void enterdeepsleep() {
        SPI4W_WRITECOM((byte) 0x10);
        SPI4W_WRITEDATA((byte) 0x01);
    }

    void SPI4W_WRITECOM(byte INIT_COM) {
        int TEMPCOM;
        int scnt;
        TEMPCOM = INIT_COM;
        nCS_H();
        nCS_L();
        SCLK_L();
        nDC_L();
        for (scnt = 0; scnt < 8; scnt++) {
            if ((TEMPCOM & (byte) 0x80) != 0) {
                SDA_H();
            } else {
                SDA_L();
            }
            DELAY_100nS(10);
            SCLK_H();
            DELAY_100nS(10);
            SCLK_L();
            TEMPCOM = TEMPCOM << 1;
            DELAY_100nS(10);
        }
        nCS_H();
    }

    void SPI4W_WRITEDATA(byte INIT_DATA) {
        int TEMPCOM;
        int scnt;
        TEMPCOM = INIT_DATA;
        nCS_H();
        nCS_L();
        SCLK_L();
        nDC_H();
        for (scnt = 0; scnt < 8; scnt++) {
            if ((TEMPCOM & (byte) 0x80) != 0) {
                SDA_H();
            } else {
                SDA_L();
            }
            DELAY_100nS(10);
            SCLK_H();
            DELAY_100nS(10);
            SCLK_L();
            TEMPCOM = TEMPCOM << 1;
            DELAY_100nS(10);
        }
        nCS_H();
    }

}
