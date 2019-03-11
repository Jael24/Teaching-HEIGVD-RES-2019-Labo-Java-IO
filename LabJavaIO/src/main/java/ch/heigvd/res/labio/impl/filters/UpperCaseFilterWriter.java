package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Olivier Liechti
 */
public class UpperCaseFilterWriter extends FilterWriter {
  
  public UpperCaseFilterWriter(Writer wrappedWriter) {
    super(wrappedWriter);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    super.write(str.toUpperCase(),off,len);
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    for(int i = 0; i < cbuf.length; ++i) {
      if(cbuf[i] <= 122 && cbuf[i] >= 97)
        cbuf[i] = (char)(cbuf[i] - 32);
    }
    super.write(cbuf,off,len);
  }

  @Override
  public void write(int c) throws IOException {
    if(c <= 122 && c >= 97)
      super.write(c - 32);
    else
      super.write(c);
  }

}
