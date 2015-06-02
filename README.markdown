# What it is ?

RemoteSynchronizer plugin allows you to synchronize files with remote locations. Instead of manually copying files from your project to their runtime location using external tools (command line, file explorer, ...), RemoteSynchronizer launches copies from Idea, using paths customized for project. It is also able to delete obsolete files on remote locations.

# How to use it ?

This plugin is based on paths mappings which link project files/directories and their target(s). After you have configured these paths from project settings, you just invoke one of the RemoteSynchronizer actions, using popup menu from any files selection, from the project view pane or from current editor :

* **Synchronize this**: synchronize all selected files/dirs
* **Synchronize all opened**: synchronize all files opened in editors
* **Synchronize all**: synchronize all files in the project.

First, obsolete files, i.e. files which no longer exist within the projet, will be deleted in target directories mapped with current selection. Then, selected files which are included in paths mappings will be copied if their date is different from the date of their target. All operations will be logged in dedicated consoles.

> **Tip**
>
> To synchronize a java class file, you can select the source file (from current editor, project explorer, ...) as well as the class file (c.f. samples).

## Settings
Settings are defined in *Preferences* (formerly *Project settings*), just look for "RemoteSynchronizer".

Import/Export buttons allow to backup plugin configuration using XML files. Paths may be stored as absolute or relative depending on chosen option.

## Mappings configuration
![Mapping configuration](https://github.com/syllant/idea-plugin-remotesynchronizer/raw/master/src/main/doc/screenshots/mapping-settings.png)

Paths mappings are gathered within targets.
These targets can be activated/deactived at any time. For example, you could define a target for your development server and another for a test server.
Synchronizations will be launched for all *active* targets. Each target is linked to one console at least (a new console may be created when existing console is busy with a long copy). Targets are renamed by double-clicking on tab titles.

### Synchronization mappings
Synchronizations are configured from paths mappings: these mappings link source paths to synchronize with their target paths. When a file is synchronized, the closest matching definition is used. Note that this means that you have to use different targets to synchronize a file with different locations. For a given target, a file will be copied at most one time, using the best matching mapping.

> **Tip**
>
> Concerning java classes, you have to configure output paths and not source paths. However, as written above, you may select java source files for the synchronization actions.

The third attribute of mappings defines if obsolete files should be deleted when synchronization is launched for this mapping. Files are considered as obsolete when they are found on remote locations but not in your project, according the paths mappings. *This must be used carefully* : if you aren't the only one to work on these remote locations, you could delete files produced by others !

> **Tip**
>
> You are strongly encouraged to test your configuration before launching first synchronization. RemoteSynchronizer provides a simulation mode for this. Simulation mode only logs operations which would be done in real mode, without any risk !

### Paths excluded from copy
You can prevent some files to be copied using ANT pattern. For example, to prevent java source files to be copied, you will write `**/*.java`, and for SVN files `**/.svn/*`.

### Paths excluded from deletion
You can also prevent some files to be deleted on remote location. You will also use ANT patterns.

## General settings
![General settings](https://github.com/syllant/idea-plugin-remotesynchronizer/raw/master/src/main/doc/screenshots/general-settings.png)

* *Simulation mode*: you should start by activating this option if you are not sure about your configuration. Synchronizations will be logged (in italic) so you can check what will occur, but no copy or deletion will be launched.
* *Store relative paths*: if checked, all paths will be stored as relative from directory storing IDEA project file (`.ipr`)
* *Synchronize on save*: if checked, project will be synchronized on save (could be dangerous if you saved incomplete files!).
* *Save before synchronize*: if checked, project will be saved before synchronization.
* *Create missing directories*: if checked, destination directories will be created as needed
* *Allow concurrent synchronizations*: if checked, several synchronizations may be launched in parallel

## Logs settings
![General settings](https://github.com/syllant/idea-plugin-remotesynchronizer/raw/master/src/main/doc/screenshots/log-settings.png)

* *Clear before synchronization*: if checked, console is cleared before each synchronization
* *Shows log window when synchronization starts*: if checked, console is shown when synchronization starts. This is useful when you want check all operation results.
* *Font*: font used in the console
* *Display source paths*: by default, only the destination path is shown in logs. If checked, you will see the source path too
* *Display excluded files*: if you activate this option, you will see files which are excluded from synchronization
* *Display identical files*: if you activate this option, you will see files which are already synchronized

## Samples

Using the settings from the screenshot, and considering `c:\project\classes`
being the output path for `c:\project\src` :

<table>
  <tr><th>If you select (1)</th><th>File will be copied to (1)</th></tr>
  <tr><td><code>c:\project\jsp\login\index.jsp</code></td><td><code>d:\deployed\web\jsp\login\index.jsp</code></td></tr>
  <tr><td><code>c:\project\classes\Foo.class</code></td><td><code>d:\deployed\WEB-INF\classes\Foo.class</code></td></tr>
  <tr><td><code>c:\project\src\Foo.java</code></td><td><code>d:\deployed\WEB-INF\classes\Foo.class</code> <em>(2)</em></td></tr>
  <tr><td><code>c:\project\src\Snoop.java</td><td><em>[excluded]</em></td></tr>
  <tr><td><code>c:\project\src\test\FooTest.java</td><td><em>[excluded]</em></td></tr>
  <tr><td><code>c:\project\readme.txt</code></td><td><code>d:\deployed\readme</code> <em>(3)</em></td></tr>
  <tr><td><code>c:\project\history.txt</code></td><td><code>d:\deployed\docs\history.txt</code> <em>(3)</em></td></tr>
</table>

**Comments:**

<em>(1)</em>. paths must be absolute. However, they will be stored as relative to the project file if you have checked this option in the project settings (from the paths pane)

<em>(2)</em>. a java class may be copied from source file or class file. Not that if you select source file, inner classes will be automatically copied with the main  lass but the source file itself will be copied (unless you put it in excluded paths, which you will generally do)

<em>(3)</em>. when a mapping is defined with a source path corresponding to a file and a destination path which does not end with a '/', the destination path is considered as being the destination file. With a '/' at the end, the destination path is considered as being the parent directory of the file

## Console

RemoteSynchronizer actions are always logged in the target consoles, so you can check what is being deleted or copied and where. You have to check logs console to see the copy result. Dependending on your configuration, the console pane will be automatically shown when errors occur.

Synchronizations are executed in background. Several synchronizations may be launched in parallel. You can use buttons in console pane to stop, interrupt or resume them.

Note that when a file is being copied, there's no way to cancel its copy. *The cancel button stops the copy after the current file has been copied*.

**Copy symbols:**

Symbols placed before paths give some information about current operations.
Their meaning is given at the top of the console:

* (/): destination file did exist, it was replaced
* (+): destination file did not exist, it was created
* (-): source file did not exist, destination file was deleted
* (=): destination file and source file have the same date, they are considered as being identical, file is not copied
* (?): selected file is a java source not compiled, so its class file can't be copied
* (^): selected file is not included in the settings

## Installation

Just use IDEA plugin manager.

# To do

* Option to show operation status using status bar or popup window
* Option to synchronized files continuously
* SSH support

# Credits

Thanks to Messia (messia@wanadoo.fr) for the logo icon.

Thanks to YourKit for providing a free licence for [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp).

*YourKit is kindly supporting open source projects with its full-featured Java Profiler.*
*YourKit, LLC is the creator of innovative and intelligent tools for profiling Java and .NET applications.*
*Take a look at !YourKit's leading software products: [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).*
