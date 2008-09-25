package org.sylfra.idea.plugins.remotesynchronizer.model;

/**
 * Store copy results,  ie. numbers of success, failures, ...
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class SyncronizingStatsInfo
{
  private int successCount;
  private int failuresCount;
  private int ignoredCount;
  private int excludedCount;
  private int deletedCount;

  public int getSuccessCount()
  {
    return successCount;
  }

  public int getFailuresCount()
  {
    return failuresCount;
  }

  public int getExcludedCount()
  {
    return excludedCount;
  }

  public int getIgnoredCount()
  {
    return ignoredCount;
  }

  public int getDeletedCount()
  {
    return deletedCount;
  }

  public void addSuccess()
  {
    successCount++;
  }

  public void addFailure()
  {
    failuresCount++;
  }

  public void addIgnored()
  {
    ignoredCount++;
  }

  public void addExcluded()
  {
    excludedCount++;
  }

  public void addDeleted()
  {
    deletedCount++;
  }

  public boolean hasFailures()
  {
    return failuresCount > 0;
  }
}
