package cz.artin.hackathon.team12.aidriver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import cz.artin.hackathon.simulator.CarColor;
import cz.artin.hackathon.simulator.CarInfo;
import cz.artin.hackathon.simulator.Drive;
import cz.artin.hackathon.simulator.Driver;
import cz.artin.hackathon.simulator.Way;
import cz.artin.hackathon.team12.KeyboardDriver;
import cz.artin.hackathon.team12.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.PI;

public class DataCollectingDriver implements Driver {

  private final Logger log = LoggerFactory.getLogger(getClass());

  // datasource filename, where collected samples are saved
  private static final String DS_FILENAME = "ds.csv";

  private final KeyboardDriver keyboardDriver = new KeyboardDriver(1.0f, 0.5f);

  // object representing the road polygon
  private Way way;

  // if true, driver is collecting data in dsBuffer
  private boolean isCollectingData = true;

  private int iter = 0;

  @NotNull
  @Override
  public String getName() {
    return "Data Collecting Driver";
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

    if (Gdx.input.isKeyPressed(Keys.ENTER)) {
      isCollectingData = !isCollectingData;
      if (isCollectingData) {
        log.info("Stopped collecting data");
      } else {
        log.info("Started collecting data");
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
    Drive drive = keyboardDriver.getDrive(self, cars);
    if (isCollectingData && iter++ % 100 == 0) {
      // add new sample to the DataSet buffer
      Utils.appendToCsv(DS_FILENAME, dist * Constants.NORMALIZE_DIST, roadCarAngle * Constants.NORMALIZE_ANGLE, drive.getTurn());
    }

    return drive;
  }

}
