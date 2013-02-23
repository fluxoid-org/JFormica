package org.cowboycoders.ant.temp;

import java.util.ArrayList;

public class ArrayListVsPrimitiveArray {
	public static void main(String[] args) {
		int big = 999999;
		long first;
		long second;
		long start = System.nanoTime();
		for (int i = 0 ; i < big ; i++) {
			ArrayList<Byte> bytes = new ArrayList<Byte>(8);
			for (int j = 0 ; j< 8 ; j++) {
				bytes.add((byte) 10);
			}
		}
		first = System.nanoTime() - start;
		
		start = System.nanoTime();
		for (int i = 0 ; i < big ; i++) {
			byte[] bytes = new byte[8];
			for (int j = 0 ; j< 8 ; j++) {
				bytes[j] = 10;
			}
		}
		second = System.nanoTime() - start;
		
		System.out.println(first / Math.pow(10, 9));
		System.out.println(second / Math.pow(10, 9));
	}
}
