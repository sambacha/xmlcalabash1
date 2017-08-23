package com.xmlcalabash.util;

import net.sf.saxon.event.SourceLocationProvider;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: Nov 23, 2008
 * Time: 5:12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class XProcLocationProvider implements SourceLocationProvider {
    Hashtable<Location,Integer> locationMap;
    Hashtable<Integer,Location> idMap;
    int nextId;

    public XProcLocationProvider() {
        locationMap = new Hashtable<Location,Integer> ();
        idMap = new Hashtable<Integer,Location> ();
        nextId = 0;
    }

    public int allocateLocation(String uri) {
        return allocateLocation(uri, -1);
    }

    public int allocateLocation(String uri, int lineNumber) {
        Location loc = new Location(uri, lineNumber);
        if (locationMap.containsKey(loc)) {
            return locationMap.get(loc);
        } else {
            int id = nextId++;
            idMap.put(id,loc);
            locationMap.put(loc,id);
            return id;
        }
    }

    public String getSystemId(long locationId) {
        int locId = (int) locationId;
        if (idMap.containsKey(locId)) {
            return idMap.get(locId).systemId;
        } else {
            return null;
        }
    }

    public int getLineNumber(long locationId) {
        int locId = (int) locationId;
        if (idMap.containsKey(locId)) {
            return idMap.get(locId).lineNumber;
        } else {
            return 0;
        }
    }

    public int getColumnNumber(long locationId) {
        return 0;
    }

    private static class Location {
        final String systemId;
        final int lineNumber;
        Location(String systemId, int lineNumber) {
            this.systemId = systemId;
            this.lineNumber = lineNumber;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + lineNumber;
            result = prime * result
                    + ((systemId == null) ? 0 : systemId.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Location other = (Location) obj;
            if (lineNumber != other.lineNumber)
                return false;
            if (systemId == null) {
                if (other.systemId != null)
                    return false;
            } else if (!systemId.equals(other.systemId))
                return false;
            return true;
        }
    }
}
