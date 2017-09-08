package org.cowboycoders.ant.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntUtilsTest {

    @Test
    public void setMask() {
        int old = 0xadde;
        int mask = 0x0ff0;
        int newVal = IntUtils.setMaskedBits(old, mask, 0xcc);
        assertEquals(0xacce, newVal);
    }

    @Test
    public void setMaskMultiByte() {
        int old = 0xdeadbeef;
        int mask = 0x0f_ff_f0_00;
        int newVal = IntUtils.setMaskedBits(old, mask, 0xabcd);
        assertEquals(0xdabcdeef, newVal);
    }

    @Test
    public void setMaskMultiByte2() {
        int old = 0x10_00_00_00;
        int mask = 0x0f_ff_f0_00;
        int newVal = IntUtils.setMaskedBits(old, mask, 1234);
        assertEquals(1234, IntUtils.getFromMask(newVal, mask));
    }

    @Test
    public void bitLevel() {
        int old = 0x00_00_00_00;
        int mask = 0b1111_1111_1110_0000;
        int newVal = IntUtils.setMaskedBits(old, mask, 1234);
        assertEquals(newVal >>> 5, 1234);
        assertEquals(1234, IntUtils.getFromMask(newVal, mask));
    }

    @Test
    public void fullSet() {
        int old = 0x00_00_00_00;
        int mask = 0xff_ff_ff_ff;
        int newVal = IntUtils.setMaskedBits(old, mask, 0xdeadbeef);
        assertEquals(0xdeadbeef, IntUtils.getFromMask(newVal, mask));
    }

    @Test
    public void lowerNibble() {
        int old = 0x00_00_00_ab;
        int mask = 0xf;
        int newVal = IntUtils.setMaskedBits(old, mask, 7);
        System.out.printf("%x\n", newVal);
        assertEquals(7, IntUtils.getFromMask(newVal, mask));
    }

    @Test
    public void fullSetLE() {
        int old = 0x00_00_00_00;
        int mask = 0xff_ff_ff_ff;
        int newVal = IntUtils.setMaskedBits(old, mask, 0xdeadbeef);
        assertEquals(0xdeadbeef, IntUtils.getFromMask(newVal, mask));
    }


    @Test
    public void setGet() {
        int old = 0xdeadbeef;
        int mask = 0x0f_ff_f0_00;
        int newVal = IntUtils.setMaskedBits(old, mask, 0xabcd);
        assertEquals(0xabcd, IntUtils.getFromMask(newVal, mask));
    }

    @Test
    public void setMaskLE() {
        int old = 0;
        int mask = 0x0ffff0;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 0xcdab);
        assertEquals(0x0abcd0, newVal);
    }

    @Test
    public void setMaskLENibble() {
        int old = 0;
        int mask = 0xf;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 0xc);
        assertEquals(0xc, newVal);
    }

    @Test
    public void setMaskLEPartialByte() {
        int old = 0;
        int mask = 0x0fff0;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 0xc0ab);
        assertEquals(0x0abc0, newVal);
    }

    @Test
    public void setMaskLEPartialByteLeftAlign() {
        int old = 0;
        int mask = 0xffffff00;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 0x563412);
        assertEquals(0x12345600, newVal);
    }

    @Test
    public void setMaskLEPartialByteRightAlign() {
        int old = 0;
        int mask = 0x00ffffff;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 0x563412);
        assertEquals(0x123456, newVal);
    }

    @Test
    public void test123() {
        int old = 0;
        int mask = 0xff;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 123);
        //System.out.printf("%x\n", 123);
        //System.out.printf("%x\n", newVal);
        assertEquals(123, newVal);
    }

    @Test
    public void offsetMultiByte() {
        int old = 0;
        int mask = 0x00ffff0;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 2789);
        //int swap = (2789 & 0xff00) >>> 8;
        //swap += (2789  & 0xFF) << 8;
        //System.out.printf("%x\n", 2789);
        //System.out.printf("%x\n", swap);
        //System.out.printf("%x\n", newVal);
        //System.out.println(swap);
        assertEquals(0xe50a0, newVal);
    }

    @Test
    public void oneBit() {
        int old = 0;
        int mask = 0b0000_0010;
        int newVal = IntUtils.setMaskedBitsLE(old, mask, 1,1);
        assertEquals(0b0000_0010, newVal);
    }

    @Test
    public void maxSigned() {
        assertEquals(7, IntUtils.maxSigned(4));
    }

    @Test
    public void maxSign() {
        assertEquals(0xffffffff,IntUtils.maxUnsigned(32));
    }

}
