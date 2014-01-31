package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 */
public class ConfigExternalizer extends AbstractProjectComponent
{
  private final Project project;

  protected ConfigExternalizer(Project project)
  {
    super(project);
    this.project = project;
  }

  public void write(File dest) throws IOException
  {
    Config config = RemoteSynchronizerPlugin.getInstance(project).getConfig();

    Element element = XmlSerializer.serialize(config, new SkipDefaultValuesSerializationFilters());
    Document document = new Document(element);

    OutputStreamWriter writer = null;
    try
    {
      writer = new OutputStreamWriter(new FileOutputStream(dest), "UTF-8");
      JDOMUtil.writeDocument(document, writer, System.getProperty("line.separator"));
    }
    finally
    {
      if (writer != null)
      {
        writer.close();
      }
    }
  }

  public void read(File src) throws IOException, JDOMException
  {
    Document document = JDOMUtil.loadDocument(src);
    Config config = XmlSerializer.deserialize(document, Config.class);
    RemoteSynchronizerPlugin.getInstance(project).getStateComponent().loadState(config);
  }
}
