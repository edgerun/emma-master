package io.edgerun.emma.bridge;

import java.util.Collection;

/**
 * BridgingTable.
 */
public interface BridgingTable {

    void insert(BridgingTableEntry entry);

    void insert(Collection<BridgingTableEntry> entries);

    void delete(BridgingTableEntry entry);

    void delete(Collection<BridgingTableEntry> entries);

    void deleteBridge(String id);

    Collection<BridgingTableEntry> getAll();

    Collection<BridgingTableEntry> getForSource(String source);
}
