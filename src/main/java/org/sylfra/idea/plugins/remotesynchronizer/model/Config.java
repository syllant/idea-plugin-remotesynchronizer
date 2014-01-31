package org.sylfra.idea.plugins.remotesynchronizer.model;

import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Store plugin settings
 */
public class Config implements Serializable
{
  private final static String DEFAULT_LOG_FONT_FAMILY = "Monospaced";
  private final static int DEFAULT_LOG_FONT_SIZE = 12;

  private GeneralOptions generalOptions;
  private LogOptions logOptions;
  private List<TargetMappings> targetMappings;
  private transient final List<ConfigListener> listeners;

  public Config()
  {
    targetMappings = new ArrayList<TargetMappings>();
    addDefaultTarget();

    generalOptions = new GeneralOptions();
    logOptions = new LogOptions();

    listeners = new LinkedList<ConfigListener>();
  }

  public LogOptions getLogOptions()
  {
    return logOptions;
  }

  public GeneralOptions getGeneralOptions()
  {
    return generalOptions;
  }

  public List<TargetMappings> getTargetMappings()
  {
    return targetMappings;
  }

  public void setTargetMappings(List<TargetMappings> targetMappings)
  {
    this.targetMappings = targetMappings;
  }

  public void setGeneralOptions(GeneralOptions generalOptions)
  {
    this.generalOptions = generalOptions;
  }

  public void setLogOptions(LogOptions logOptions)
  {
    this.logOptions = logOptions;
  }

  public void addConfigListener(ConfigListener l)
  {
    listeners.add(l);
  }

  public void removeConfigListener(ConfigListener l)
  {
    listeners.remove(l);
  }

  public void fireConfigChanged()
  {
    LinkedList<ConfigListener> listenersCopy = new LinkedList<ConfigListener>(listeners);
    for (ConfigListener listener : listenersCopy)
    {
      listener.configChanged(this);
    }
  }

  public TargetMappings addDefaultTarget()
  {
    TargetMappings result = new TargetMappings();
    result.setName(LabelsFactory.get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME));
    targetMappings.add(result);

    return result;
  }

  public boolean hasActiveTarget()
  {
    for (TargetMappings targetMapping : targetMappings)
    {
      if ((targetMapping).isActive())
      {
        return true;
      }
    }

    return false;
  }

  public final static class GeneralOptions implements Serializable
  {
    private boolean storeRelativePaths;
    private boolean saveBeforeCopy;
    private boolean copyOnSave;
    private boolean createMissingDirs;
    private boolean simulationMode;
    private boolean allowConcurrentRuns;

    public GeneralOptions()
    {
      storeRelativePaths = true;
      saveBeforeCopy = true;
      createMissingDirs = true;
    }

    public boolean isStoreRelativePaths()
    {
      return storeRelativePaths;
    }

    public void setStoreRelativePaths(boolean storeRelativePaths)
    {
      this.storeRelativePaths = storeRelativePaths;
    }

    public boolean isSaveBeforeCopy()
    {
      return saveBeforeCopy;
    }

    public void setSaveBeforeCopy(boolean saveBeforeCopy)
    {
      this.saveBeforeCopy = saveBeforeCopy;
    }

    public boolean isCopyOnSave()
    {
      return copyOnSave;
    }

    public void setCopyOnSave(boolean copyOnSave)
    {
      this.copyOnSave = copyOnSave;
    }

    public boolean isCreateMissingDirs()
    {
      return createMissingDirs;
    }

    public void setCreateMissingDirs(boolean createMissingDirs)
    {
      this.createMissingDirs = createMissingDirs;
    }

    public boolean isSimulationMode()
    {
      return simulationMode;
    }

    public void setSimulationMode(boolean simulationMode)
    {
      this.simulationMode = simulationMode;
    }

    public boolean isAllowConcurrentRuns()
    {
      return allowConcurrentRuns;
    }

    public void setAllowConcurrentRuns(boolean allowConcurrentRuns)
    {
      this.allowConcurrentRuns = allowConcurrentRuns;
    }
  }

  public final static class LogOptions implements Serializable
  {
    private boolean logSrcPaths;
    private boolean logExludedPaths;
    private boolean logIdenticalPaths;
    private boolean clearBeforeSynchro;
    private boolean autoPopup;
    private String logFontFamily;
    private int logFontSize;

    public LogOptions()
    {
      logFontFamily = DEFAULT_LOG_FONT_FAMILY;
      logFontSize = DEFAULT_LOG_FONT_SIZE;
      logExludedPaths = true;
      logIdenticalPaths = true;
      clearBeforeSynchro = false;
      autoPopup = true;
    }

    public boolean isLogSrcPaths()
    {
      return logSrcPaths;
    }

    public void setLogSrcPaths(boolean logSrcPaths)
    {
      this.logSrcPaths = logSrcPaths;
    }

    public boolean isLogExludedPaths()
    {
      return logExludedPaths;
    }

    public void setLogExludedPaths(boolean logExludedPaths)
    {
      this.logExludedPaths = logExludedPaths;
    }

    public String getLogFontFamily()
    {
      return logFontFamily;
    }

    public void setLogFontFamily(String logFontFamily)
    {
      this.logFontFamily = logFontFamily;
    }

    public int getLogFontSize()
    {
      return logFontSize;
    }

    public void setLogFontSize(int logFontSize)
    {
      this.logFontSize = logFontSize;
    }

    public boolean isLogIdenticalPaths()
    {
      return logIdenticalPaths;
    }

    public void setLogIdenticalPaths(boolean logIdenticalPaths)
    {
      this.logIdenticalPaths = logIdenticalPaths;
    }

    public boolean isClearBeforeSynchro()
    {
      return clearBeforeSynchro;
    }

    public void setClearBeforeSynchro(boolean clearBeforeSynchro)
    {
      this.clearBeforeSynchro = clearBeforeSynchro;
    }

    public boolean isAutoPopup()
    {
      return autoPopup;
    }

    public void setAutoPopup(boolean autoPopup)
    {
      this.autoPopup = autoPopup;
    }
  }
}
