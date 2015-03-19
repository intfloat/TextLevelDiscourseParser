/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */




/** 
    @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
*/

package mstparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.Iterator;

public class Alphabet implements Serializable
{
    public gnu.trove.TObjectIntHashMap map;
    public int numEntries;
    public gnu.trove.TIntObjectHashMap featureMap;
//    public HashMap<Integer, String> featureMap;
    boolean growthStopped = false;

    public Alphabet (int capacity)
    {
	this.map = new gnu.trove.TObjectIntHashMap (capacity);
	this.featureMap = new gnu.trove.TIntObjectHashMap();
	this.featureMap.clear();
	//this.map.setDefaultValue(-1);

	numEntries = 0;
    }

    public Alphabet ()
    {
	this (10000);
    }

	
    /** Return -1 if entry isn't present. */
    public int lookupIndex (Object entry)
    {
	if (entry == null) {
	    throw new IllegalArgumentException ("Can't lookup \"null\" in an Alphabet.");
	}

	int ret = map.get(entry);

	if (ret == -1 && !growthStopped) {
	    ret = numEntries;
	    map.put (entry, ret);
	    featureMap.put(ret, (String)entry);
	    numEntries++;
	}
	
	return ret;
    }

    public Object[] toArray () {
	return map.keys();
    }

    public boolean contains (Object entry)
    {
	return map.contains (entry);
    }

    public int size ()
    {
	return numEntries;
    }

    public void stopGrowth ()
    {
	growthStopped = true;
	map.compact();
    }

    public void allowGrowth ()
    {
	growthStopped = false;
    }

    public boolean growthStopped ()
    {
	return growthStopped;
    }


    // Serialization 
		
    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;

    private void writeObject (ObjectOutputStream out) throws IOException {
	out.writeInt (CURRENT_SERIAL_VERSION);
	out.writeInt (numEntries);
	out.writeObject(map);
	out.writeObject(featureMap);
	out.writeBoolean (growthStopped);
    }

    private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
	int version = in.readInt ();
	numEntries = in.readInt();
	map = (gnu.trove.TObjectIntHashMap)in.readObject();
	System.out.println("map: " + map.size());	
	featureMap = (gnu.trove.TIntObjectHashMap)in.readObject();
	System.out.println("feature map: " + featureMap.size());
//	if (featureMap.size() > 10000) {
//		for (int i=10; i<20; i++)
//			System.err.println(featureMap.get(i));
//	}
	growthStopped = in.readBoolean();
    }
	
}
