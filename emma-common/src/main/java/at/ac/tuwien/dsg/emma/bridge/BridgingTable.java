package at.ac.tuwien.dsg.emma.bridge;

import java.util.Collection;

/**
 * BridgingTable.
 */
public interface BridgingTable {

    void insert(BridgingTableEntry entry);

    void insert(Collection<BridgingTableEntry> entries);

    void delete(BridgingTableEntry entry);

    Collection<BridgingTableEntry> getAll();

    Collection<BridgingTableEntry> getForSource(String source);
}
