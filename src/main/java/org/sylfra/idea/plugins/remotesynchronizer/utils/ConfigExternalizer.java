package org.sylfra.idea.plugins.remotesynchronizer.utils;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;

import java.io.*;

import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * @author <a href="mailto:sylvain.francois@kalistick.fr">Sylvain FRANCOIS</a>
 * @version $Id$
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
