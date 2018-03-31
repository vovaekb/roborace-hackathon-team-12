package cz.artin.hackathon.team12.aidriver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import cz.artin.hackathon.simulator.*;
import cz.artin.hackathon.team12.KeyboardDriver;
import cz.artin.hackathon.team12.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;
import static cz.artin.hackathon.team12.aidriver.Constants.NORMALIZE_ANGLE;
import static cz.artin.hackathon.team12.aidriver.Constants.NORMALIZE_DIST;

public class Driver_team12 implements Driver {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // filename with saved neural network
    private static final String NN_FILENAME = "src/main/resources/Team12/nn.zip";

    private final KeyboardDriver keyboardDriver = new KeyboardDriver(1.0f, 0.5f);

    // object representing the road polygon
    private Way way;

    // if set to false, driving by key is enabled
    private boolean isAutonomousPhase = true;

    //private final NeuralNetwork neuralNetwork = new NeuralNetwork(NN_FILENAME);
    private int segment;

    private static int lastsegment=0;

    private Vector2 last_pos = new Vector2(0,0);
    private long  timestamp = System.currentTimeMillis();

    @NotNull
    @Override
    public String getName() {
        return "CrazyRider";
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


        // Get turning and speed of the car
        Drive drive;
        boolean ai = false;
        if (isAutonomousPhase) {
            if (ai) {
                // AI
                float dist = way.distanceFromSegment(position, segment) * way.sideOfSegment(position, segment);
                float roadCarAngle = way.angleFromSegment(self.getAngle(), segment) / PI * 180;
                drive = getAutonomousDrive(dist, roadCarAngle);
                log.trace("distance={}, roadCarAngle={}", dist, roadCarAngle);
            } else {
                // Driver_team12

                float x_diff = 15*cos(self.getAngle());
                float y_diff = 15*sin(self.getAngle());
                //System.out.println("dx:"+(x_diff)+"   dy:"+(y_diff));
                //System.out.println("x:"+(position.x)+"   y:"+(position.y));

                position.x = position.x + x_diff;
                position.y = position.y + y_diff;
                //System.out.println("x:"+(position.x)+"   y:"+(position.y));

                float angle = way.angleFromSegment(self.getAngle(), segment) / PI * 180;
                float angle_next2 = way.angleFromSegment(self.getAngle(), segment) / PI * 180;
                float angle_next = way.angleFromSegment(self.getAngle(), segment) / PI * 180;
                if (segment < way.getPoints().length-1) {
                    angle_next = way.angleFromSegment(self.getAngle(), segment+1) / PI * 180;
                }
                if (segment < way.getPoints().length-2) {
                    angle_next2 = way.angleFromSegment(self.getAngle(), segment+2) / PI * 180;
                }
                float dist = way.distanceFromSegment(position, segment);
                float p = dist * way.sideOfSegment(position, segment) / way.getWidth();
                float accel = 0.1f;
                //System.out.println("way.sideOfSegment(position, segment)="+way.sideOfSegment(position, segment));
/*
                if (segment != lastsegment) {
                    System.out.print("segment= "+segment);

                   // Grid grid = new Grid();
                   // grid.get().

                    float x_diff = way.segmentEnd(segment).x - way.segmentStart(segment).x;
                    float y_diff = way.segmentEnd(segment).y - way.segmentStart(segment).y;

                    float x_pred = way.segmentEnd(segment).x + x_diff;
                    float y_pred = way.segmentEnd(segment).y + y_diff;

                    float x_next = way.segmentEnd(segment+1).x;
                    float y_next = way.segmentEnd(segment+1).y;

                    if (way.segmentEnd(segment+1).x > x_pred-20 && way.segmentEnd(segment+1).x < x_pred+20 &&
                            way.segmentEnd(segment+1).y > y_pred-20 && way.segmentEnd(segment+1).y < y_pred+20) {
                        System.out.println("     -line");
                    } else {
                        System.out.println("     -curve");
                    }
                    //System.out.println("ss= "+way.segmentStart(segment)+"se= "+way.segmentEnd(segment));
                    //lastsegment = segment;
                }
*/
                // GRID
                Grid grid = new Grid(2, 50,1,self, way, cars);
                //boolean path = grid.get(0,0).getRoad();
                //System.out.println("------------------------------------------");
                /*for (int i=0; i< grid.getXSize(); i++) {
                    for (int j=0; j< grid.getYSize(); j++) {
                        System.out.print("("+i+","+j+"):"+grid.get(i,j).getRoad()+"  ");
                    }
                    System.out.println();
                }*/
                int i;
                boolean shoot=false;
                for (i=0; i < grid.getXSize(); i++) {
                    if (grid.get(i,0).getCar()) {
                        shoot = true;
                        break;
                    }
                }

                if (i < 10) {

                }

                accel=1.0f;
                float turning;
                if(Math.hypot(self.getPosition().x - way.segmentEnd(segment).x, self.getPosition().y - way.segmentEnd(segment).y) < 50) {
                    turning= (float) (2*p - angle/20 - angle_next/5 - angle_next2/50);
                } else {
                    turning= 2*p - angle/20;
                }
                //System.out.println("i="+i+"   - accel="+accel+"  turning="+turning);

                drive = new Drive(accel, turning, shoot);
            }
        } else {
            drive = keyboardDriver.getDrive(self, cars);
        }

        return drive;
    }

    private Drive getAutonomousDrive(float dist, float roadCarAngle) {
        INDArray inputs = Nd4j.create(new float[] {dist * NORMALIZE_DIST, roadCarAngle * NORMALIZE_ANGLE});
        //float turning = neuralNetwork.predict(inputs).getFloat(0);
        //return new Drive(1f, turning, false);
        return new Drive(1f, 0, false);
    }

}
