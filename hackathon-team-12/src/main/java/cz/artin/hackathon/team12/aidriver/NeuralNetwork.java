package cz.artin.hackathon.team12.aidriver;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NeuralNetwork {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final int INPUT_SIZE = 2; // Number of inputs
  private static final int OUTPUT_SIZE = 1; // Number of outcomes
  private static final int RNG_SEED = 0; // This random-number generator applies a seed to ensure that the same initial weights are used when training.
  private static final int NUM_EPOCHS = 5000; // An epoch is a complete pass through a given dataset.

  private final MultiLayerNetwork model;

  public NeuralNetwork() {
    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
        .seed(RNG_SEED)
        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
        .iterations(1)
        // the size of changes in the network during training
        .learningRate(0.8)
        .updater(Updater.ADAM)
        .regularization(true).l2(1e-4)
        .list()
        // hidden layer
        .layer(0, new DenseLayer.Builder()
            .nIn(INPUT_SIZE) // Number of input datapoints.
            .nOut(5)        // Size of the hidden layer
            .activation(Activation.TANH)// Activation function.
            .weightInit(WeightInit.XAVIER) // Weight initialization.
            .build())
        .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
            .nIn(5)         // this should match the size of previous layer
            .nOut(OUTPUT_SIZE)
            .activation(Activation.TANH)
            .weightInit(WeightInit.XAVIER)
            .build())
        .pretrain(false).backprop(true)
        .build();

    model = new MultiLayerNetwork(conf);
    model.init();
  }

  /**
   * Loads the neural network model from file.
   *
   * @param filename
   */
  public NeuralNetwork(String filename) {
    try {
      model = ModelSerializer.restoreMultiLayerNetwork(filename);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  /**
   * Fits the neural network to the provided dataset (learns the network).
   *
   * @param ds Dataset
   */
  public void fit(DataSet ds) {
    log.info("Training model....");
    for (int i = 0; i < NUM_EPOCHS; i++) {
      model.fit(ds);
      log.info("Epoch {}/{}: {}", i, NUM_EPOCHS, model.score());
    }
  }

  /**
   * Predicts the output based on the input values.
   *
   * @param input Array of the input values
   * @return Predicted values
   */
  public INDArray predict(INDArray input) {
    return model.output(input);
  }

  /**
   * Saves the neural network model.
   *
   * @param filename
   */
  public void saveModel(String filename) {
    boolean saveUpdater = false;              //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
    try {
      ModelSerializer.writeModel(model, filename, saveUpdater);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
