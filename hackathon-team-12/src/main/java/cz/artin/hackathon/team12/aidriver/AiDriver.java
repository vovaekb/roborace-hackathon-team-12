package cz.artin.hackathon.team12.aidriver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import cz.artin.hackathon.simulator.CarColor;
import cz.artin.hackathon.simulator.CarInfo;
import cz.artin.hackathon.simulator.Drive;
import cz.artin.hackathon.simulator.Driver;
import cz.artin.hackathon.simulator.Way;
import cz.artin.hackathon.team12.KeyboardDriver;
import cz.artin.hackathon.team12.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.PI;
import static cz.artin.hackathon.team12.aidriver.Constants.NORMALIZE_ANGLE;
import static cz.artin.hackathon.team12.aidriver.Constants.NORMALIZE_DIST;

public class AiDriver implements Driver {

  private final Logger log = LoggerFactory.getLogger(getClass());

  // filename with saved neural network
  private static final String NN_FILENAME = "nn.zip";

  private final KeyboardDriver keyboardDriver = new KeyboardDriver(1.0f, 0.5f);

  // object representing the road polygon
  private Way way;

  // if set to false, driving by key is enabled
  private boolean isAutonomousPhase = true;

  private final NeuralNetwork neuralNetwork = new NeuralNetwork(NN_FILENAME);

  @NotNull
  @Override
  public String getName() {
    return "AI Driver";
  }

  @NotNull
  @Override
  public CarColor getColor() {
    return CarColor.BLUE;
  }

  @Override
  public void enterRace(Way way) {
    this.way = way;
  }

  @NotNull
  @Override
  public Drive getDrive(CarInfo self, List<CarInfo> cars) {
    // we want to switch between autonomous phase and manual driving
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      isAutonomousPhase = !isAutonomousPhase;
      if (isAutonomousPhase) {
        log.info("Autonomous drive");
      } else {
        log.info("Manual drive");
      }
      Utils.sleep(200);
    }

    /// Get our position and calculate basic information
    Vector2 position = self.getPosition();
    int segment = way.closestSegment(position);

    float dist = way.distanceFromSegment(position, segment) * way.sideOfSegment(position, segment);
    float roadCarAngle = way.angleFromSegment(self.getAngle(), segment) / PI * 180;

    log.trace("distance={}, roadCarAngle={}", dist, roadCarAngle);

    // Get turning and speed of the car
    Drive drive;
    if (isAutonomousPhase) {
      drive = getAutonomousDrive(dist, roadCarAngle);
    } else {
      drive = keyboardDriver.getDrive(self, cars);
    }

    return drive;
  }

  private Drive getAutonomousDrive(float dist, float roadCarAngle) {
    INDArray inputs = Nd4j.create(new float[] {dist * NORMALIZE_DIST, roadCarAngle * NORMALIZE_ANGLE});
    float turning = neuralNetwork.predict(inputs).getFloat(0);
    return new Drive(0.1f, turning, false);
  }

}
