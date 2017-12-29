package app;

import javax.microedition.lcdui.*;
import javax.microedition.io.file.*;
import javax.microedition.io.Connector;
import java.io.*;
import java.util.*;
import util.*;

public class FileSystemBrowser extends List implements CommandListener {
	protected String cwd = "/";
	protected Reader midlet;
	protected Command backCommand = new Command("Back", Command.BACK, 2);
	protected Image iconDir, iconFile;
	
	protected static boolean isDirectory(String entry) {
		return entry.endsWith("/");
	}
	
	protected static class EntriesComparator implements Comparator {
		public int compare(Object objA, Object objB) {
			String a = (String)objA;
			String b = (String)objB;
			boolean aIsDir = isDirectory(a);
			boolean bIsDir = isDirectory(b);
			if(aIsDir && !bIsDir)
				return -1;
			if(bIsDir && !aIsDir)
				return 1;
			return a.compareTo(b);
		}
	}
	protected final static EntriesComparator entriesComparator = new EntriesComparator();

	protected void listFiles() {
		deleteAll();
		Enumeration content = null;
		if(cwd.equals("/")) {
			content = FileSystemRegistry.listRoots();
		} else {
			try {
				FileConnection fc = (FileConnection)Connector.open("file://" + cwd, Connector.READ);
				content = fc.list();
				fc.close();
			} catch(IOException ex) {
				midlet.fatal("Can't list directory", ex.toString());
			}
		}
		Vector vector = new Vector();
		while(content.hasMoreElements())
			vector.addElement(content.nextElement().toString());
		String[] entries = new String[vector.size()];
		vector.copyInto(entries);
		vector = null;
		SortUtils.quickSort(entries, entriesComparator);
		for(int i = 0; i < entries.length; ++i)
			append(entries[i], isDirectory(entries[i]) ? iconDir : iconFile);
	}
	
	protected void chdir(String dir) {
		cwd += dir;
	}
	
	protected void back() {
		// Drop all the trailing '/'s
		while(cwd.endsWith("/"))
			cwd = cwd.substring(0, cwd.length()-1);
		// Drop last '/'-delimited segment
		int index = cwd.lastIndexOf('/');
		if(index == -1) {
			cwd = "/";
		} else {
			cwd = cwd.substring(0, index+1);
		}
	}
	
	protected void select(String file) {
		try {
			InputStream is = Connector.openInputStream("file://" + cwd + file);
			midlet.readFile(is, file);
		} catch(IOException ex) {
			midlet.fatal("Can't open file", ex.toString());
		}
	}
	
	public FileSystemBrowser(Reader midlet) {
		super("", List.IMPLICIT);
		this.midlet = midlet;
		addCommand(List.SELECT_COMMAND);
		addCommand(backCommand);
		setCommandListener(this);
		try {
			iconDir = Image.createImage(getClass().getResourceAsStream("/dir.png"));
			iconFile = Image.createImage(getClass().getResourceAsStream("/file.png"));
		} catch(IOException ex) {
			midlet.fatal("Can't load icons", ex.toString());
		}
		listFiles();
	}
	
	public void commandAction(Command command, Displayable d) {
		if(command == List.SELECT_COMMAND) {
			if(size() > 0) {
				String entry = getString(getSelectedIndex());
				if(isDirectory(entry)) {
					chdir(entry);
					listFiles();
				} else {
					select(entry);
				}
			}
		} else if(command == backCommand) {
			back();
			listFiles();
		}
	}
}