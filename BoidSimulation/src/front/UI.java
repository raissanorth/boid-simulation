package front;

import java.util.ArrayList;


import back.Boid;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 * @author Raissa North
 * 
 * Inspired by Conrad Parker's explanation on http://www.kfish.org/boids/pseudocode.html#ref1, based on original description by Craig Reynolds,
 * using the three simple rules of separation, alignment, and cohesion.
 */

public class UI extends Application {
	int width = 1400, height = 800;
	float x = 100, y = 100;
	double smoothingVelocity = 30; // divided by
	double collisionNormalisationDelta = 5; // multiplied by

	// ArrayList of Boids
	ArrayList<Boid> boidList = new ArrayList<Boid>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Create Boid objects and add to boidList
		for (int i = 0; i < 150; i++){
			Boid circle1 = new Boid((float)Math.random()*width, (float)Math.random()*height, 6, 0, 0);
			circle1.setFill(Color.color(Math.random(),Math.random(),Math.random()));
			boidList.add(circle1);
		}
		
//		for (int i = 0; i < 50; i++){
//			Boid  circle1 = new Boid((float)Math.random()*width, (float)Math.random()*height, 10, 0, 0);
//			circle1.setFill(Color.color(Math.random(),Math.random(),Math.random()));
//			boidList.add(circle1);
//		}

		Group boids = new Group(); 
		BorderPane border = new BorderPane();
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(15, 12, 15, 12));

		Button bVector1 = new Button();
		bVector1.setPrefWidth(width/6);
		bVector1.setText("Turn on/off Separation Rule");

		Button bVector2 = new Button();
		bVector2.setPrefWidth(width/6);
		bVector2.setText("Turn on/off Alignment Rule");


		Button bVector3 = new Button();
		bVector3.setPrefWidth(width/6);
		bVector3.setText("Turn on/off Cohesion Rule");
		
		vbox.getChildren().addAll(bVector1, bVector2, bVector3);
		border.setRight(vbox);
		border.setCenter(boids);
		Scene scene = new Scene(border, width, height);

		// Add Boids to the Group object
		for (int i = 0; i < boidList.size(); i++) {
			Boid boid = boidList.get(i);
			boids.getChildren().add(boid);
		}

		// Setup animation
		KeyFrame frame = new KeyFrame(Duration.seconds(1.0/24.0), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// --> Draw boids.
				drawBoids();

				// --> Update boids' positions.
				updateBoids();

				/* // Boid Collision Detection
				checkCollisions(); */

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
			
			// Test individual vectors
//			boid.speed.x = (float) (boid.speed.x + vector2.getX() + vector3.getX() );
//			boid.speed.y = (float) (boid.speed.y + vector2.getY() + vector3.getY() );
			
			// Update speed, by adding sum of vectors to original speed
			boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector2.getX() + vector3.getX());
			boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector2.getY() + vector3.getY());
			
			// Add speed to current position
			double newX = mod((boid.position.getX()+boid.speed.x) , width);
			double newY = mod((boid.position.getY()+boid.speed.y) , height);

			// Reset boid's position
			Point2D newPos = new Point2D(newX,newY);
			boid.setPosition(newPos);
		}
	}
	
	/** Helper mod function */
	double mod(double x, double m) {
	    return (x%m + m)%m;
	}

	private Point2D matchVelocity(Boid b) {
		Point2D vector3 = new Point2D(0,0);
		
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
				if(b.collide(boid, 50)){
					vector2 = vector2.subtract(boid.position.subtract(b.position)).normalize().multiply(collisionNormalisationDelta); 
				}
			}
		}

		// Return vector
		return vector2;
	}
	
	private Point2D steerToCenterOfFlock(Boid b) {
		Point2D center =  new Point2D(0,0);
		
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

/*	protected void checkCollisions() {
		// Check each boid against any other boid.
		for (Boid boidA : boidList) {
			for (Boid boidB : boidList) {
				if (boidA.collide(boidB)) {
					avoidCollision(boidA, boidB);
				}
			}
		}
		for (Boid boidA : boidList) {
			boidA.collided = false;
		}
	}

	protected boolean avoidCollision(Boid boidA, Boid boidB) {
		if (boidA != boidB) {

			float newVelX1 = (boidA.speed.x * (boidA.mass - boidB.mass) + (2 * boidB.mass * boidB.speed.x))
					/ (boidA.mass + boidB.mass);
			float newVelY1 = (boidA.speed.y * (boidA.mass - boidB.mass) + (2 * boidB.mass * boidB.speed.y))
					/ (boidA.mass + boidB.mass);
			float newVelX2 = (boidB.speed.x * (boidB.mass - boidA.mass) + (2 * boidA.mass * boidA.speed.x))
					/ (boidA.mass + boidB.mass);
			float newVelY2 = (boidB.speed.y * (boidB.mass - boidA.mass) + (2 * boidA.mass * boidA.speed.y))
					/ (boidA.mass + boidB.mass);

			if (!boidA.collided) {
				boidA.setDx(newVelX1);
				boidA.setDy(newVelY1);
				boidA.collided = true;
			}
			
			if (!boidB.collided) {
				boidB.setDx(newVelX2);
				boidB.setDy(newVelY2);
				boidB.collided = true;
			}
		}
		return false;
	}
	*/

	
}
