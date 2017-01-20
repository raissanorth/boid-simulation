package front;

import java.util.ArrayList;

import back.Boid;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 * @author Raissa North
 * 
 *         Inspired by Conrad Parker's explanation on
 *         http://www.kfish.org/boids/pseudocode.html#ref1, based on original
 *         description by Craig Reynolds, using the three simple rules of
 *         separation, alignment, and cohesion.
 */

public class UI extends Application {

	private Boolean vector1state = true;
	private Boolean vector2state = true;
	private Boolean vector3state = true;

	int width = 1400, height = 800;
	float x = 100, y = 100;
	double smoothingVelocity = 50; // divided by
	double collisionNormalisationDelta = 6; // multiplied by

	// ArrayList of Boids
	ArrayList<Boid> boidList = new ArrayList<Boid>();

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Create Boid objects and add to boidList
		for (int i = 0; i < 150; i++) {
			Boid circle1 = new Boid((float) Math.random() * width, (float) Math.random() * height, 6, 0, 0);
			circle1.setFill(Color.color(Math.random(), Math.random(), Math.random()));
			boidList.add(circle1);
		}

		Group boids = new Group();
		Pane inner = new Pane();
		inner.setStyle("-fx-background-color: black");
		ToolBar bar = new ToolBar();

		BorderPane border = new BorderPane(inner);
		border.setTop(bar);

		Button bVector1 = new Button();
		bVector1.setPrefWidth(width / 7);
		bVector1.setText("Turn off Cohesion Rule");

		Button bVector2 = new Button();
		bVector2.setPrefWidth(width / 7);
		bVector2.setText("Turn off Separation Rule");

		Button bVector3 = new Button();
		bVector3.setPrefWidth(width / 7);
		bVector3.setText("Turn off Alignment Rule");

		Text status = new Text("Separation, alignment, and cohesion turned on");

		bar.getItems().addAll(bVector1, bVector2, bVector3, new Separator(), status);
		inner.getChildren().add(boids);
		Scene scene = new Scene(border, width, height);

		// Add Boids to the Group object
		for (int i = 0; i < boidList.size(); i++) {
			Boid boid = boidList.get(i);
			boids.getChildren().add(boid);
		}

		bVector1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (vector1state) {
					vector1state = false;
					status.setText("Separation, and alignment turned on. Cohesion turned off.");
					bVector1.setText("Turn on Cohesion Rule");
				} else {
					vector1state = true;
					status.setText("Separation, alignment, and cohesion turned on.");
					bVector1.setText("Turn off Cohesion Rule");
				}
			}
		});

		bVector2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (vector2state) {
					vector2state = false;
					status.setText("Alignment, and cohesion turned on. Separation turned off.");
					bVector2.setText("Turn on Separation Rule");
				} else {
					vector2state = true;
					status.setText("Separation, alignment, and cohesion turned on.");
					bVector2.setText("Turn off Separation Rule");
				}

			}
		});

		bVector3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (vector3state) {
					vector3state = false;
					status.setText("Separation, and cohesion turned on. Alignment turned off.");
					bVector3.setText("Turn on Alignment Rule");
				} else {
					vector3state = true;
					status.setText("Separation, alignment, and cohesion turned on.");
					bVector3.setText("Turn off Alignment Rule");
				}
			}
		});

		// Setup animation
		KeyFrame frame = new KeyFrame(Duration.seconds(1.0 / 24.0), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// --> Draw boids.
				drawBoids();

				// --> Update boids' positions.
				updateBoids();

				/*
				 * // Boid Collision Detection checkCollisions();
				 */

				// --> Wall Collision Detection
				for (int i = 0; i < boidList.size(); i++) {
					Boid colorCircle = boidList.get(i);
					wallCollisionDetecter(colorCircle, scene);
				}
			}
		});

		TimelineBuilder.create().cycleCount(javafx.animation.Animation.INDEFINITE).keyFrames(frame).build().play();

		primaryStage.setTitle("Animation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void drawBoids() {
		for (Boid boid : boidList) {
			boid.setTranslateX(boid.getTranslateX() + boid.getDx());
			boid.setTranslateY(boid.getTranslateY() + boid.getDy());
		}
	}

	private void updateBoids() {
		for (Boid boid : boidList) {

			Point2D vector1 = steerToCenterOfFlock(boid); // 1st rule cohesion
			Point2D vector2 = collisionAvoidance(boid); // 2nd rule separation
			Point2D vector3 = matchVelocity(boid); // 3rd rule alignment

			if (vector1state) {
				if (vector2state) {
					// vector 1 + vector 2 + vector 3
					if (vector3state) {
						// Update speed, by adding sum of vectors to original
						// speed
						boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector2.getX() + vector3.getX());
						boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector2.getY() + vector3.getY());
					}
					// vector 1 + vector 2 (NO vector3)
					else {
						boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector2.getX());
						boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector2.getY());
					}
				} else {
					// vector 1 + vector 3 (NO vector 2)
					if (vector3state) {
						// Update speed, by adding sum of vectors to original
						// speed
						boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector3.getX());
						boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector3.getY());
					}
					// vector 1 (NO vector 3) (NO vector 2)
					else {
						boid.speed.x = (float) (boid.speed.x + vector1.getX());
						boid.speed.y = (float) (boid.speed.y + vector1.getY());
					}
				}
			}

			else {
				if (vector2state) {
					// vector 2 + vector 3 (NO vector 1)
					if (vector3state) {
						boid.speed.x = (float) (boid.speed.x + vector2.getX() + vector3.getX());
						boid.speed.y = (float) (boid.speed.y + vector2.getY() + vector3.getY());
					}
					// vector 2 (NO vector 3) (NO vector 1)
					else {
						boid.speed.x = (float) (boid.speed.x + vector2.getX());
						boid.speed.y = (float) (boid.speed.y + vector2.getY());
					}
				} else {
					// vector 3 (NO vector 1) (NO vector 2)
					if (vector3state) {
						boid.speed.x = (float) (boid.speed.x + vector3.getX());
						boid.speed.y = (float) (boid.speed.y + vector3.getY());
					}
					// (NO vector 1) (NO vector 2) (NO vector 3)
					else {
						boid.speed.x = boid.speed.x;
						boid.speed.y = boid.speed.y;
					}
				}
			}

			// Add speed to current position
			double newX = mod((boid.position.getX() + boid.speed.x), width);
			double newY = mod((boid.position.getY() + boid.speed.y), height);

			// Reset boid's position
			Point2D newPos = new Point2D(newX, newY);
			boid.setPosition(newPos);
		}
	}

	/** Helper mod function */
	double mod(double x, double m) {
		return (x % m + m) % m;
	}

	private Point2D matchVelocity(Boid b) {
		Point2D vector3 = new Point2D(0, 0);

		// Sum speed of all Boids
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {
				vector3 = vector3.add(boid.speed.x, boid.speed.y);
			}
		}

		// Divide by number of all Boids (-myself) and smoothe
		double v3x = ((vector3.getX() / (boidList.size() - 1)) - b.speed.x) / smoothingVelocity;
		double v3y = ((vector3.getY() / (boidList.size() - 1)) - b.speed.y) / smoothingVelocity;

		// Return vector
		return new Point2D(v3x, v3y);
	}

	private Point2D collisionAvoidance(Boid b) {
		Point2D vector2 = new Point2D(0, 0);

		// Check if colliding with any other boid
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {

				// collision (including offset) detected
				if (b.collide(boid, 50)) {
					vector2 = vector2.subtract(boid.position.subtract(b.position)).normalize()
							.multiply(collisionNormalisationDelta);
				}
			}
		}

		// Return vector
		return vector2;
	}

	private Point2D steerToCenterOfFlock(Boid b) {
		Point2D center = new Point2D(0, 0);

		// Sum center of each boid
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {
				Point2D currentBoid = new Point2D(boid.getCenterX() + boid.getTranslateX() + boid.getRadius(),
						boid.getCenterY() + boid.getTranslateY() + boid.getRadius());
				center = center.add(currentBoid);
			}
		}

		// This boid's position
		Point2D bPosintion = new Point2D(b.getCenterX() + b.getTranslateX() + b.getRadius(),
				b.getCenterY() + b.getTranslateY() + b.getRadius());

		// Steer boid a percent into center
		double v3x = ((center.getX() / (boidList.size() - 1)) - bPosintion.getX()) / 100;
		double v3y = ((center.getY() / (boidList.size() - 1)) - bPosintion.getY()) / 100;

		// Return vector
		return new Point2D(v3x, v3y);
	}

	private void wallCollisionDetecter(Boid b, Scene scene) {
		// Top wall collision
		if (b.getCenterX() + b.getTranslateX() < b.getRadius()) {
			if (b.getDx() < 0) {
				b.setDx(-b.getDx());
			}
		}
		// Bottom wall collision
		else if (b.getCenterX() + b.getTranslateX() + b.getRadius() > scene.getWidth()) {
			if (b.getDx() > 0) {
				b.setDx(-b.getDx());
			}
		}
		// Left wall collision
		else if (b.getCenterY() + b.getTranslateY() < b.getRadius()) {
			if (b.getDy() < 0) {
				b.setDy(-b.getDy());
			}
		}
		// Right wall collision
		else if (b.getCenterY() + b.getTranslateY() + b.getRadius() > scene.getHeight()) {
			if (b.getDy() > 0) {
				b.setDy(-b.getDy());
			}
		}
	}

	public static void main(String[] args) {
		Application.launch();
	}
}
