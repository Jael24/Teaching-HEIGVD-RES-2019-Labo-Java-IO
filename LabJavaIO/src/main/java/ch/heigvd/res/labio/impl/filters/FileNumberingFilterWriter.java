package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  int numeroLigne = 1;
  boolean debutFichier = true;
  boolean attenteWindows = false;

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    String tab = "\t";
    boolean chaineAjoutee = false;
    StringBuilder result = new StringBuilder();

    if (debutFichier) {
       result.append(numeroLigne++).append(tab);
      debutFichier = false;
    }

    int longueurChaine = len + off;
    int i;

    for(i = off; i < longueurChaine; ++i) {
      // si la longueur est egale a i+1, ne va pas tester le charAt(i+1) (en vertue du &&)
      if (str.charAt(i) == '\r' && str.charAt(i+1) == '\n') {
        result.append(str.substring(off, i + 2)).append(numeroLigne++).append(tab);
        off = i + 2;
        ++i;
        if (off > len)
          --off;
        chaineAjoutee = true;
      }
      else if ((str.charAt(i) == '\n'  || (str.charAt(i) == '\r' && str.charAt(i+1) != '\n'))) {
        result.append(str.substring(off, i + 1)).append(numeroLigne++).append(tab);
        off = i + 1;
        if (off > len)
          --off;
        chaineAjoutee = true;
      }
    }

    if (str.charAt(i-1) != '\n' || !chaineAjoutee)
      result.append(str.substring(off,i));

    super.write(result.toString(),0,result.length());
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    for(int i = off; i < off+len; ++i){
        write(cbuf[i]);
    }
  }

  @Override
  public void write(int c) throws IOException {
    if(debutFichier) {
      super.write(Integer.toString(numeroLigne++) + "\t", 0, 2);
      debutFichier = false;
    }

    if(c == 10) {
        String valeur = Integer.toString(numeroLigne++);
        if (attenteWindows) {
            super.write(13);
            super.write(10);
            super.write(valeur,0,valeur.length());
            super.write(9); // 9 = '\t'
            attenteWindows = false;
            return;
        }
      super.write(c);
      super.write(valeur,0,valeur.length());
      super.write(9); // 9 = '\t'
    } else if (c == 13) {
        attenteWindows = true;
    }
    else {
        if(attenteWindows) {
            super.write(13);
            super.write(Integer.toString(numeroLigne),0,Integer.toString(numeroLigne).length());
            super.write(9); // 9 = '\t'
            attenteWindows = false;
        }
      super.write(c);
      attenteWindows = false;
    }
  }

}
