package cz.artin.hackathon.team12.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.primitives.Floats;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {

  public static void appendToCsv(String filename, float... values) {
    CharSink out = Files.asCharSink(new File(filename), Charset.defaultCharset(), FileWriteMode.APPEND);
    try {
      out.write(Joiner.on(',').join(Floats.asList(values)) + "\n");
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<float[]> loadFromCsv(String filename) {
    CharSource in = Files.asCharSource(new File(filename), Charset.defaultCharset());
    try {
      return in.readLines(new LineProcessor<List<float[]>>() {

        private List<float[]> lines = new ArrayList<>();

        @Override
        public boolean processLine(String line) throws IOException {
          List<String> strings = Splitter.on(',').splitToList(line);
          float[] floats = new float[strings.size()];
          for (int i = 0; i < strings.size(); i++) {
            floats[i] = Float.parseFloat(strings.get(i));
          }
          lines.add(floats);
          return true;
        }

        @Override
        public List<float[]> getResult() {
          return lines;
        }

      });
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static DataSet listToDataSet(List<float[]> list, int inputSampleSize, int outputSampleSize) {
    INDArray input = Nd4j.zeros(list.size(), inputSampleSize);
    INDArray output = Nd4j.zeros(list.size(), outputSampleSize);

    for (int i = 0; i < list.size(); i++) {
      float[] sample = list.get(i);

      Preconditions.checkState(sample.length == inputSampleSize + outputSampleSize, "Invalid sample count: %s", sample.length);

      for (int j = 0; j < inputSampleSize; j++) {
        input.putScalar(new int[] {i, j}, sample[j]);
      }

      for (int j = 0; j < outputSampleSize; j++) {
        output.putScalar(new int[] {i, j}, sample[inputSampleSize + j]);
      }
    }

    return new DataSet(input, output);
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException ignore) {
    }
  }

}
