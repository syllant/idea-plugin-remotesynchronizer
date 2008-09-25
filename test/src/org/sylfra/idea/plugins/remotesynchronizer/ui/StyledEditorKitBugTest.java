package org.sylfra.idea.plugins.remotesynchronizer.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StyledEditorKitBugTest extends JFrame
{
  private JTabbedPane tabbedPane;
  private PrivateConsole console2;
  private PrivateConsole console1;

  public StyledEditorKitBugTest()
  {
    tabbedPane = new JTabbedPane();

    console1 = new PrivateConsole();
    tabbedPane.addTab("Target1", console1);

    console2 = new PrivateConsole();
    tabbedPane.addTab("Target2", console2);

    buildUI();
  }

  private void buildUI()
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());

    JButton button = new JButton("Start");
    button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        console1.print();
        console2.print();
      }
    });
    cp.add(button, BorderLayout.WEST);
    cp.add(tabbedPane, BorderLayout.CENTER);
  }

  public static void main(String[] args)
  {
    StyledEditorKitBugTest consoleTest = new StyledEditorKitBugTest();
    consoleTest.setSize(400, 400);
    consoleTest.setVisible(true);
  }

  private final class PrivateConsole extends JEditorPane
  {
    public PrivateConsole()
    {
      setEditorKit(new StyledEditorKit());
    }

    public boolean print()
    {
      Document doc = getDocument();
      Style baseStyle = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);
      try
      {
        int start = doc.getLength();
        doc.insertString(start, "String\n", baseStyle);
      }
      catch (BadLocationException e)
      {
        e.printStackTrace();
      }

      revalidate();
      repaint();

      return true;
    }
  }

}
