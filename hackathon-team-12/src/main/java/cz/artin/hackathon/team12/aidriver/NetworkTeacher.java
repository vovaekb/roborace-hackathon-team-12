package cz.artin.hackathon.team12.aidriver;

import cz.artin.hackathon.team12.utils.Utils;
import org.nd4j.linalg.dataset.DataSet;

import java.util.List;

/**
 * @author pavel.cernocky@artin.cz
 */
public class NetworkTeacher {

  // datasource filename, from where collected samples are loaded
  private static final String DS_FILENAME = "ds.csv";

  // filename with saved neural network
  private static final String NN_FILENAME = "nn.zip";

  public static void main(String[] args) {
    List<float[]> samples = Utils.loadFromCsv(DS_FILENAME);
    DataSet dataset = Utils.listToDataSet(samples, 2, 1);

    NeuralNetwork neuralNetwork = new NeuralNetwork();
    neuralNetwork.fit(dataset);
    neuralNetwork.saveModel(NN_FILENAME);
  }

}
