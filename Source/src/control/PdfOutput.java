package control;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JScrollPane;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfOutput {

    public static void write(File path, JScrollPane table) throws FileNotFoundException, DocumentException{
        Document document = new Document(new Rectangle(table.getWidth(),table.getHeight()));
          PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        
          document.open();
          PdfContentByte cb = writer.getDirectContent();
          cb.saveState();
          
          PdfTemplate pdfTemplate = cb.createTemplate(table.getWidth(), table.getHeight());
          Graphics2D g2 = new PdfGraphics2D(cb,table.getWidth(), table.getHeight());
          table.print(g2);
          cb.addTemplate(pdfTemplate, table.getWidth(), table.getHeight());
          g2.dispose();
          
          cb.restoreState();
        
          document.close();
    }
}
