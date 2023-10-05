package CZ4031Project1;



public class LinearProfiler {
  long startTime;
  long endTime;

  boolean started;

  public LinearProfiler() {
    this.started = false;
  }

  public void startProfiling() {
    this.startTime = System.nanoTime();
    this.started = true;
  }

  public void endProfiling() {
    this.started = false;
    this.endTime = System.nanoTime();
  }

  public long getProfiledDurationNano() {
    return this.endTime - this.startTime;
  }
}
