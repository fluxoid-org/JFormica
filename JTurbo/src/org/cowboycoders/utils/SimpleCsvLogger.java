/*
 *    Copyright (c) 2013, Will Szumski
 *    Copyright (c) 2013, Doug Szumski
 *
 *    This file is part of Cyclismo.
 *
 *    Cyclismo is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Cyclismo is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SimpleCsvLogger {

	private String directoryName;
	private String fileName;
	private static final Logger LOGGER = Logger.getLogger(SimpleCsvLogger.class
			.getSimpleName());
	private String headingsString;

	private Long timeOffset;

	private File directory;
	private File file;
	private boolean setupOk = false;
	private boolean writeHeadings;
	private boolean addTime = false;
	private CharSequence comment;
	private boolean started;
	private String[] userHeadings;
	private boolean append = true;
	private Map<String, Object> headingDataMap = new HashMap<String, Object>();

	/**
	 * 
	 * @param dir
	 * @param filename
	 * @param headings excluding time if added
	 */
	public SimpleCsvLogger(String dir, String filename, String... headings) {
		this.directoryName = dir;
		this.fileName = filename;
		this.addTime = true;
		userHeadings = headings;

		// setupOk = true;
		// write(HEADINGS);
	}
	
	public SimpleCsvLogger(File file, String... headings) {
		this(file.getParent() == null ? System.getProperty("user.dir") : file.getParent(), file.getName(),headings);
	}
	
	
	/**
	 * @param append append to file or delete and recreate
	 * Must be called before first update. Default false.
	 * @param addTime
	 */
	public synchronized void append(boolean append) {
		if (started)
			return;
		this.append = append;
	}
	
	/**
	 * Prefixes a time field. Default false.
	 * must be called before first update.
	 * @param addTime add a time field in seconds
	 */
	public synchronized void addTime(boolean addTime) {
		if (started)
			return;
		this.addTime = addTime;
	}
	
	/**
	 * Add comment above headings
	 * @param comment
	 */
	public synchronized void setComment(CharSequence comment) {
		this.comment = comment;
	}

	protected StringBuilder separate(Object[] values) {
		StringBuilder headingsBuilder = new StringBuilder();
		headingsBuilder.append(values[0].toString());
		for (int i = 1; i < values.length; i++) {
			headingsBuilder.append(";" + values[i].toString());
		}
		return headingsBuilder;
	}

	/**
	 * Creates a new output stream to write to the given filename.
	 * 
	 * @throws IOException
	 */
	protected PrintWriter newPrintWriter() throws IOException {
		file = new File(directory, fileName);
		return new PrintWriter(new FileWriter(file, true));
	}

	private void init() {
		directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		file = new File(directory, fileName);
		if (!append) {
			file.delete();
		}
		newLog();
	}

	public synchronized void update(Object... values) {
		if (!started) {
			init();
			started = true;
		}

		if (!setupOk) {
			LOGGER.warning("newLog not called");
			return;
		}

		if (writeHeadings) {
			this.headingsString = separate(userHeadings).toString();
			if (addTime) {
				this.headingsString = "time;" + this.headingsString;
			}
			write("\n", comment, "\n");
			writeHeadings = false;
			write(headingsString, "\n");
		}

		double currentTimeStamp;

		if (timeOffset == null) {
			timeOffset = System.nanoTime();
			currentTimeStamp = 0;
		} else {
			currentTimeStamp = (System.nanoTime() - timeOffset)
					/ Math.pow(10, 9);
		}

		StringBuilder outputText = new StringBuilder();

		if (addTime) {
			outputText.append(currentTimeStamp + ";").append(separate(values));
		} else {

			outputText.append(separate(values));
		}

		outputText.append("\n");

		write(outputText);

	}
	
	public synchronized void update(String heading, Object value) {
		if (heading == null || value == null) throw new NullPointerException("null values not allowed");
		if (!Arrays.asList(userHeadings).contains(heading)) {
			throw new IllegalArgumentException("heading not in list passed to constructor");
		}
		
		headingDataMap.put(heading, value);
		
		if (headingDataMap.size() >= userHeadings.length) {
			Object [] values = new Object [headingDataMap.size()];
			for (int i =0 ; i< userHeadings.length ; i++) {
				values[i] = headingDataMap.get(userHeadings[i]);
			}
			update(values);
			headingDataMap.clear();
			return;
		}
		
		
	}

	public synchronized void newLog() {
		// we need to write headings on next update as getters could refer to
		// stale values
		writeHeadings = true;
		setupOk = true;
	}

	private void write(CharSequence... input) {
		if (input == null) {
			LOGGER.info("ignoring null param");
			return;
		}
		StringBuilder outputText = new StringBuilder();
		for (CharSequence string : input) {
			outputText.append(string);
		}
		write(outputText);
	}

	private void write(CharSequence string) {
		PrintWriter writer = null;
		try {
			writer = newPrintWriter();
			writer.append(string);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void main(String[] args) {
		//SimpleCsvLogger log = new SimpleCsvLogger("logs", "metrics.log", "power","speed");
		File file = new File("./logs/hi.log");
		SimpleCsvLogger log = new SimpleCsvLogger(file, "power","speed");
		log.addTime(false);
		log.append(false);
		log.setComment("hi there");
		log.update("power",2.);
		log.update("power",5.);
		log.update("power",7.);
		log.update("speed",10);
	}

}