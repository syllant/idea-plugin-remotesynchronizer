package org.sylfra.idea.plugins.remotesynchronizer.synchronizing;

import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.model.SyncronizingStatsInfo;

/**
 * Used to handle synchronizing events
 */
public interface SynchronizerThreadListener
{
  public void threadStarted(SynchronizerThread thread, Runnable runnable, VirtualFile[] files);

  public void threadFinished(SynchronizerThread copierThread,
    SyncronizingStatsInfo statsInfo);

  public void threadStopped(SynchronizerThread thread, SyncronizingStatsInfo statsInfo);

  public void threadInterrupted(SynchronizerThread thread);

  public void threadResumed(SynchronizerThread thread);

  public void fileCopying(SynchronizerThread thread, String src, String dest,
    int copyType);

  public void fileDeleting(SynchronizerThread thread, String path);

  public void copyFailed(SynchronizerThread thread, Throwable t);

  public void dirDeletionFailed(SynchronizerThread syncronizerThread,
    String path);

  public void fileDeletionFailed(SynchronizerThread syncronizerThread);
}
