package CZ4031Project1.bptree;

import java.util.HashSet;

public class BPlusTreeProfiler {
  HashSet<Node> nodesAccessed;
  HashSet<Integer> blocksIndexesAccessed;
  long startTime;
  long endTime;

  boolean started;

  public BPlusTreeProfiler() {
    this.nodesAccessed = new HashSet<Node>();
    this.blocksIndexesAccessed = new HashSet<Integer>();
    this.started = false;
  }

  public void startProfiling() {
    this.nodesAccessed.clear();
    this.blocksIndexesAccessed.clear();
    this.startTime = System.nanoTime();
    this.started = true;
  }

  public void endProfiling() {
    this.started = false;
    this.endTime = System.nanoTime();
  }

  public void setNodeAccessed(Node node) throws IllegalStateException {
    if (!started)
      throw new IllegalStateException("Cannot set node as accessed. Profiling not started");
    this.nodesAccessed.add(node);
  }

  public void setBlockIndexAccessed(int blockIndex) {
    if (!started)
      throw new IllegalStateException("Cannot set block as accessed. Profiling not started");
    this.blocksIndexesAccessed.add(blockIndex);
  }

  public HashSet<Node> getNodesAccessed() {
    return nodesAccessed;
  }

  public HashSet<Integer> getBlocksIndexesAccessed() {
    return blocksIndexesAccessed;
  }

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }
}
